package org.infinispan.persistence.redis;

import org.infinispan.persistence.redis.support.RedisCluster;
import org.infinispan.test.AbstractInfinispanTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

@Test(testName = "persistence.redi.RedisStoreMixedAccessTest", groups="functional")
public class RedisStoreMixedAccessTest extends AbstractInfinispanTest
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

    public void testMixedAccess()
    {

    }

    public void testMixedAccessWithLifespan()
    {

    }

    public void testMixedAccessWithLifespanAndMaxIdle()
    {

    }
}
