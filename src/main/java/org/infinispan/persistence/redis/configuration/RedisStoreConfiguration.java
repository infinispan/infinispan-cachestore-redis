package org.infinispan.persistence.redis.configuration;

import org.infinispan.commons.configuration.attributes.AttributeSet;
import org.infinispan.persistence.redis.RedisStore;
import org.infinispan.commons.api.BasicCacheContainer;
import org.infinispan.commons.configuration.BuiltBy;
import org.infinispan.commons.configuration.ConfigurationFor;
import org.infinispan.commons.configuration.attributes.AttributeDefinition;
import org.infinispan.configuration.cache.AbstractStoreConfiguration;
import org.infinispan.configuration.cache.AsyncStoreConfiguration;
import org.infinispan.configuration.cache.SingletonStoreConfiguration;

import java.util.Properties;

@BuiltBy(RedisStoreConfigurationBuilder.class)
@ConfigurationFor(RedisStore.class)
final public class RedisStoreConfiguration extends AbstractStoreConfiguration
{
    static final AttributeDefinition<Long> CONNECTION_TIMEOUT = AttributeDefinition.builder("connectionTimeout", 30L).build();
    static final AttributeDefinition<String> REMOTE_CACHE_NAME = AttributeDefinition.builder("remoteCacheName", BasicCacheContainer.DEFAULT_CACHE_NAME).immutable().build();
    static final AttributeDefinition<Long> SOCKET_TIMEOUT = AttributeDefinition.builder("socketTimeout", 30L).build();

    public RedisStoreConfiguration(
        AttributeSet attributes,
        AsyncStoreConfiguration async,
        SingletonStoreConfiguration singletonStore
    )
    {
        super(attributes, async, singletonStore);
    }
}
