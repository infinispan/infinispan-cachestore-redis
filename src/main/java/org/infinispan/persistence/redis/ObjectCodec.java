package org.infinispan.persistence.redis;

import com.lambdaworks.redis.codec.RedisCodec;
import org.infinispan.commons.marshall.StreamingMarshaller;

import java.nio.ByteBuffer;

final public class ObjectCodec extends RedisCodec<Object,Object>
{
    private StreamingMarshaller streamingMarshaller;

    public ObjectCodec(StreamingMarshaller streamingMarshaller)
    {
        this.streamingMarshaller = streamingMarshaller;
    }

    @Override
    public Object decodeKey(ByteBuffer bytes)
    {
        try {
            return this.streamingMarshaller.objectFromByteBuffer(bytes.array());
        }
        catch(Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Object decodeValue(ByteBuffer bytes)
    {
        try {
            return this.streamingMarshaller.objectFromByteBuffer(bytes.array());
        }
        catch(Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public byte[] encodeKey(Object key)
    {
        try {
            return this.streamingMarshaller.objectToBuffer(key).getBuf();
        }
        catch(Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public byte[] encodeValue(Object value)
    {
        try {
            return this.streamingMarshaller.objectToBuffer(value).getBuf();
        }
        catch(Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
