package org.infinispan.persistence.redis.configuration;

import org.infinispan.configuration.cache.AbstractStoreConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

final public class RedisStoreConfigurationBuilder
    extends AbstractStoreConfigurationBuilder<RedisStoreConfiguration, RedisStoreConfigurationBuilder>
    implements RedisStoreConfigurationChildBuilder<RedisStoreConfigurationBuilder>
{
    private final ConnectionPoolConfigurationBuilder connectionPool;
    private List<RedisServerConfigurationBuilder> servers = new ArrayList<RedisServerConfigurationBuilder>();

    public RedisStoreConfigurationBuilder(PersistenceConfigurationBuilder builder)
    {
        super(builder);
        connectionPool = new ConnectionPoolConfigurationBuilder(this);
    }

    @Override
    public RedisStoreConfiguration create()
    {
        return new RedisStoreConfiguration(this.attributes.protect(), this.async.create(),
            this.singletonStore.create(), this.connectionPool.create());
    }

    @Override
    public RedisStoreConfigurationBuilder self()
    {
        return this;
    }

    @Override
    public RedisStoreConfigurationBuilder connectionTimeout(int connectionTimeout)
    {
        this.attributes.attribute(RedisStoreConfiguration.CONNECTION_TIMEOUT).set(connectionTimeout);
        return this;
    }

    @Override
    public RedisStoreConfigurationBuilder socketTimeout(int socketTimeout)
    {
        this.attributes.attribute(RedisStoreConfiguration.SOCKET_TIMEOUT).set(socketTimeout);
        return this;
    }

    @Override
    public RedisStoreConfigurationBuilder maxRedirections(int maxRedirections)
    {
        this.attributes.attribute(RedisStoreConfiguration.MAX_REDIRECTIONS).set(maxRedirections);
        return this;
    }

    @Override
    public RedisServerConfigurationBuilder addServer()
    {
        RedisServerConfigurationBuilder builder = new RedisServerConfigurationBuilder(this);
        this.servers.add(builder);
        return builder;
    }

    @Override
    public ConnectionPoolConfigurationBuilder connectionPool()
    {
        return this.connectionPool;
    }
}
