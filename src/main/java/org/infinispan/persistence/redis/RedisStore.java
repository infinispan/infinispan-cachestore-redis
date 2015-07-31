package org.infinispan.persistence.redis;

import com.lambdaworks.redis.RedisClusterConnection;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.cluster.RedisClusterClient;
import com.lambdaworks.redis.output.KeyStreamingChannel;
import org.infinispan.metadata.InternalMetadata;
import org.infinispan.persistence.redis.configuration.RedisServerConfiguration;
import org.infinispan.persistence.redis.configuration.RedisStoreConfiguration;
import org.infinispan.commons.configuration.ConfiguredBy;
import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.filter.KeyFilter;
import org.infinispan.marshall.core.MarshalledEntry;
import org.infinispan.persistence.TaskContextImpl;
import org.infinispan.persistence.spi.*;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;
import org.redisson.Config;
import org.redisson.Redisson;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import net.jcip.annotations.ThreadSafe;
import org.redisson.core.RBucket;

@ThreadSafe
@ConfiguredBy(RedisStoreConfiguration.class)
final public class RedisStore implements AdvancedLoadWriteStore
{
    private static final Log log = LogFactory.getLog(RedisStore.class, Log.class);

    private InitializationContext ctx;
    private RedisClusterClient client;
    private ObjectCodec codec;

    /**
     * Used to initialize a cache loader.  Typically invoked by the {@link org.infinispan.persistence.manager.PersistenceManager}
     * when setting up cache loaders.
     *
     * @throws PersistenceException in case of an error, e.g. communicating with the external storage
     */
    @Override
    public void init(InitializationContext ctx)
    {
        RedisStore.log.info("Redis cache store initialising");

        this.ctx = ctx;
        this.codec = new ObjectCodec(this.ctx.getMarshaller());
        RedisStoreConfiguration configuration = this.ctx.getConfiguration();

        List<RedisURI> clusterNodes = new ArrayList<RedisURI>();

        try {
            for (RedisServerConfiguration server : configuration.servers()) {
                clusterNodes.add(RedisURI.create(new URI(
                    (server.ssl() ? "rediss" : "redis"),
                    configuration.password(),
                    server.host(),
                    server.port(),
                    String.valueOf(configuration.database()),
                    null,
                    null
                )));
            }
        }
        catch(URISyntaxException ex) {
            throw new PersistenceException(ex);
        }

        this.client = new RedisClusterClient(clusterNodes);
    }

    /**
     * Invoked on component start
     */
    @Override
    public void start()
    {
        RedisStore.log.info("Redis cache store starting");
    }

    /**
     * Invoked on component stop
     */
    @Override
    public void stop()
    {
        RedisStore.log.info("Redis cache store stopping");

        if (null != this.client) {
            this.client.shutdown();
        }
    }

