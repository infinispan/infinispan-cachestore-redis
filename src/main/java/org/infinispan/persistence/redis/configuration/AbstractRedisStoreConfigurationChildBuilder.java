package org.infinispan.persistence.redis.configuration;

import org.infinispan.configuration.cache.AbstractStoreConfigurationChildBuilder;

public abstract class AbstractRedisStoreConfigurationChildBuilder<S> extends AbstractStoreConfigurationChildBuilder<S>
    implements RedisStoreConfigurationChildBuilder<S>
{
    private final RedisStoreConfigurationBuilder builder;

    protected AbstractRedisStoreConfigurationChildBuilder(
        RedisStoreConfigurationBuilder builder
    )
    {
        super(builder);
        this.builder = builder;
    }

    @Override
    public RedisServerConfigurationBuilder addServer()
    {
        return builder.addServer();
    }

    @Override
    public RedisStoreConfigurationBuilder connectionTimeout(int connectionTimeout)
    {
        return builder.connectionTimeout(connectionTimeout);
    }

    @Override
    public RedisStoreConfigurationBuilder socketTimeout(int socketTimeout)
    {
        return builder.connectionTimeout(socketTimeout);
    }

    @Override
    public RedisStoreConfigurationBuilder clientName(String clientName)
    {
        return builder.clientName(clientName);
    }

    @Override
    public RedisStoreConfigurationBuilder database(int database)
    {
        return builder.database(database);
    }

    @Override
    public RedisStoreConfigurationBuilder password(String password)
    {
        return builder.password(password);
    }

    @Override
    public RedisStoreConfigurationBuilder retryAttempts(int retryAttempts)
    {
        return builder.retryAttempts(retryAttempts);
    }

    @Override
    public RedisStoreConfigurationBuilder retryInterval(int retryInterval)
    {
        return builder.retryInterval(retryInterval);
    }

    @Override
    public RedisStoreConfigurationBuilder executionTimeout(int executionTimeout)
    {
        return builder.executionTimeout(executionTimeout);
    }
}
