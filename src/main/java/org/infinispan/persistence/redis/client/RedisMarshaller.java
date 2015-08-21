package org.infinispan.persistence.redis.client;

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
     * Unmarshall the value back from the Redis type
     */
    byte[] decode(T buf);
}
