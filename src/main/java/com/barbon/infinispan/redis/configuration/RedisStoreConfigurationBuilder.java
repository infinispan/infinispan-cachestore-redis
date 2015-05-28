package com.barbon.infinispan.redis.configuration;

import org.infinispan.configuration.cache.AbstractStoreConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;

final public class RedisStoreConfigurationBuilder
    extends AbstractStoreConfigurationBuilder<RedisStoreConfiguration, RedisStoreConfigurationBuilder>
    implements RedisStoreConfigurationChildBuilder<RedisStoreConfigurationBuilder>
{
    public RedisStoreConfigurationBuilder(PersistenceConfigurationBuilder builder)
    {
        super(builder, RedisStoreConfiguration.attributeDefinitionSet());
    }

    @Override
    public RedisStoreConfiguration create()
    {
        return null;
    }

    @Override
    public RedisStoreConfigurationBuilder self()
    {
        return null;
    }
}
