package org.infinispan.persistence.redis.configuration;

import org.infinispan.configuration.cache.StoreConfigurationChildBuilder;
import org.infinispan.persistence.redis.configuration.RedisStoreConfiguration.Topology;

public interface RedisStoreConfigurationChildBuilder<S> extends StoreConfigurationChildBuilder<S>
{
    /**
     * Adds a new remote server
     */
    RedisServerConfigurationBuilder addServer();

    /**
     * Configures the connection pool
     */
    ConnectionPoolConfigurationBuilder connectionPool();

    /**
     * The property defines the database ID number
     */
    RedisStoreConfigurationBuilder database(int database);

    /**
     * The property defines the password for accessing Redis
     */
    RedisStoreConfigurationBuilder password(String password);

    /**
     * The property defines the topology of the Redis store
     */
    RedisStoreConfigurationBuilder topology(Topology topology);

    /**
     * This property defines the maximum socket connect timeout before giving up connecting to the
     * server.
     */
    RedisStoreConfigurationBuilder connectionTimeout(long connectionTimeout);

    /**
     * This property defines the maximum socket read timeout in milliseconds before giving up waiting
     * for bytes from the server. Defaults to 60000 (1 minute)
     */
    RedisStoreConfigurationBuilder socketTimeout(long socketTimeout);
}
