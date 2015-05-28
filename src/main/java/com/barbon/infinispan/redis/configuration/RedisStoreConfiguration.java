package com.barbon.infinispan.redis.configuration;

import com.barbon.infinispan.redis.RedisStore;
import org.infinispan.commons.configuration.BuiltBy;
import org.infinispan.commons.configuration.ConfigurationFor;
import org.infinispan.commons.configuration.attributes.AttributeSet;
import org.infinispan.configuration.cache.AbstractStoreConfiguration;
import org.infinispan.configuration.cache.AsyncStoreConfiguration;
import org.infinispan.configuration.cache.SingletonStoreConfiguration;
import org.infinispan.configuration.global.ExecutorFactoryConfiguration;

@BuiltBy(RedisStoreConfigurationBuilder.class)
@ConfigurationFor(RedisStore.class)
final public class RedisStoreConfiguration extends AbstractStoreConfiguration
{
    public RedisStoreConfiguration(
        AttributeSet attributes,
        AsyncStoreConfiguration async,
        SingletonStoreConfiguration singletonStore,
        ExecutorFactoryConfiguration asyncExecutorFactory,
        ConnectionPoolConfiguration connectionPool
    )
    {
        super(attributes, async, singletonStore);
    }

    public static AttributeSet attributeDefinitionSet()
    {
        return new AttributeSet(RedisStoreConfiguration.class, AbstractStoreConfiguration.attributeDefinitionSet());
    }
}
