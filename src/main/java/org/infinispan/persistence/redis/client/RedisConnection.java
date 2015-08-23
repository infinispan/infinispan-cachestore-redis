package org.infinispan.persistence.redis.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface RedisConnection
{
    /**
     * Redis scan command
     */
    Iterable<Object> scan();

    /**
     * Redis get command
     */
    List<byte[]> hmget(Object key, String... field) throws IOException, InterruptedException, ClassNotFoundException;

    /**
     * Redis set command
     */

    void hmset(Object key, Map<String,byte[]> fields) throws IOException, InterruptedException;

    /**
     * Redis expire command
     */
    void expire(Object key, int expire);

    /**
     * Redis del command
     */
    boolean delete(Object key) throws IOException, InterruptedException;

    /**
     * Redis exists command
     */
    boolean exists(Object key) throws IOException, InterruptedException;

    /**
     * Redis dbsize command
     */
    long dbSize();

    /**
     * Redis flushdb command
     */
    void flushDb() throws IOException, InterruptedException;

    /**
     * Release the connection, returning the connection to the pool
     */
    void release();
}
