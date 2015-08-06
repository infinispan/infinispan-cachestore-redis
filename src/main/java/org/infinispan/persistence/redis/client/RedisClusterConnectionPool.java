package org.infinispan.persistence.redis.client;

import org.infinispan.persistence.redis.configuration.RedisServerConfiguration;
import org.infinispan.persistence.redis.configuration.RedisStoreConfiguration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

public class RedisClusterConnectionPool implements RedisConnectionPool
{
    private RedisMarshaller<String> marshaller;
    private JedisCluster cluster;

    public RedisClusterConnectionPool(RedisStoreConfiguration configuration, RedisMarshaller<String> marshaller)
    {
        Set<HostAndPort> clusterNodes = new HashSet<HostAndPort>();
        for (RedisServerConfiguration server : configuration.servers()) {
            clusterNodes.add(new HostAndPort(server.host(), server.port()));
        }

        // todo: obtain from configuration file
        int connectionTimeout = 2000;
        int soTimeout = 2000;
        int maxRedirections = 5;

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(5);
        poolConfig.setMinIdle(2);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinEvictableIdleTimeMillis(100);
        poolConfig.setTimeBetweenEvictionRunsMillis(100);

        this.cluster = new JedisCluster(clusterNodes, connectionTimeout, soTimeout, maxRedirections, poolConfig);
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
