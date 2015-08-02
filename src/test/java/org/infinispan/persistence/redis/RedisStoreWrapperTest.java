package org.infinispan.persistence.redis;

import org.infinispan.persistence.redis.support.RedisCluster;
import org.infinispan.test.AbstractInfinispanTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

@Test(testName = "persistence.redis.RedisStoreWrapperTest", groups="functional")
public class RedisStoreWrapperTest extends AbstractInfinispanTest
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
    public void tearDown()
    {
        redisCluster.kill();
    }

    public void testEntryWrapping() throws Exception
    {

    }
}
