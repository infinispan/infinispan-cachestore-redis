package org.infinispan.persistence.redis;

import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import org.infinispan.persistence.BaseStoreFunctionalTest;
import org.infinispan.persistence.redis.configuration.RedisStoreConfigurationBuilder;
import org.infinispan.persistence.redis.support.RedisCluster;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test(testName = "persistence.redis.RedisStoreFunctionalTest", groups = "functional")
public class RedisStoreFunctionalTest extends BaseStoreFunctionalTest
{
    RedisCluster redisCluster;

    @BeforeTest(alwaysRun = true)
    public void startUp()
        throws Exception
    {
        System.out.println("RedisStoreFunctionalTest:Setting up");
        redisCluster = new RedisCluster();
        redisCluster.start();
    }

    @AfterClass
    public void tearDown()
    {
        System.out.println("RedisStoreFunctionalTest:Tearing down");
        redisCluster.kill();
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
            .port(6379)
        ;

        return persistence;
    }
}
