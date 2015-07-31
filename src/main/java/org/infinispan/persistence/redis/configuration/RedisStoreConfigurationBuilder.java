package org.infinispan.persistence.redis.configuration;

import org.infinispan.configuration.cache.AbstractStoreConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

final public class RedisStoreConfigurationBuilder
    extends AbstractStoreConfigurationBuilder<RedisStoreConfiguration, RedisStoreConfigurationBuilder>
    implements RedisStoreConfigurationChildBuilder<RedisStoreConfigurationBuilder>
{
    private List<RedisServerConfigurationBuilder> servers = new ArrayList<RedisServerConfigurationBuilder>();

    public RedisStoreConfigurationBuilder(PersistenceConfigurationBuilder builder)
    {
        super(builder, RedisStoreConfiguration.attributeDefinitionSet());
    }

    @Override
    public RedisStoreConfiguration create()
    {
        return new RedisStoreConfiguration(this.attributes.protect(), this.async.create(), this.singletonStore.create());
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
    public RedisStoreConfigurationBuilder clientName(String clientName)
    {
        this.attributes.attribute(RedisStoreConfiguration.CLIENT_NAME).set(clientName);
        return this;
    }

    @Override
    public RedisStoreConfigurationBuilder database(int database)
    {
        this.attributes.attribute(RedisStoreConfiguration.DATABASE).set(database);
        return this;
    }

    @Override
    public RedisStoreConfigurationBuilder password(String password)
    {
        this.attributes.attribute(RedisStoreConfiguration.PASSWORD).set(password);
        return this;
    }

    @Override
    public RedisStoreConfigurationBuilder retryAttempts(int retryAttempts)
    {
        this.attributes.attribute(RedisStoreConfiguration.RETRY_ATTEMPTS).set(retryAttempts);
        return this;
    }

    @Override
    public RedisStoreConfigurationBuilder retryInterval(int retryInterval)
    {
        this.attributes.attribute(RedisStoreConfiguration.RETRY_INTERVAL).set(retryInterval);
        return this;
    }

    @Override
    public RedisStoreConfigurationBuilder executionTimeout(int executionTimeout)
    {
        this.attributes.attribute(RedisStoreConfiguration.EXECUTION_TIMEOUT).set(executionTimeout);
        return this;
    }

    @Override
    public RedisServerConfigurationBuilder addServer()
    {
        RedisServerConfigurationBuilder builder = new RedisServerConfigurationBuilder(this);
        this.servers.add(builder);
        return builder;
    }
}
