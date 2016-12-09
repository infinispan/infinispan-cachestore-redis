package org.infinispan.persistence.redis;

import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.persistence.redis.client.*;
import org.infinispan.persistence.redis.configuration.RedisStoreConfiguration;
import org.infinispan.commons.configuration.ConfiguredBy;
import org.infinispan.filter.KeyFilter;
import org.infinispan.marshall.core.MarshalledEntry;
import org.infinispan.persistence.TaskContextImpl;
import org.infinispan.persistence.spi.*;
import org.infinispan.util.logging.Log;
import org.infinispan.util.logging.LogFactory;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
@ConfiguredBy(RedisStoreConfiguration.class)
final public class RedisStore implements AdvancedLoadWriteStore
{
    /**
     * The instances of this class, keyed by Infinispan cache name.
     */
    private static Map<String, RedisStore> instances = new Hashtable<>();
    
    
    /**
     * Returns the {@link #init initialised} instances of this class.
     *
     * @return The instances of this class as an unmodifiable map, keyed by
     *         Infinispan cache name.
     */
    public static Map<String, RedisStore> getInstances() {
        return Collections.unmodifiableMap(instances);
    }
    
    private static final Log log = LogFactory.getLog(RedisStore.class, Log.class);

    private InitializationContext ctx = null;
    private RedisConnectionPool connectionPool = null;

    /**
     * Used to initialize a cache loader.  Typically invoked by the {@link org.infinispan.persistence.manager.PersistenceManager}
     * when setting up cache loaders.
     *
     * @throws PersistenceException in case of an error, e.g. communicating with the external storage
     */
    @Override
    public void init(InitializationContext ctx)
    {
        RedisStore.log.infof("Initialising Redis store for cache '%s'", ctx.getCache().getName());
        this.ctx = ctx;
    
        // Register store
        if (ctx.getCache().getName() != null) {
            RedisStore.instances.put(ctx.getCache().getName(), this);
        }
    }

    /**
     * Invoked on component start
     */
    @Override
    public void start()
    {
        RedisStore.log.infof("Starting Redis store for cache '%s'", ctx.getCache().getName());

        try {
            this.connectionPool = RedisConnectionPoolFactory.factory(this.ctx.getConfiguration(), this.ctx.getMarshaller());
        }
        catch(Exception ex) {
            RedisStore.log.errorf(ex, "Failed to initialise the Redis store for cache '%s'", ctx.getCache().getName());
            throw new PersistenceException(ex);
        }
    }

