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
    public RedisStoreConfigurationBuilder self()
    {
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
    public RedisServerConfigurationBuilder addServer()
    {
        RedisServerConfigurationBuilder builder = new RedisServerConfigurationBuilder(this);
        this.servers.add(builder);
        return builder;
    }

    @Override
    public RedisStoreConfigurationBuilder read(RedisStoreConfiguration template)
    {
        super.read(template);
        for (RedisServerConfiguration server : template.servers()) {
            this.addServer().ssl(server.ssl()).host(server.host()).port(server.port());
        }

        return this;
    }

    @Override
    public RedisStoreConfiguration create()
    {
        List<RedisServerConfiguration> redisServers = new ArrayList<RedisServerConfiguration>();
        for (RedisServerConfigurationBuilder server : servers) {
            redisServers.add(server.create());
        }
        attributes.attribute(RedisStoreConfiguration.SERVERS).set(redisServers);
        return new RedisStoreConfiguration(this.attributes.protect(), this.async.create(), this.singletonStore.create());
    }
}
