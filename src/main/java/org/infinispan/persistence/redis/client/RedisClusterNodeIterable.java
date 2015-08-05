package org.infinispan.persistence.redis.client;

import org.infinispan.commons.marshall.StreamingMarshaller;
import redis.clients.jedis.JedisCluster;

import java.util.Iterator;

public class RedisClusterNodeIterable implements Iterable<Object>
{
    private StreamingMarshaller marshaller;
    private JedisCluster client;

    public RedisClusterNodeIterable(JedisCluster client, StreamingMarshaller marshaller)
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