    /**
     * Invoked on component stop
     */
    @Override
    public void stop()
    {
        RedisStore.log.infof("Stopping Redis store for cache '%s'", ctx.getCache().getName());

        if (null != this.connectionPool) {
            this.connectionPool.shutdown();
        }
    
        // Unregister store
        if (ctx.getCache().getName() != null) {
            RedisStore.instances.remove(ctx.getCache().getName());
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
        RedisStore.log.debugf("Iterating Redis store entries for cache '%s'", ctx.getCache().getName());

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
                            RedisStore.log.errorf(ex, "Failed to process a Redis store key for cache '%s'", ctx.getCache().getName());
                            throw new PersistenceException(ex);
                        }
                    }
                });
            }
        }
        catch(Exception ex) {
            RedisStore.log.errorf(ex, "Failed to process the Redis store keys for cache '%s'", ctx.getCache().getName());
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
        RedisStore.log.debugf("Fetching the Redis store size for cache '%s'", ctx.getCache().getName());
        RedisConnection connection = null;

        try {
            connection = this.connectionPool.getConnection();
            long dbSize = connection.dbSize();

            // Can't return more than Integer.MAX_VALUE due to interface limitation
            // If the number of elements in redis is more than the int max size,
            // log the anomaly and return the int max size
            if (dbSize > Integer.MAX_VALUE) {
                RedisStore.log.warnf("The Redis store for cache '%s' is holding more entries than we can count! " +
                        "Total number of entries found %d. Limited to returning count as %d",
                        ctx.getCache().getName(), dbSize, Integer.MAX_VALUE
                );

                return Integer.MAX_VALUE;
            }
            else {
                return (int) dbSize;
            }
        }
        catch(Exception ex) {
            RedisStore.log.errorf(ex, "Failed to fetch the entry count for the Redis store for cache '%s'", ctx.getCache().getName());
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
        RedisStore.log.debugf("Clearing the Redis store for cache '%s'", ctx.getCache().getName());
        RedisConnection connection = null;

        try {
            connection = this.connectionPool.getConnection();
            connection.flushDb();
        }
        catch(Exception ex) {
            RedisStore.log.errorf(ex, "Failed to clear the Redis store for cache '%s'", ctx.getCache().getName());
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
        RedisStore.log.debugf("Loading entry from the Redis store for cache '%s'", ctx.getCache().getName());
        RedisConnection connection = null;

        try {
            connection = this.connectionPool.getConnection();
            List<byte[]> data = connection.hmget(key, "value", "metadata");
            byte[] value = data.get(0);

            if (null == value) {
                return null;
            }

            ByteBuffer valueBuf = this.ctx.getByteBufferFactory().newByteBuffer(value, 0, value.length);
            ByteBuffer metadataBuf = null;
            byte[] metadata = data.get(1);

            if (null != metadata) {
                metadataBuf = this.ctx.getByteBufferFactory().newByteBuffer(metadata, 0, metadata.length);
            }

            return this.ctx.getMarshalledEntryFactory().newMarshalledEntry(key, valueBuf, metadataBuf);
        }
        catch(Exception ex) {
            RedisStore.log.errorf(ex, "Failed to load entry from the Redis store for cache '%s'", ctx.getCache().getName());
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
        RedisStore.log.debugf("Writing entry to the Redis store for cache '%s'", ctx.getCache().getName());
        RedisConnection connection = null;

        try {
            byte[] value;
            byte[] metadata;
            long lifespan = -1;
            Map<String,byte[]> fields = new HashMap<>();

            if (null != marshalledEntry.getValueBytes()) {
                value = marshalledEntry.getValueBytes().getBuf();
                fields.put("value", value);
            }

            if (null != marshalledEntry.getMetadataBytes()) {
                metadata = marshalledEntry.getMetadataBytes().getBuf();
                fields.put("metadata", metadata);
                lifespan = marshalledEntry.getMetadata().lifespan();
            }

            connection = this.connectionPool.getConnection();
            connection.hmset(marshalledEntry.getKey(), fields);

            if (-1 < lifespan) {
                connection.expire(marshalledEntry.getKey(), this.toSeconds(lifespan, marshalledEntry.getKey(), "lifespan"));
            }
        }
        catch(Exception ex) {
            RedisStore.log.errorf(ex,"Failed to write entry to the Redis store for cache '%s'", ctx.getCache().getName());
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
        RedisStore.log.debugf("Deleting entry from Redis store for cache '%s'", ctx.getCache().getName());
        RedisConnection connection = null;

        try {
            connection = this.connectionPool.getConnection();
            return connection.delete(key);
        }
        catch(Exception ex) {
            RedisStore.log.errorf(ex,"Failed to delete entry from the Redis store for cache '%s'", ctx.getCache().getName());
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
        RedisStore.log.debugf("Checking key in Redis store for cache '%s'", ctx.getCache().getName());
        RedisConnection connection = null;

        try {
            connection = this.connectionPool.getConnection();
            return connection.exists(key);
        }
        catch(Exception ex) {
            RedisStore.log.errorf(ex,"Failed to check key in Redis store for cache '%s'", ctx.getCache().getName());
            throw new PersistenceException(ex);
        }
        finally {
            if (null != connection) {
                connection.release();
            }
        }
    }

    private int toSeconds(long millis, Object key, String desc)
    {
        if (millis > 0 && millis < 1000) {
            if (log.isTraceEnabled()) {
                log.tracef("Adjusting %s time for (k,v): (%s, %s) from %d millis to 1 sec, as milliseconds are not supported by Redis",
                    desc ,key, millis);
            }

            return 1;
        }

        return (int) TimeUnit.MILLISECONDS.toSeconds(millis);
    }
}
