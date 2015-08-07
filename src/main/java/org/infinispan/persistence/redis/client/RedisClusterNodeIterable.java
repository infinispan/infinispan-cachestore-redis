package org.infinispan.persistence.redis.client;

import redis.clients.jedis.JedisCluster;

import java.util.Iterator;

final public class RedisClusterNodeIterable implements Iterable<Object>
{
    private RedisMarshaller<String> marshaller;
    private JedisCluster client;

    public RedisClusterNodeIterable(JedisCluster client, RedisMarshaller<String> marshaller)
    {
        this.client = client;
        this.marshaller = marshaller;
    }

    @Override
    public Iterator<Object> iterator()
    {
        return new RedisClusterNodeIterator(this.client, this.marshaller);
    }
}
