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
    public RedisStoreConfigurationBuilder database(int database)
    {
        return builder.database(database);
    }

    @Override
    public RedisStoreConfigurationBuilder password(String password)
    {
        return builder.password(password);
    }
}
