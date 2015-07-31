package org.infinispan.persistence.redis.configuration;

import org.infinispan.configuration.cache.StoreConfigurationChildBuilder;

public interface RedisStoreConfigurationChildBuilder<S> extends StoreConfigurationChildBuilder<S>
{
    /**
     * Adds a new remote server
     */
    RedisServerConfigurationBuilder addServer();

    /**
     * The property defines the database ID number
     */
    RedisStoreConfigurationBuilder database(int database);

    /**
     * The property defines the password for accessing Redis
     */
    RedisStoreConfigurationBuilder password(String password);
}
