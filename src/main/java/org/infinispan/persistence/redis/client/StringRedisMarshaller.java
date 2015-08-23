package org.infinispan.persistence.redis.client;

import org.infinispan.commons.marshall.StreamingMarshaller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final public class StringRedisMarshaller implements RedisMarshaller<String>
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
        if (null == data) {
            return null;
        }

        try {
            byte[] buf = this.marshaller.objectToByteBuffer(data);
            return this.encode(buf);
        }
        catch(IOException | InterruptedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Object unmarshall(String buf)
    {
        if (null == buf) {
            return null;
        }

        try {
            byte[] data = this.decode(buf);
            return this.marshaller.objectFromByteBuffer(data);
        }
        catch(IOException | ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public String encode(byte[] data)
    {
        if (null == data) {
            return null;
        }

        return new String(data, Charset.forName(this.encoding));
    }

    @Override
    public Map<String,String> encode(Map<String,byte[]> datums)
    {
        Map<String,String> encoded = new HashMap<>();

        for (String key : datums.keySet()) {
            encoded.put(key, this.encode(datums.get(key)));
        }

        return encoded;
    }

    @Override
    public byte[] decode(String buf)
    {
        if (null == buf) {
            return null;
        }

        return buf.getBytes(Charset.forName(this.encoding));
    }

    @Override
    public List<byte[]> decode(List<String> bufs)
    {
        List<byte[]> decoded = new ArrayList<>();

        for (String buf : bufs) {
            decoded.add(this.decode(buf));
        }

        return decoded;
    }
}
