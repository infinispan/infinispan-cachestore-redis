package org.infinispan.persistence.redis;

import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import org.infinispan.persistence.BaseStoreFunctionalTest;
import org.infinispan.persistence.redis.configuration.RedisStoreConfigurationBuilder;
import org.infinispan.persistence.redis.support.RedisCluster;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.infinispan.persistence.redis.configuration.RedisStoreConfiguration.Topology;

@Test(testName = "persistence.redis.RedisClusterStoreFunctionalTest", groups = "functional")
public class RedisClusterStoreFunctionalTest extends BaseStoreFunctionalTest
{
    private RedisCluster redisCluster;

    @BeforeClass(alwaysRun = true)
    public void beforeClass()
        throws Exception
    {
        System.out.println("RedisClusterStoreFunctionalTest:Setting up");
        redisCluster = new RedisCluster();
        redisCluster.start();
    }

    @AfterClass(alwaysRun = true)
    public void afterClass()
    {
        System.out.println("RedisClusterStoreFunctionalTest:Tearing down");
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
            .topology(Topology.CLUSTER)
            .addServer()
            .host("localhost")
            .port(6379)
        ;

        return persistence;
    }

    @Override
    public void testPreloadAndExpiry()
    {
        // No support for pre-load
    }

    @Override
    public void testPreloadStoredAsBinary()
    {
        // No support for pre-load
    }

    @Override
    public void testTwoCachesSameCacheStore()
    {
        // Cluster mode does not support database index selection, and so the cache store cannot
        // support two cache stores using the same clustered Redis backend.
    }
}
