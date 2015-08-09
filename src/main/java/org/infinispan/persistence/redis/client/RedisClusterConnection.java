package org.infinispan.persistence.redis.client;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.Map;

final public class RedisClusterConnection implements RedisConnection
{
    private JedisCluster cluster;
    private RedisMarshaller<String> marshaller;

    RedisClusterConnection(JedisCluster cluster, RedisMarshaller<String> marshaller)
    {
        this.cluster = cluster;
        this.marshaller = marshaller;
    }

    @Override
    public void release()
    {
        // Nothing to do. Connection pools are managed internally by the cluster client.
    }

    @Override
    public Iterable<Object> scan()
    {
        return new RedisClusterNodeIterable(this.cluster, marshaller);
    }

    @Override
    public RedisCacheEntry get(Object key)
        throws IOException, InterruptedException, ClassNotFoundException
    {
        String valueByteString = this.cluster.get(this.marshaller.marshallKey(key));
        return (valueByteString != null ? this.marshaller.unmarshallValue(valueByteString) : null);
    }

    @Override
    public void set(Object key, RedisCacheEntry value)
        throws IOException, InterruptedException
    {
        this.cluster.set(this.marshaller.marshallKey(key), this.marshaller.marshallValue(value));
    }

    @Override
    public void expireAt(Object key, long expireAt)
    {
        this.cluster.expireAt(this.marshaller.marshallKey(key), expireAt);
    }

    @Override
    public boolean delete(Object key)
        throws IOException, InterruptedException
    {
        return this.cluster.del(this.marshaller.marshallKey(key)) > 0;
    }

    @Override
    public boolean exists(Object key)
        throws IOException, InterruptedException
    {
        return this.cluster.exists(this.marshaller.marshallKey(key));
    }

    @Override
    public long dbSize()
    {
        long totalSize = 0;
        Jedis client = null;

        Map<String, JedisPool> clusterNodes = this.cluster.getClusterNodes();
        for (String nodeKey : clusterNodes.keySet()) {
            try {
                client = clusterNodes.get(nodeKey).getResource();
                totalSize += client.dbSize();
            }
            finally {
                if (null != client) {
                    client.close();
                    client = null;
                }
            }
        }

        return totalSize;
    }

    @Override
    public void flushDb()
        throws IOException, InterruptedException
    {
        Jedis client = null;

        Map<String, JedisPool> clusterNodes = this.cluster.getClusterNodes();
        for (String nodeKey : clusterNodes.keySet()) {
            try {
                client = clusterNodes.get(nodeKey).getResource();
                client.flushDB();
            }
            finally {
                if (null != client) {
                    client.close();
                    client = null;
                }
            }
        }
    }
}
