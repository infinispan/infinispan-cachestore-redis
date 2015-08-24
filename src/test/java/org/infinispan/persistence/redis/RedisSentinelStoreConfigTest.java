package org.infinispan.persistence.redis;

import org.infinispan.Cache;
import org.infinispan.persistence.redis.support.RedisSentinel;
import org.infinispan.persistence.spi.CacheLoader;
import org.infinispan.test.AbstractInfinispanTest;
import org.infinispan.test.CacheManagerCallable;
import org.infinispan.test.TestingUtil;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.infinispan.test.TestingUtil.withCacheManager;
import static org.junit.Assert.assertEquals;

@Test(testName = "persistence.redis.RedisServerStoreConfigTest", groups = "functional")
public class RedisSentinelStoreConfigTest extends AbstractInfinispanTest
{
    private static final String CACHE_LOADER_CONFIG = "redis-sentinel-cl-config.xml";
    private RedisSentinel redisServer;

    @BeforeTest(alwaysRun = true)
    public void beforeTest()
        throws IOException
    {
        System.out.println("RedisSentinelStoreConfigTest:Setting up");
        redisServer = new RedisSentinel();
        redisServer.start();
    }

    public void simpleTest() throws Exception
    {
        withCacheManager(new CacheManagerCallable(TestCacheManagerFactory.fromXml(CACHE_LOADER_CONFIG)) {
            @Override
            public void call() {
                Cache<Object, Object> cache = cm.getCache();
                CacheLoader cacheLoader = TestingUtil.getCacheLoader(cache);
                assert cacheLoader != null;
                assert cacheLoader instanceof RedisStore;

                cache.put("k", "v");

                assertEquals(1, cm.getCache().size());
                cache.stop();
            }
        });
    }

    @AfterTest(alwaysRun = true)
    public void afterTest()
    {
        System.out.println("RedisSentinelStoreConfigTest:Tearing down");
        this.redisServer.kill();
    }
}
