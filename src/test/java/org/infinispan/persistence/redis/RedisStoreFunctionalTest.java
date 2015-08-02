package org.infinispan.persistence.redis;

import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import org.infinispan.persistence.BaseStoreFunctionalTest;
import org.infinispan.persistence.redis.configuration.RedisStoreConfigurationBuilder;
import org.testng.annotations.Test;

@Test(testName = "persistence.redis.RedisStoreFunctionalTest", groups = "functional")
public class RedisStoreFunctionalTest extends BaseStoreFunctionalTest
{
    @Override
    protected PersistenceConfigurationBuilder createCacheStoreConfig(
        PersistenceConfigurationBuilder persistence,
        boolean b
    )
    {
        persistence
            .addStore(RedisStoreConfigurationBuilder.class)
            .addServer()
                .host("localhost")
        ;

        return persistence;
    }
}
