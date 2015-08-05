package org.infinispan.persistence.redis.client;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.Map;

public class RedisClusterConnection implements RedisConnection
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
        // do nothing
    }

    @Override
    public Iterable<Object> scan()
    {
        return new RedisClusterNodeIterable(this.cluster, marshaller);
    }

    @Override
    public Object get(Object key)
        throws IOException, InterruptedException, ClassNotFoundException
    {
        String keyByteString = this.marshaller.marshall(key);
        String valueByteString = this.cluster.get(keyByteString);

        if (null != valueByteString) {
            return this.marshaller.unmarshall(valueByteString);
        }
        else {
            return null;
        }
    }

    @Override
    public void set(Object key, Object value)
        throws IOException, InterruptedException
    {
        String keyByteString = this.marshaller.marshall(key);
        String valueByteString = this.marshaller.marshall(value);
        this.cluster.set(keyByteString, valueByteString);
    }

    @Override
    public boolean delete(Object key)
        throws IOException, InterruptedException
    {
        String keyByteString = this.marshaller.marshall(key);
        return this.cluster.del(keyByteString) > 0;
    }

    @Override
    public boolean exists(Object key)
        throws IOException, InterruptedException
    {
        String keyByteString = this.marshaller.marshall(key);
        return this.cluster.exists(keyByteString);
    }

    @Override
    public long dbSize()
    {
        long totalSize = 0;
        Jedis client = null;

        try {
            Map<String, JedisPool> clusterNodes = this.cluster.getClusterNodes();
            for (String nodeKey : clusterNodes.keySet()) {
                client = clusterNodes.get(nodeKey).getResource();
                totalSize += client.dbSize();
                client.close();
                client = null;
            }
        }
        finally {
            if (null != client) {
                client.close();
            }
        }

        return totalSize;
    }

    @Override
    public void flushDb()
        throws IOException, InterruptedException
    {
        Jedis client = null;

        try {
            Map<String, JedisPool> clusterNodes = this.cluster.getClusterNodes();
            for (String nodeKey : clusterNodes.keySet()) {
                client = clusterNodes.get(nodeKey).getResource();
                client.flushDB();
                client.close();
                client = null;
            }
        }
        finally {
            if (null != client) {
                client.close();
            }
        }
    }
}
