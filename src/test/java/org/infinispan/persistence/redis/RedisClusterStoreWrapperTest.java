package org.infinispan.persistence.redis;

import org.infinispan.persistence.redis.support.RedisCluster;
import org.infinispan.test.AbstractInfinispanTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

@Test(testName = "persistence.redis.RedisStoreWrapperTest", groups="functional")
public class RedisClusterStoreWrapperTest extends AbstractInfinispanTest
{
    RedisCluster redisCluster;

//    @BeforeClass(alwaysRun = true)
//    public void beforeClass()
//        throws IOException
//    {
//        System.out.println("RedisClusterStoreWrapperTest:Setting up");
//        redisCluster = new RedisCluster();
//        redisCluster.start();
//    }
//
//    @AfterClass(alwaysRun = true)
//    public void afterClass()
//    {
//        System.out.println("RedisClusterStoreWrapperTest:Tearing down");
//        redisCluster.kill();
//    }
}
