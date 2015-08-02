package org.infinispan.persistence.redis;

import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import org.infinispan.persistence.BaseStoreFunctionalTest;
import org.infinispan.persistence.redis.configuration.RedisStoreConfigurationBuilder;
import org.infinispan.persistence.redis.support.RedisCluster;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

@Test(testName = "persistence.redis.RedisStoreFunctionalTest", groups = "functional")
public class RedisStoreFunctionalTest extends BaseStoreFunctionalTest
{
    RedisCluster redisCluster;

    @BeforeTest
    public void startUp()
        throws IOException
    {
        redisCluster = new RedisCluster();
        redisCluster.start();
    }

    @AfterTest
    public void teardown()
    {
        try {
            super.teardown();
        }
        finally {
            redisCluster.kill();
        }
    }

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
            .port(16379)
        ;

        return persistence;
    }
}
