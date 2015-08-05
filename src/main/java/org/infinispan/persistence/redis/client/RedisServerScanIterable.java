package org.infinispan.persistence.redis.client;

import org.infinispan.commons.marshall.StreamingMarshaller;
import redis.clients.jedis.Jedis;

import java.util.Iterator;

public class RedisServerScanIterable implements Iterable<Object>
{
    private StreamingMarshaller marshaller;
    private Jedis client;

    public RedisServerScanIterable(Jedis client, StreamingMarshaller marshaller)
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
