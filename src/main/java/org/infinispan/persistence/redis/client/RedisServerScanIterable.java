package org.infinispan.persistence.redis.client;

import redis.clients.jedis.Jedis;

import java.util.Iterator;

final public class RedisServerScanIterable implements Iterable<Object>
{
    private RedisMarshaller<String> marshaller;
    private Jedis client;

    public RedisServerScanIterable(Jedis client, RedisMarshaller<String> marshaller)
    {
        this.client = client;
        this.marshaller = marshaller;
    }

    @Override
    public Iterator<Object> iterator()
    {
        return new RedisServerKeyIterator(this.client, this.marshaller);
    }
}
