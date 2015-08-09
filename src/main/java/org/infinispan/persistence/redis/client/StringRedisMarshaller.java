package org.infinispan.persistence.redis.client;

import org.infinispan.commons.marshall.StreamingMarshaller;

import java.io.IOException;
import java.nio.charset.Charset;

final public class StringRedisMarshaller implements RedisMarshaller<String>
{
    private final String encoding = "ISO-8859-1";
    private StreamingMarshaller marshaller;

    public StringRedisMarshaller(StreamingMarshaller marshaller)
    {
        this.marshaller = marshaller;
    }

    @Override
    public String marshallKey(Object data)
    {
        try {
            byte[] buf = this.marshaller.objectToByteBuffer(data);
            return new String(buf, Charset.forName(this.encoding));
        }
        catch(IOException | InterruptedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Object unmarshallKey(String buf)
    {
        try {
            byte[] data = buf.getBytes(Charset.forName(this.encoding));
            return this.marshaller.objectFromByteBuffer(data);
        }
        catch(IOException | ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public String marshallValue(RedisCacheEntry data)
    {
        try {
            byte[] buf = this.marshaller.objectToByteBuffer(data);
            return new String(buf, Charset.forName(this.encoding));
        }
        catch(IOException | InterruptedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public RedisCacheEntry unmarshallValue(String buf)
    {
        try {
            byte[] data = buf.getBytes(Charset.forName(this.encoding));
            return (RedisCacheEntry) this.marshaller.objectFromByteBuffer(data);
        }
        catch(IOException | ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
