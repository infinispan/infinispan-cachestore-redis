package org.infinispan.persistence.redis.client;

import org.infinispan.persistence.redis.configuration.ConnectionPoolConfiguration;
import org.infinispan.persistence.redis.configuration.RedisServerConfiguration;
import org.infinispan.persistence.redis.configuration.RedisStoreConfiguration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

final public class RedisServerConnectionPool implements RedisConnectionPool
{
    private JedisPool connectionPool;
    private RedisMarshaller<String> marshaller;

    public RedisServerConnectionPool(RedisStoreConfiguration configuration, RedisMarshaller<String> marshaller)
    {
        String host = null;
        int port = 6379;

        for (RedisServerConfiguration server : configuration.servers()) {
            host = server.host();
            port = server.port();
        }

        if (null == host) {
            // todo: handle error
        }

        ConnectionPoolConfiguration connectionPoolConfiguration = configuration.connectionPool();

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(connectionPoolConfiguration.maxTotal());
        poolConfig.setMinIdle(connectionPoolConfiguration.minIdle());
        poolConfig.setMaxIdle(connectionPoolConfiguration.maxIdle());
        poolConfig.setMinEvictableIdleTimeMillis(connectionPoolConfiguration.minEvictableIdleTime());
        poolConfig.setTimeBetweenEvictionRunsMillis(connectionPoolConfiguration.timeBetweenEvictionRuns());

        this.connectionPool = new JedisPool(
            poolConfig,
            host,
            port,
            configuration.connectionTimeout(),
            configuration.socketTimeout(),
            configuration.password(),
            configuration.database(),
            null
        );

        this.marshaller = marshaller;
    }

    @Override
    public RedisConnection getConnection()
    {
        return new RedisServerConnection(this.connectionPool.getResource(), this.marshaller);
    }

    @Override
    public void shutdown()
    {
        this.connectionPool.destroy();
    }
}
