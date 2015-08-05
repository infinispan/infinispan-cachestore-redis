package org.infinispan.persistence.redis.client;

import org.infinispan.commons.marshall.StreamingMarshaller;

import java.io.IOException;
import java.nio.charset.Charset;

public class StringRedisMarshaller implements RedisMarshaller<String>
{
    private final String encoding = "ISO-8859-1";
    private StreamingMarshaller marshaller;

    public StringRedisMarshaller(StreamingMarshaller marshaller)
    {
        this.marshaller = marshaller;
    }

    @Override
    public String marshall(Object data)
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
    public Object unmarshall(String buf)
    {
        try {
            byte[] data = buf.getBytes(Charset.forName(this.encoding));
            return this.marshaller.objectFromByteBuffer(data);
        }
        catch(IOException | ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
