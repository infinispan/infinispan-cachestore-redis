package org.infinispan.persistence.redis.client;

import org.infinispan.persistence.redis.configuration.ConnectionPoolConfiguration;
import org.infinispan.persistence.redis.configuration.RedisServerConfiguration;
import org.infinispan.persistence.redis.configuration.RedisStoreConfiguration;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

final public class RedisSentinelConnectionPool implements RedisConnectionPool
{
    private RedisMarshaller<String> marshaller;
    private JedisSentinelPool sentinelPool;

    public RedisSentinelConnectionPool(RedisStoreConfiguration configuration, RedisMarshaller<String> marshaller)
    {
        Set<String> sentinels = new HashSet<String>();
        for (RedisServerConfiguration server : configuration.sentinels()) {
            sentinels.add(String.format("%s:%s", server.host(), server.port()));
        }

        ConnectionPoolConfiguration connectionPoolConfiguration = configuration.connectionPool();

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(connectionPoolConfiguration.maxTotal());
        poolConfig.setMinIdle(connectionPoolConfiguration.minIdle());
        poolConfig.setMaxIdle(connectionPoolConfiguration.maxIdle());
        poolConfig.setMinEvictableIdleTimeMillis(connectionPoolConfiguration.minEvictableIdleTime());
        poolConfig.setTimeBetweenEvictionRunsMillis(connectionPoolConfiguration.timeBetweenEvictionRuns());

        sentinelPool = new JedisSentinelPool(
            configuration.masterName(),
            sentinels,
            poolConfig,
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
        return new RedisServerConnection(this.sentinelPool.getResource(), this.marshaller);
    }

    @Override
    public void shutdown()
    {
        this.sentinelPool.destroy();
    }
}
