package org.infinispan.persistence.redis.client;

import org.infinispan.persistence.redis.configuration.ConnectionPoolConfiguration;
import org.infinispan.persistence.redis.configuration.RedisServerConfiguration;
import org.infinispan.persistence.redis.configuration.RedisStoreConfiguration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

final public class RedisClusterConnectionPool implements RedisConnectionPool
{
    private RedisMarshaller<String> marshaller;
    private JedisCluster cluster;

    public RedisClusterConnectionPool(RedisStoreConfiguration configuration, RedisMarshaller<String> marshaller)
    {
        Set<HostAndPort> clusterNodes = new HashSet<HostAndPort>();
        for (RedisServerConfiguration server : configuration.servers()) {
            clusterNodes.add(new HostAndPort(server.host(), server.port()));
        }

        ConnectionPoolConfiguration connectionPoolConfiguration = configuration.connectionPool();

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(connectionPoolConfiguration.maxTotal());
        poolConfig.setMinIdle(connectionPoolConfiguration.minIdle());
        poolConfig.setMaxIdle(connectionPoolConfiguration.maxIdle());
        poolConfig.setMinEvictableIdleTimeMillis(connectionPoolConfiguration.minEvictableIdleTime());
        poolConfig.setTimeBetweenEvictionRunsMillis(connectionPoolConfiguration.timeBetweenEvictionRuns());

        this.cluster = new JedisCluster(
            clusterNodes,
            configuration.connectionTimeout(),
            configuration.socketTimeout(),
            configuration.maxRedirections(),
            poolConfig
        );

        this.marshaller = marshaller;
    }

    @Override
    public RedisConnection getConnection()
    {
        return new RedisClusterConnection(this.cluster, this.marshaller);
    }

    @Override
    public void shutdown()
    {
        this.cluster.close();
    }
}
