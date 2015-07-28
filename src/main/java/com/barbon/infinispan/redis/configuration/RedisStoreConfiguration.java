package com.barbon.infinispan.redis.configuration;

import com.barbon.infinispan.redis.RedisStore;
import org.infinispan.commons.configuration.BuiltBy;
import org.infinispan.commons.configuration.ConfigurationFor;
import org.infinispan.configuration.cache.AbstractStoreConfiguration;
import org.infinispan.configuration.cache.AsyncStoreConfiguration;
import org.infinispan.configuration.cache.SingletonStoreConfiguration;

import java.util.Properties;

@BuiltBy(RedisStoreConfigurationBuilder.class)
@ConfigurationFor(RedisStore.class)
final public class RedisStoreConfiguration extends AbstractStoreConfiguration
{
    public RedisStoreConfiguration(
        boolean purgeOnStartup,
        boolean fetchPersistentState,
        boolean ignoreModifications,
        AsyncStoreConfiguration async,
        SingletonStoreConfiguration singletonStore,
        boolean preload,
        boolean shared,
        Properties properties
    )
    {
        super(purgeOnStartup, fetchPersistentState, ignoreModifications, async, singletonStore, preload, shared, properties);
    }
}
