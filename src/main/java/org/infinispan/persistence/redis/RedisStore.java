package org.infinispan.persistence.redis;

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
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
@ConfiguredBy(RedisStoreConfiguration.class)
final public class RedisStore implements AdvancedLoadWriteStore
{
    private static final Log log = LogFactory.getLog(RedisStore.class, Log.class);

    private InitializationContext ctx;
    private RedisStoreConfiguration configuration;

    private JedisPool pool;
    private JedisPoolConfig poolConfig;
    private JedisCluster cluster;

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
        this.configuration = this.ctx.getConfiguration();

        // Configure pool
        this.poolConfig = new JedisPoolConfig();
        this.poolConfig.setMaxIdle(this.configuration.connectionPool().maxIdle());
        this.poolConfig.setMaxTotal(this.configuration.connectionPool().maxTotal());
        this.poolConfig.setMinIdle(this.configuration.connectionPool().minIdle());

        Set<HostAndPort> clusterNodes = new HashSet<HostAndPort>();
        for (RedisServerConfiguration redisServerConfiguration : this.configuration.servers()) {
            clusterNodes.add(new HostAndPort(redisServerConfiguration.host(), redisServerConfiguration.port()));
        }

        JedisCluster cluster = new JedisCluster(clusterNodes, this.configuration.connectionTimeout(),
            this.configuration.socketTimeout(), this.configuration.maxRedirections(), this.poolConfig);
    }

    /**
     * Invoked on component start
     */
    @Override
    public void start()
    {

    }

    /**
     * Invoked on component stop
     */
    @Override
    public void stop()
    {
        if (null != this.pool) {
            this.pool.destroy();
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
    public void process(final KeyFilter filter, final CacheLoaderTask task, Executor executor, boolean fetchValue, boolean fetchMetadata)
    {
        final InitializationContext ctx = this.ctx;
        final JedisPool pool = this.pool;
        final TaskContext taskContext = new TaskContextImpl();
        Jedis jedis = null;

        try {
            jedis = this.pool.getResource();

            for (final byte[] keyRaw : jedis.keys(new byte[] {'*'})) {
                if (taskContext.isStopped()) {
                    break;
                }

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        Jedis jedis = null;

                        try {
                            Object key = ctx.getMarshaller().objectFromByteBuffer(keyRaw);

                            if (filter.accept(key)) {
                                jedis = pool.getResource();

                                byte[] valueRaw = jedis.get(keyRaw);
                                ByteBuffer keyBuf = ctx.getByteBufferFactory().newByteBuffer(keyRaw, 0, keyRaw.length);
                                ByteBuffer valueBuf = ctx.getByteBufferFactory().newByteBuffer(valueRaw, 0, valueRaw.length);

                                task.processEntry(
                                    ctx.getMarshalledEntryFactory().newMarshalledEntry(keyBuf, valueBuf, (ByteBuffer) null),
                                    taskContext
                                );
                            }
                        }
                        catch(Exception ex) {
                            RedisStore.log.error("Failed to process the redis store key", ex);

                            if (null != jedis) {
                                pool.returnBrokenResource(jedis);
                                jedis = null;
                            }

                            throw new PersistenceException(ex);
                        }
                        finally {
                            if (null != jedis) {
                                pool.returnResource(jedis);
                            }
                        }
                    }
                });
            }
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to process the redis store keys", ex);

            if (null != jedis) {
                pool.returnBrokenResource(jedis);
                jedis = null;
            }

            throw new PersistenceException(ex);
        }
        finally {
            if (null != jedis) {
                pool.returnResource(jedis);
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
        Jedis jedis = null;

        try {
            jedis = this.pool.getResource();
            long dbSize = jedis.dbSize();

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

            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
                jedis = null;
            }

            throw new PersistenceException(ex);
        }
        finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
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
        Jedis jedis = null;

        try {
            jedis = this.pool.getResource();
            jedis.flushDB();
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to clear all elements in the redis store", ex);

            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
                jedis = null;
            }

            throw new PersistenceException(ex);
        }
        finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
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
        Jedis jedis = null;

        try {
            ByteBuffer keyRaw = this.ctx.getMarshaller().objectToBuffer(key);

            jedis = this.pool.getResource();
            byte[] value = jedis.get(keyRaw.getBuf());

            if (null == value) {
                return null;
            }

            ByteBuffer valueBuf = this.ctx.getByteBufferFactory().newByteBuffer(value, 0, value.length);
            return this.ctx.getMarshalledEntryFactory().newMarshalledEntry(keyRaw, valueBuf, (ByteBuffer) null);
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to load element from the redis store", ex);

            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
                jedis = null;
            }

            throw new PersistenceException(ex);
        }
        finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
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
        ByteBuffer buf = marshalledEntry.getValueBytes();
        Jedis jedis = null;

        try {
            byte[] key = marshalledEntry.getKeyBytes().getBuf();

            jedis = this.pool.getResource();
            jedis.set(key, buf.getBuf());
            jedis.expire(key, 100); // todo: set based on configuration
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to write element to the redis store", ex);

            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
                jedis = null;
            }

            throw new PersistenceException(ex);
        }
        finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
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
        Jedis jedis = null;

        try {
            jedis = this.pool.getResource();
            ByteBuffer marshaledBuff = this.ctx.getMarshaller().objectToBuffer(key);
            return jedis.del(marshaledBuff.getBuf()) > 0;
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to delete element from the redis store", ex);

            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
                jedis = null;
            }

            throw new PersistenceException(ex);
        }
        finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
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
        Jedis jedis = null;

        try {
            jedis = this.pool.getResource();
            ByteBuffer marshaledBuff = this.ctx.getMarshaller().objectToBuffer(key);
            return jedis.exists(marshaledBuff.getBuf());
        }
        catch(Exception ex) {
            RedisStore.log.error("Failed to discover if element is in the redis store", ex);

            if (null != jedis) {
                this.pool.returnBrokenResource(jedis);
                jedis = null;
            }

            throw new PersistenceException(ex);
        }
        finally {
            if (null != jedis) {
                this.pool.returnResource(jedis);
            }
        }
    }
}
