package org.infinispan.persistence.redis;

import org.infinispan.persistence.redis.support.RedisCluster;
import org.infinispan.test.AbstractInfinispanTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

@Test(testName = "persistence.redis.RedisStoreWrapperTest", groups="functional")
public class RedisClusterStoreWrapperTest extends AbstractInfinispanTest
{
    RedisCluster redisCluster;

//    @BeforeTest
//    public void startUp()
//        throws IOException
//    {
//        System.out.println("RedisClusterStoreWrapperTest:Setting up");
//        redisCluster = new RedisCluster();
//        redisCluster.start();
//    }
//
//    @AfterClass(alwaysRun = true)
//    public void tearDown()
//    {
//        System.out.println("RedisClusterStoreWrapperTest:Tearing down");
//        redisCluster.kill();
//    }
}
