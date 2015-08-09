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
    public ConnectionPoolConfigurationBuilder connectionPool()
    {
        return builder.connectionPool();
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
    public RedisStoreConfigurationBuilder masterName(String masterName)
    {
        return builder.masterName(masterName);
    }

    @Override
    public RedisStoreConfigurationBuilder maxRedirections(int maxRedirections)
    {
        return builder.maxRedirections(maxRedirections);
    }

    @Override
    public RedisStoreConfigurationBuilder topology(Topology topology)
    {
        return builder.topology(topology);
    }

    @Override
    public RedisStoreConfigurationBuilder socketTimeout(int socketTimeout)
    {
        return builder.socketTimeout(socketTimeout);
    }

    @Override
    public RedisStoreConfigurationBuilder connectionTimeout(int connectionTimeout)
    {
        return builder.connectionTimeout(connectionTimeout);
    }
}
