package org.infinispan.persistence.redis.configuration;

import org.infinispan.commons.configuration.attributes.*;
import org.infinispan.persistence.redis.RedisStore;
import org.infinispan.commons.configuration.BuiltBy;
import org.infinispan.commons.configuration.ConfigurationFor;
import org.infinispan.commons.configuration.attributes.Attribute;
import org.infinispan.configuration.cache.AbstractStoreConfiguration;
import org.infinispan.configuration.cache.AsyncStoreConfiguration;
import org.infinispan.configuration.cache.SingletonStoreConfiguration;

import java.util.ArrayList;
import java.util.List;


@BuiltBy(RedisStoreConfigurationBuilder.class)
@ConfigurationFor(RedisStore.class)
final public class RedisStoreConfiguration extends AbstractStoreConfiguration
{
    static final AttributeDefinition<Integer> CONNECTION_TIMEOUT = AttributeDefinition.builder("connectionTimeout", 2000).build();
    static final AttributeDefinition<Integer> SOCKET_TIMEOUT = AttributeDefinition.builder("socketTimeout", 2000).build();
    static final AttributeDefinition<Integer> MAX_REDIRECTIONS = AttributeDefinition.builder("maxRedirections", 5).build();
    static final AttributeDefinition<List<RedisServerConfiguration>> SERVERS = AttributeDefinition.builder("servers", null, (Class<List<RedisServerConfiguration>>)(Class<?>)List.class).initializer(new AttributeInitializer<List<RedisServerConfiguration>>() {
        @Override
        public List<RedisServerConfiguration> initialize() {
            return new ArrayList<>();
        }
    }).build();

    private final Attribute<List<RedisServerConfiguration>> servers;
    private final Attribute<Integer> connectionTimeout;
    private final Attribute<Integer> socketTimeout;
    private final Attribute<Integer> maxRedirections;
    private final ConnectionPoolConfiguration connectionPool;

    public RedisStoreConfiguration(
        AttributeSet attributes,
        AsyncStoreConfiguration async,
        SingletonStoreConfiguration singletonStore,
        ConnectionPoolConfiguration connectionPool
    )
    {
        super(attributes, async, singletonStore);
        this.connectionTimeout = attributes.attribute(CONNECTION_TIMEOUT);
        this.socketTimeout = attributes.attribute(SOCKET_TIMEOUT);
        this.maxRedirections = attributes.attribute(MAX_REDIRECTIONS);
        this.connectionPool = connectionPool;
        this.servers = attributes.attribute(SERVERS);
    }

    public List<RedisServerConfiguration> servers()
    {
        return this.servers.get();
    }

    public ConnectionPoolConfiguration connectionPool()
    {
        return this.connectionPool;
    }

    public int connectionTimeout()
    {
        return this.connectionTimeout.get();
    }

    public int socketTimeout()
    {
        return this.socketTimeout.get();
    }

    public int maxRedirections()
    {
        return this.maxRedirections.get();
    }
}
