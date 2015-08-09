package org.infinispan.persistence.redis.client;

public interface RedisMarshaller<T>
{
    /**
     * Marshall the value to the format needed by Redis
     */
    T marshallKey(Object data);

    /**
     * Unmarshall the value back from the Redis type
     */
    Object unmarshallKey(T buf);

    /**
     * Marshall the value to the format needed by Redis
     */
    T marshallValue(RedisCacheEntry data);

    /**
     * Unmarshall the value back from the Redis type
     */
    RedisCacheEntry unmarshallValue(T buf);
}