    /**
     * Iterates in parallel over the entries in the storage using the threads from the <b>executor</b> pool. For each
     * entry the {@link CacheLoaderTask#processEntry(MarshalledEntry, TaskContext)} is
     * invoked. Before passing an entry to the callback task, the entry should be validated against the <b>filter</b>.
     * Implementors should build an {@link TaskContext} instance (implementation) that is fed to the {@link
     * CacheLoaderTask} on every invocation. The {@link CacheLoaderTask} might invoke {@link
     * org.infinispan.persistence.spi.AdvancedCacheLoader.TaskContext#stop()} at any time, so implementors of this method
     * should verify TaskContext's state for early termination of iteration. The method should only return once the
     * iteration is complete or as soon as possible in the case TaskContext.stop() is invoked.
     *
     * @param filter        to validate which entries should be feed into the task. Might be null.
     * @param task          callback to be invoked in parallel for each stored entry that passes the filter check
     * @param executor      an external thread pool to be used for parallel iteration
     * @param fetchValue    whether or not to fetch the value from the persistent store. E.g. if the iteration is
     *                      intended only over the key set, no point fetching the values from the persistent store as
     *                      well
     * @param fetchMetadata whether or not to fetch the metadata from the persistent store. E.g. if the iteration is
     *                      intended only ove the key set, then no pint fetching the metadata from the persistent store
     *                      as well
     * @throws PersistenceException in case of an error, e.g. communicating with the external storage
     */
    @Override
    public void process(
        final KeyFilter filter,
        final CacheLoaderTask task,
        Executor executor,
        boolean fetchValue,
        boolean fetchMetadata
    )
    {
        final InitializationContext ctx = this.ctx;
        final TaskContext taskContext = new TaskContextImpl();

        try {
            RedisClusterConnection<Object,Object> connection = this.client.connectCluster(this.codec);
            connection.keys(new KeyStreamingChannel<Object>()
            {
                @Override
                public void onKey(final Object key)
                {
                    if (taskContext.isStopped()) {
                        throw new IllegalStateException();
                    }

                    if (null != key) {
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (filter.accept(key)) {
                                        Object value = null;

                                        if (fetchValue) {
                                            value = connection.get(key);
                                        }

                                        task.processEntry(
                                            ctx.getMarshalledEntryFactory().newMarshalledEntry(key, value, null),
                                            taskContext
                                        );
                                    }
                                } catch (Exception ex) {
                                    RedisStore.log.error("Failed to process the redis store key", ex);
                                    throw new PersistenceException(ex);
                                }
                            }
                        });
                    }
                }
            }, "*");
        }
        catch(IllegalStateException ex) {
            // Break out exception type for aborting processing
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to process the redis store keys", ex);
            throw new PersistenceException(ex);
        }
    }

    /**
     * Using the thread in the pool, remove all the expired data from the persistence storage. For each removed entry,
     * the supplied listener is invoked.
     *
     * @throws PersistenceException in case of an error, e.g. communicating with the external storage
     */
    @Override
    public void purge(Executor executor, final PurgeListener purgeListener)
    {
        // Nothing to purge, redis is set to expire data itself
    }

    /**
     * Returns the number of elements in the store.
     *
     * @throws PersistenceException in case of an error, e.g. communicating with the external storage
     */
    @Override
    public int size()
    {
        try {
            RedisClusterConnection<Object,Object> connection = this.client.connectCluster(this.codec);
            long dbSize = connection.dbsize();

            // Can't return more than Integer.MAX_VALUE due to interface limitation
            // If the number of elements in redis is more than the int max size,
            // log the anomaly and return the int max size
            if (dbSize > Integer.MAX_VALUE) {
                RedisStore.log.info(
                    String.format("Redis store is holding more elements than we can count! " +
                            "Total number of elements found %d. Limited to returning count as %d",
                        dbSize, Integer.MAX_VALUE
                    )
                );

                return Integer.MAX_VALUE;
            }
            else {
                return (int) dbSize;
            }
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to fetch element count from the redis store", ex);
            throw new PersistenceException(ex);
        }
    }

    /**
     * Removes all the data from the storage.
     *
     * @throws PersistenceException in case of an error, e.g. communicating with the external storage
     */
    @Override
    public void clear()
    {
        try {
            RedisClusterConnection<Object,Object> connection = this.client.connectCluster(this.codec);
            connection.flushdb();
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to clear all elements in the redis store", ex);
            throw new PersistenceException(ex);
        }
    }

    /**
     * Fetches an entry from the storage. If a {@link MarshalledEntry} needs to be created here, {@link
     * org.infinispan.persistence.spi.InitializationContext#getMarshalledEntryFactory()} and {@link
     * InitializationContext#getByteBufferFactory()} should be used.
     *
     * @return the entry, or null if the entry does not exist
     * @throws PersistenceException in case of an error, e.g. communicating with the external storage
     */
    @Override
    public MarshalledEntry load(Object key)
    {
        try {
            RedisClusterConnection<Object,Object> connection = this.client.connectCluster(this.codec);
            Object value = connection.get(key);

            if (null == value) {
                return null;
            }
            else {
                return this.ctx.getMarshalledEntryFactory().newMarshalledEntry(key, value, null);
            }
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to load element from the redis store", ex);
            throw new PersistenceException(ex);
        }
    }

    /**
     * Persists the entry to the storage.
     *
     * @throws PersistenceException in case of an error, e.g. communicating with the external storage
     * @see MarshalledEntry
     */
    @Override
    public void write(MarshalledEntry marshalledEntry)
    {
        try {
            RedisClusterConnection<Object,Object> connection = this.client.connectCluster(this.codec);
            connection.set(marshalledEntry.getKey(), marshalledEntry.getValue());
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to write element to the redis store", ex);
            throw new PersistenceException(ex);
        }
    }

    /**
     * Delete the entry for the given key from the store
     *
     * @return true if the entry existed in the persistent store and it was deleted.
     * @throws PersistenceException in case of an error, e.g. communicating with the external storage
     */
    @Override
    public boolean delete(Object key)
    {
        try {
            RedisClusterConnection<Object,Object> connection = this.client.connectCluster(this.codec);
            return connection.del(key) > 0;
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to delete element from the redis store", ex);
            throw new PersistenceException(ex);
        }
    }

    /**
     * Returns true if the storage contains an entry associated with the given key.
     *
     * @return True if the cache contains the key specified
     * @throws PersistenceException in case of an error, e.g. communicating with the external storage
     */
    @Override
    public boolean contains(Object key)
    {
        try {
            RedisClusterConnection<Object,Object> connection = this.client.connectCluster(this.codec);
            return connection.exists(key);
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to discover if element is in the redis store", ex);
            throw new PersistenceException(ex);
        }
    }
}
