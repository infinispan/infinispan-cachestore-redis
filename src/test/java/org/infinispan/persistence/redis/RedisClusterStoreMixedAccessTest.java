package org.infinispan.persistence.redis;

import org.infinispan.persistence.redis.support.RedisCluster;
import org.infinispan.test.AbstractInfinispanTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

@Test(testName = "persistence.redi.RedisStoreMixedAccessTest", groups="functional")
public class RedisClusterStoreMixedAccessTest extends AbstractInfinispanTest
{
    RedisCluster redisCluster;

//    @BeforeTest
//    public void startUp()
//        throws IOException
//    {
//        System.out.println("RedisClusterStoreMixedAccessTest:Setting up");
//        redisCluster = new RedisCluster();
//        redisCluster.start();
//    }
//
//    @AfterClass(alwaysRun = true)
//    public void tearDown()
//    {
//        System.out.println("RedisClusterStoreMixedAccessTest:Tearing down");
//        redisCluster.kill();
//    }
}
