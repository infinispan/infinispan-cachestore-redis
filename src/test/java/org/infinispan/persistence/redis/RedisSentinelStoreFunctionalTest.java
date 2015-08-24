package org.infinispan.persistence.redis;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import org.infinispan.persistence.BaseStoreFunctionalTest;
import org.infinispan.persistence.redis.configuration.RedisStoreConfiguration.Topology;
import org.infinispan.persistence.redis.configuration.RedisStoreConfigurationBuilder;
import org.infinispan.persistence.redis.support.RedisSentinel;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(testName = "persistence.redis.RedisServerStoreFunctionalTest", groups = "functional")
public class RedisSentinelStoreFunctionalTest extends BaseStoreFunctionalTest
{
    private RedisSentinel redisServer;

    @BeforeClass(alwaysRun = true)
    public void beforeClass()
        throws Exception
    {
        redisServer = new RedisSentinel();
        redisServer.start();
    }

    @AfterClass(alwaysRun = true)
    public void afterClass()
    {
        redisServer.kill();
    }

    @Override
    protected PersistenceConfigurationBuilder createCacheStoreConfig(
        PersistenceConfigurationBuilder persistence,
        boolean b
    )
    {
        return createCacheStoreConfig(persistence, b, 0);
    }

    protected PersistenceConfigurationBuilder createCacheStoreConfig(
        PersistenceConfigurationBuilder persistence,
        boolean b,
        int database
    )
    {
        persistence
            .addStore(RedisStoreConfigurationBuilder.class)
            .topology(Topology.SERVER)
            .addServer()
            .host("localhost")
            .port(6379)
            .database(database)
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
        ConfigurationBuilder cb1 = new ConfigurationBuilder();
        cb1.read(this.cacheManager.getDefaultCacheConfiguration());
        this.createCacheStoreConfig(cb1.persistence(), false, 0);
        Configuration c1 = cb1.build();

        ConfigurationBuilder cb2 = new ConfigurationBuilder();
        cb2.read(this.cacheManager.getDefaultCacheConfiguration());
        this.createCacheStoreConfig(cb2.persistence(), false, 1);
        Configuration c2 = cb2.build();

        this.cacheManager.defineConfiguration("testTwoCachesSameCacheStore-1", c1);
        this.cacheManager.defineConfiguration("testTwoCachesSameCacheStore-2", c2);
        Cache first = this.cacheManager.getCache("testTwoCachesSameCacheStore-1");
        Cache second = this.cacheManager.getCache("testTwoCachesSameCacheStore-2");

        first.start();
        second.start();
        first.put("key", this.wrap("key", "val"));
        Assert.assertEquals("val", this.unwrap(first.get("key")));
        Assert.assertNull(second.get("key"));
        second.put("key2", this.wrap("key2", "val2"));
        Assert.assertEquals("val2", this.unwrap(second.get("key2")));
        Assert.assertNull(first.get("key2"));
    }
}
