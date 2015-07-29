package com.barbon.infinispan.redis.configuration;

import org.infinispan.configuration.cache.StoreConfigurationChildBuilder;

public interface RedisStoreConfigurationChildBuilder<S> extends StoreConfigurationChildBuilder<S>
{
    /**
     * This property defines the maximum socket connect timeout before giving up connecting to the
     * server.
     */
    RedisStoreConfigurationBuilder connectionTimeout(long connectionTimeout);

    /**
     * The name of the remote cache in the remote infinispan cluster, to which to connect to. If
     * unspecified, the default cache will be used
     */
    RedisStoreConfigurationBuilder remoteCacheName(String remoteCacheName);

    /**
     * This property defines the maximum socket read timeout in milliseconds before giving up waiting
     * for bytes from the server. Defaults to 60000 (1 minute)
     */
    RedisStoreConfigurationBuilder socketTimeout(long socketTimeout);
}
