package org.infinispan.persistence.redis.client;

public interface RedisConnectionPool
{
    /**
     * Get a connection from the pool
     */
    RedisConnection getConnection();

    /**
     * Shutdown the connection pool, closing all connections
     */
    void shutdown();
}
