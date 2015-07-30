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
    static final AttributeDefinition<String> CLIENT_NAME = AttributeDefinition.builder("clientName", "").build();
    static final AttributeDefinition<String> PASSWORD = AttributeDefinition.builder("password", (String) null).build();
    static final AttributeDefinition<Integer> DATABASE = AttributeDefinition.builder("database", 2000).build();
    static final AttributeDefinition<Integer> RETRY_ATTEMPTS = AttributeDefinition.builder("retryAttempts", 2000).build();
    static final AttributeDefinition<Integer> RETRY_INTERVAL = AttributeDefinition.builder("retryInterval", 2000).build();
    static final AttributeDefinition<Integer> EXECUTION_TIMEOUT = AttributeDefinition.builder("executionTimeout", 2000).build();
    static final AttributeDefinition<List<RedisServerConfiguration>> SERVERS = AttributeDefinition.builder("servers", null, (Class<List<RedisServerConfiguration>>)(Class<?>)List.class).initializer(new AttributeInitializer<List<RedisServerConfiguration>>() {
        @Override
        public List<RedisServerConfiguration> initialize() {
            return new ArrayList<>();
        }
    }).build();

    public static AttributeSet attributeDefinitionSet() {
        return new AttributeSet(RedisStoreConfiguration.class, AbstractStoreConfiguration.attributeDefinitionSet(),
            CONNECTION_TIMEOUT, SOCKET_TIMEOUT, CLIENT_NAME, PASSWORD, DATABASE, RETRY_ATTEMPTS, RETRY_INTERVAL, EXECUTION_TIMEOUT, SERVERS);
    }

    private final Attribute<List<RedisServerConfiguration>> servers;
    private final Attribute<Integer> connectionTimeout;
    private final Attribute<Integer> socketTimeout;
    private final Attribute<String> clientName;
    private final Attribute<Integer> database;
    private final Attribute<String> password;
    private final Attribute<Integer> retryAttempts;
    private final Attribute<Integer> retryInterval;
    private final Attribute<Integer> executionTimeout;
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
        this.connectionPool = connectionPool;
        this.servers = attributes.attribute(SERVERS);
        this.clientName = attributes.attribute(CLIENT_NAME);
        this.password = attributes.attribute(PASSWORD);
        this.database = attributes.attribute(DATABASE);
        this.retryAttempts = attributes.attribute(RETRY_ATTEMPTS);
        this.retryInterval = attributes.attribute(RETRY_INTERVAL);
        this.executionTimeout = attributes.attribute(EXECUTION_TIMEOUT);
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

    public int retryAttempts()
    {
        return this.retryAttempts.get();
    }

    public int retryInterval()
    {
        return this.retryInterval.get();
    }

    public int executionTimeout()
    {
        return this.executionTimeout.get();
    }

    public int database()
    {
        return this.database.get();
    }

    public String password()
    {
        return this.password.get();
    }

    public String clientName()
    {
        return this.clientName.get();
    }
}
