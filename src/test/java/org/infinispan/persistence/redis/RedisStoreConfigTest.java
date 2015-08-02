package org.infinispan.persistence.redis;

import org.infinispan.persistence.redis.support.RedisCluster;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

@Test(testName = "persistence.redis.RemoteStoreConfigTest", groups = "functional")
public class RedisStoreConfigTest
{
    public static final String CACHE_LOADER_CONFIG = "redis-cl-config.xml";
    RedisCluster redisCluster;

    @BeforeTest
    public void startUp()
        throws IOException
    {
        redisCluster = new RedisCluster();
        redisCluster.start();
    }

    @AfterTest
    public void tearDown()
    {
        redisCluster.kill();
    }
}
