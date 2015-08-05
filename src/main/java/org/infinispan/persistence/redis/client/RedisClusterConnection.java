package org.infinispan.persistence.redis.client;

import org.infinispan.commons.marshall.StreamingMarshaller;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class RedisClusterConnection implements RedisConnection
{
    private JedisCluster cluster;
    private StreamingMarshaller marshaller;

    RedisClusterConnection(JedisCluster cluster, StreamingMarshaller marshaller)
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
        byte[] keyBuf = this.marshaller.objectToByteBuffer(key);
        String keyByteString = new String(keyBuf, Charset.forName("UTF-8"));

        String valueByteString = this.cluster.get(keyByteString);

        if (null != valueByteString) {
            byte[] valueBuf = valueByteString.getBytes(Charset.forName("UTF-8"));
            return this.marshaller.objectFromByteBuffer(valueBuf);
        }
        else {
            return null;
        }
    }

    @Override
    public void set(Object key, Object value)
        throws IOException, InterruptedException
    {
        byte[] keyBuf = this.marshaller.objectToByteBuffer(key);
        String keyByteString = new String(keyBuf, Charset.forName("UTF-8"));

        byte[] valueBuf = this.marshaller.objectToByteBuffer(value);
        String valueByteString = new String(valueBuf, Charset.forName("UTF-8"));

        this.cluster.set(keyByteString, valueByteString);
    }

    @Override
    public boolean delete(Object key)
        throws IOException, InterruptedException
    {
        byte[] keyBuf = this.marshaller.objectToByteBuffer(key);
        String keyByteString = new String(keyBuf, Charset.forName("UTF-8"));

        return this.cluster.del(keyByteString) > 0;
    }

    @Override
    public boolean exists(Object key)
        throws IOException, InterruptedException
    {
        byte[] keyBuf = this.marshaller.objectToByteBuffer(key);
        String keyByteString = new String(keyBuf, Charset.forName("UTF-8"));

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
