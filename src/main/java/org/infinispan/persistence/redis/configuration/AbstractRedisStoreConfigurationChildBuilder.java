package org.infinispan.persistence.redis.configuration;

import org.infinispan.configuration.cache.AbstractStoreConfigurationChildBuilder;
import org.infinispan.persistence.redis.configuration.RedisStoreConfiguration.Topology;

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

    @Override
    public RedisStoreConfigurationBuilder topology(Topology topology)
    {
        return builder.topology(topology);
    }
}
