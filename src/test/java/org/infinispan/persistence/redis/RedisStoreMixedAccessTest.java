package org.infinispan.persistence.redis;

import org.infinispan.persistence.redis.support.RedisCluster;
import org.infinispan.test.AbstractInfinispanTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

@Test(testName = "persistence.redi.RedisStoreMixedAccessTest", groups="functional")
public class RedisStoreMixedAccessTest extends AbstractInfinispanTest
{
    RedisCluster redisCluster;

    @BeforeTest(alwaysRun = true)
    public void startUp()
        throws IOException
    {
        System.out.println("RedisStoreMixedAccessTest:Setting up");
        redisCluster = new RedisCluster();
        redisCluster.start();
    }

    @AfterClass
    public void tearDown()
    {
        System.out.println("RedisStoreMixedAccessTest:Tearing down");
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
