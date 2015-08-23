package org.infinispan.persistence.redis.client;

import java.util.List;
import java.util.Map;

public interface RedisMarshaller<T>
{
    /**
     * Marshall the value to the format needed by Redis
     */
    T marshall(Object data);

    /**
     * Unmarshall the value back from the Redis type
     */
    Object unmarshall(T buf);

    /**
     * Marshall the value to the format needed by Redis
     */
    T encode(byte[] data);

    /**
     * Marshall multiple values to the format needed by Redis
     */
    Map<String,T> encode(Map<String,byte[]> datums);

    /**
     * Unmarshall the value back from the Redis type
     */
    byte[] decode(T buf);

    /**
     * Unmarshall multiple values back from the Redis type
     */
    List<byte[]> decode(List<T> bufs);
}
