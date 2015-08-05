package org.infinispan.persistence.redis.client;

import java.io.IOException;

public interface RedisConnection
{
    /**
     * Redis scan command
     */
    Iterable<Object> scan();

    /**
     * Redis get command
     */
    Object get(Object key) throws IOException, InterruptedException, ClassNotFoundException;

    /**
     * Redis set command
     */

    void set(Object key, Object value) throws IOException, InterruptedException;

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
