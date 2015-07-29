package org.infinispan.persistence.redis.configuration;

import org.infinispan.configuration.cache.AbstractStoreConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;

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
        return new RedisStoreConfiguration(attributes.protect(), async.create(), singletonStore.create());
    }

    @Override
    public RedisStoreConfigurationBuilder self()
    {
        return this;
    }

    @Override
    public RedisStoreConfigurationBuilder connectionTimeout(long connectionTimeout)
    {
        attributes.attribute(RedisStoreConfiguration.CONNECTION_TIMEOUT).set(connectionTimeout);
        return this;
    }

    @Override
    public RedisStoreConfigurationBuilder remoteCacheName(String remoteCacheName)
    {
        attributes.attribute(RedisStoreConfiguration.REMOTE_CACHE_NAME).set(remoteCacheName);
        return this;
    }

    @Override
    public RedisStoreConfigurationBuilder socketTimeout(long socketTimeout)
    {
        attributes.attribute(RedisStoreConfiguration.SOCKET_TIMEOUT).set(socketTimeout);
        return this;
    }
}
