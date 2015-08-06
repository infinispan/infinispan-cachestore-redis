package org.infinispan.persistence.redis;

import org.infinispan.persistence.redis.support.RedisCluster;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

@Test(testName = "persistence.redis.RemoteStoreConfigTest", groups = "functional")
public class RedisClusterStoreConfigTest
{
    public static final String CACHE_LOADER_CONFIG = "redis-cl-config.xml";
    RedisCluster redisCluster;

    @BeforeTest(alwaysRun = true)
    public void startUp()
        throws IOException
    {
        System.out.println("RedisStoreConfigTest:Setting up");
        redisCluster = new RedisCluster();
        redisCluster.start();
    }

    @AfterTest
    public void tearDown()
    {
        System.out.println("RedisStoreConfigTest:Tearing down");
        redisCluster.kill();
    }
}
