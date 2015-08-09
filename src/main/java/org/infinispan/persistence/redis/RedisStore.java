package org.infinispan.persistence.redis;

import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.persistence.redis.client.RedisCacheEntry;
import org.infinispan.persistence.redis.client.RedisConnection;
import org.infinispan.persistence.redis.client.RedisConnectionPool;
import org.infinispan.persistence.redis.client.RedisConnectionPoolFactory;
import org.infinispan.persistence.redis.configuration.RedisStoreConfiguration;
import org.infinispan.commons.configuration.ConfiguredBy;
import org.infinispan.filter.KeyFilter;
import org.infinispan.marshall.core.MarshalledEntry;
import org.infinispan.persistence.TaskContextImpl;
import org.infinispan.persistence.spi.*;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import java.util.concurrent.Executor;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
@ConfiguredBy(RedisStoreConfiguration.class)
final public class RedisStore implements AdvancedLoadWriteStore
{
    private static final Log log = LogFactory.getLog(RedisStore.class, Log.class);

    private InitializationContext ctx;
    private RedisConnectionPool connectionPool;

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
        this.connectionPool = RedisConnectionPoolFactory.factory(this.ctx.getConfiguration(), this.ctx.getMarshaller());
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

        if (null != this.connectionPool) {
            this.connectionPool.shutdown();
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
        RedisStore.log.debug("Iterating Redis store entries");

        final InitializationContext ctx = this.ctx;
        final TaskContext taskContext = new TaskContextImpl();
        final RedisConnectionPool connectionPool = this.connectionPool;
        final RedisStore cacheStore = this;
        RedisConnection connection = connectionPool.getConnection();

        try {
            for (Object key : connection.scan()) {
                if (taskContext.isStopped()) {
                    break;
                }

                if (null != filter && ! filter.accept(key)) {
                    continue;
                }

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MarshalledEntry marshalledEntry;
                            if (fetchValue) {
                                marshalledEntry = cacheStore.load(key);
                            }
                            else {
                                marshalledEntry = ctx.getMarshalledEntryFactory().newMarshalledEntry(
                                    key, null, (ByteBuffer) null);
                            }

                            if (null != marshalledEntry) {
                                task.processEntry(marshalledEntry, taskContext);
                            }
                        }
                        catch (Exception ex) {
                            RedisStore.log.error("Failed to process the redis store key", ex);
                            throw new PersistenceException(ex);
                        }
                    }
                });
            }
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to process the redis store keys", ex);
            throw new PersistenceException(ex);
        }
        finally {
            if (null != connection) {
                connection.release();
            }
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
        // Nothing to do. All expired entries are purged by Redis itself
    }

    /**
     * Returns the number of elements in the store.
     *
     * @throws PersistenceException in case of an error, e.g. communicating with the external storage
     */
    @Override
    public int size()
    {
        RedisStore.log.debug("Calculating Redis store size");
        RedisConnection connection = null;

        try {
            connection = this.connectionPool.getConnection();
            long dbSize = connection.dbSize();

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
        finally {
            if (null != connection) {
                connection.release();
            }
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
        RedisStore.log.debug("Clearing Redis store");
        RedisConnection connection = null;

        try {
            connection = this.connectionPool.getConnection();
            connection.flushDb();
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to clear all elements in the redis store", ex);
            throw new PersistenceException(ex);
        }
        finally {
            if (null != connection) {
                connection.release();
            }
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
        RedisStore.log.debug("Loading entry from Redis store");
        RedisConnection connection = null;

        try {
            connection = this.connectionPool.getConnection();
            RedisCacheEntry cacheEntry = connection.get(key);

            if (null == cacheEntry) {
                return null;
            }

            byte[] value = cacheEntry.getValueBytes();
            byte[] metadata = cacheEntry.getMetadataBytes();
            ByteBuffer valueBuf = null;
            ByteBuffer metadataBuf = null;

            if (null != value) {
                valueBuf = this.ctx.getByteBufferFactory().newByteBuffer(value, 0, value.length);
            }

            if (null != metadata) {
                metadataBuf = this.ctx.getByteBufferFactory().newByteBuffer(metadata, 0, metadata.length);
            }

            return this.ctx.getMarshalledEntryFactory().newMarshalledEntry(key, valueBuf, metadataBuf);
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to load element from the redis store", ex);
            throw new PersistenceException(ex);
        }
        finally {
            if (null != connection) {
                connection.release();
            }
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
        RedisStore.log.debug("Writing entry to Redis store");
        RedisConnection connection = null;

        try {
            byte[] value = null;
            byte[] metadata = null;

            if (null != marshalledEntry.getValueBytes()) {
                value = marshalledEntry.getValueBytes().getBuf();
            }

            if (null != marshalledEntry.getMetadataBytes()) {
                metadata = marshalledEntry.getMetadataBytes().getBuf();
            }

            connection = this.connectionPool.getConnection();
            connection.set(marshalledEntry.getKey(), new RedisCacheEntry(value, metadata));

            if (null != metadata) {
                connection.expireAt(marshalledEntry.getKey(), marshalledEntry.getMetadata().expiryTime());
            }
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to write element to the redis store", ex);
            throw new PersistenceException(ex);
        }
        finally {
            if (null != connection) {
                connection.release();
            }
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
        RedisStore.log.debug("Deleting entry from Redis store");
        RedisConnection connection = null;

        try {
            connection = this.connectionPool.getConnection();
            return connection.delete(key);
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to delete element from the redis store", ex);
            throw new PersistenceException(ex);
        }
        finally {
            if (null != connection) {
                connection.release();
            }
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
        RedisStore.log.debug("Checking store for Redis entry");
        RedisConnection connection = null;

        try {
            connection = this.connectionPool.getConnection();
            return connection.exists(key);
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to discover if element is in the redis store", ex);
            throw new PersistenceException(ex);
        }
        finally {
            if (null != connection) {
                connection.release();
            }
        }
    }
}
