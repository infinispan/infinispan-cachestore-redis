package com.barbon.infinispan.redis.configuration;

import org.infinispan.configuration.cache.AbstractStoreConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import static com.barbon.infinispan.redis.configuration.RedisStoreConfiguration.CONNECTION_TIMEOUT;
import static com.barbon.infinispan.redis.configuration.RedisStoreConfiguration.REMOTE_CACHE_NAME;
import static com.barbon.infinispan.redis.configuration.RedisStoreConfiguration.SOCKET_TIMEOUT;

final public class RedisStoreConfigurationBuilder
    extends AbstractStoreConfigurationBuilder<RedisStoreConfiguration, RedisStoreConfigurationBuilder>
    implements RedisStoreConfigurationChildBuilder<RedisStoreConfigurationBuilder>
{
    public RedisStoreConfigurationBuilder(PersistenceConfigurationBuilder builder)
    {
        super(builder);
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

    @Override
    public RedisStoreConfigurationBuilder connectionTimeout(long connectionTimeout)
    {
        attributes.attribute(CONNECTION_TIMEOUT).set(connectionTimeout);
        return this;
    }

    @Override
    public RedisStoreConfigurationBuilder remoteCacheName(String remoteCacheName)
    {
        attributes.attribute(REMOTE_CACHE_NAME).set(remoteCacheName);
        return this;
    }

    @Override
    public RedisStoreConfigurationBuilder socketTimeout(long socketTimeout)
    {
        attributes.attribute(SOCKET_TIMEOUT).set(socketTimeout);
        return this;
    }
}
