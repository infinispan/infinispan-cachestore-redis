package org.infinispan.persistence.redis.client;

import org.infinispan.commons.marshall.StreamingMarshaller;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.nio.charset.Charset;

public class RedisServerConnection implements RedisConnection
{
    private Jedis client;
    private StreamingMarshaller marshaller;

    RedisServerConnection(Jedis client, StreamingMarshaller marshaller)
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
        byte[] keyBuf = this.marshaller.objectToByteBuffer(key);
        String keyByteString = new String(keyBuf, Charset.forName("UTF-8"));

        String valueByteString = this.client.get(keyByteString);

        byte[] valueBuf = valueByteString.getBytes(Charset.forName("UTF-8"));
        return this.marshaller.objectFromByteBuffer(valueBuf);
    }

    @Override
    public void set(Object key, Object value)
        throws IOException, InterruptedException
    {
        byte[] keyBuf = this.marshaller.objectToByteBuffer(key);
        String keyByteString = new String(keyBuf, Charset.forName("UTF-8"));

        byte[] valueBuf = this.marshaller.objectToByteBuffer(value);
        String valueByteString = new String(valueBuf, Charset.forName("UTF-8"));

        this.client.set(keyByteString, valueByteString);
    }

    @Override
    public boolean delete(Object key)
        throws IOException, InterruptedException
    {
        byte[] keyBuf = this.marshaller.objectToByteBuffer(key);
        String keyByteString = new String(keyBuf, Charset.forName("UTF-8"));

        return this.client.del(keyByteString) > 0;
    }

    @Override
    public boolean exists(Object key)
        throws IOException, InterruptedException
    {
        byte[] keyBuf = this.marshaller.objectToByteBuffer(key);
        String keyByteString = new String(keyBuf, Charset.forName("UTF-8"));

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
