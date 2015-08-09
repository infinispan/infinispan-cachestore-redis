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
    public enum Topology
    {
        CLUSTER,
        SENTINEL,
        SERVER
    }

    static final AttributeDefinition<Integer> CONNECTION_TIMEOUT = AttributeDefinition.builder("connectionTimeout", 2000).build();
    static final AttributeDefinition<Integer> SOCKET_TIMEOUT = AttributeDefinition.builder("socketTimeout", 2000).build();
    static final AttributeDefinition<String> MASTER_NAME = AttributeDefinition.builder("masterName", null, String.class).build();
    static final AttributeDefinition<String> PASSWORD = AttributeDefinition.builder("password", null, String.class).build();
    static final AttributeDefinition<Integer> DATABASE = AttributeDefinition.builder("database", 0).build();
    static final AttributeDefinition<Integer> MAX_REDIRECTIONS = AttributeDefinition.builder("maxRedirections", 5).build();
    static final AttributeDefinition<Topology> TOPOLOGY = AttributeDefinition.builder("topology", Topology.CLUSTER).build();
    static final AttributeDefinition<List<RedisServerConfiguration>> SERVERS = AttributeDefinition.builder("servers", null, (Class<List<RedisServerConfiguration>>)(Class<?>)List.class).initializer(new AttributeInitializer<List<RedisServerConfiguration>>() {
        @Override
        public List<RedisServerConfiguration> initialize() {
            return new ArrayList<>();
        }
    }).build();

    public static AttributeSet attributeDefinitionSet() {
        return new AttributeSet(RedisStoreConfiguration.class, AbstractStoreConfiguration.attributeDefinitionSet(),
            PASSWORD, DATABASE, SERVERS, TOPOLOGY, CONNECTION_TIMEOUT, SOCKET_TIMEOUT, MASTER_NAME, MAX_REDIRECTIONS);
    }

    private final ConnectionPoolConfiguration connectionPool;
    private final Attribute<List<RedisServerConfiguration>> servers;
    private final Attribute<Integer> database;
    private final Attribute<String> password;
    private final Attribute<String> masterName;
    private final Attribute<Topology> topology;
    private final Attribute<Integer> socketTimeout;
    private final Attribute<Integer> connectionTimeout;
    private final Attribute<Integer> maxRedirections;

    public RedisStoreConfiguration(
        AttributeSet attributes,
        AsyncStoreConfiguration async,
        SingletonStoreConfiguration singletonStore,
        ConnectionPoolConfiguration connectionPool
    )
    {
        super(attributes, async, singletonStore);
        this.connectionPool = connectionPool;
        this.servers = attributes.attribute(SERVERS);
        this.password = attributes.attribute(PASSWORD);
        this.masterName = attributes.attribute(MASTER_NAME);
        this.database = attributes.attribute(DATABASE);
        this.topology = attributes.attribute(TOPOLOGY);
        this.socketTimeout = attributes.attribute(SOCKET_TIMEOUT);
        this.connectionTimeout = attributes.attribute(CONNECTION_TIMEOUT);
        this.maxRedirections = attributes.attribute(MAX_REDIRECTIONS);
    }

    public List<RedisServerConfiguration> servers()
    {
        return this.servers.get();
    }

    public int database()
    {
        return this.database.get();
    }

    public String password()
    {
        return this.password.get();
    }

    public Topology topology()
    {
        return this.topology.get();
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

    public String masterName()
    {
        return this.masterName.get();
    }

    public int maxRedirections()
    {
        return this.maxRedirections.get();
    }
}
