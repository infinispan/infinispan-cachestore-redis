package org.infinispan.persistence.redis.configuration;

import org.infinispan.configuration.cache.StoreConfigurationChildBuilder;

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
     * This property defines the maximum socket connect timeout before giving up connecting to the
     * server.
     */
    RedisStoreConfigurationBuilder connectionTimeout(int connectionTimeout);

    /**
     * This property defines the maximum socket read timeout in milliseconds before giving up waiting
     * for bytes from the server. Defaults to 60000 (1 minute)
     */
    RedisStoreConfigurationBuilder socketTimeout(int socketTimeout);

    /**
     * Maximum number of redirections permitted when connecting to the cluster
     */
    RedisStoreConfigurationBuilder maxRedirections(int maxRedirections);
}
