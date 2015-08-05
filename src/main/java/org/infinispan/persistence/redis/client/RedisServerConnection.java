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
        String keyByteString = this.marshaller.marshall(key);
        String valueByteString = this.client.get(keyByteString);

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
        this.client.set(keyByteString, valueByteString);
    }

    @Override
    public boolean delete(Object key)
        throws IOException, InterruptedException
    {
        String keyByteString = this.marshaller.marshall(key);
        return this.client.del(keyByteString) > 0;
    }

    @Override
    public boolean exists(Object key)
        throws IOException, InterruptedException
    {
        String keyByteString = this.marshaller.marshall(key);
        return this.client.exists(keyByteString);
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
