package org.infinispan.persistence.redis.client;

import redis.clients.jedis.Jedis;

import java.io.IOException;

public class RedisServerConnection implements RedisConnection
{
    private Jedis client;
    private RedisMarshaller<String> marshaller;

    RedisServerConnection(Jedis client, RedisMarshaller<String> marshaller)
    {
        this.client = client;
        this.marshaller = marshaller;
    }

    @Override
    public void release()
    {
        this.client.close();
    }

    @Override
    public Iterable<Object> scan()
    {
        return new RedisServerScanIterable(this.client, this.marshaller);
    }

    @Override
    public Object get(Object key)
        throws IOException, InterruptedException, ClassNotFoundException
    {
        String valueByteString = this.client.get(this.marshaller.marshall(key));
        return (valueByteString != null ? this.marshaller.unmarshall(valueByteString) : null);
    }

    @Override
    public void set(Object key, Object value)
        throws IOException, InterruptedException
    {
        this.client.set(this.marshaller.marshall(key), this.marshaller.marshall(value));
    }

    @Override
    public boolean delete(Object key)
        throws IOException, InterruptedException
    {
        return this.client.del(this.marshaller.marshall(key)) > 0;
    }

    @Override
    public boolean exists(Object key)
        throws IOException, InterruptedException
    {
        return this.client.exists(this.marshaller.marshall(key));
    }

    @Override
    public long dbSize()
    {
        return this.client.dbSize();
    }

    @Override
    public void flushDb()
    {
        this.client.flushDB();
    }
}
