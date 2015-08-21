package org.infinispan.persistence.redis.client;

import redis.clients.jedis.Jedis;

import java.io.IOException;

final public class RedisServerConnection implements RedisConnection
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
    public byte[] hget(Object key, String field)
        throws IOException, InterruptedException, ClassNotFoundException
    {
        return this.marshaller.decode(this.client.hget(this.marshaller.marshall(key), field));
    }

    @Override
    public void hset(Object key, String field, byte[] value)
        throws IOException, InterruptedException
    {
        this.client.hset(this.marshaller.marshall(key), field, this.marshaller.encode(value));
    }

    @Override
    public void expireAt(Object key, long expireAt)
    {
        this.client.expireAt(this.marshaller.marshall(key), expireAt);
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
