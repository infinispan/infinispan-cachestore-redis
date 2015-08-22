package org.infinispan.persistence.redis;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.persistence.BaseStoreTest;
import org.infinispan.persistence.redis.configuration.RedisStoreConfigurationBuilder;
import org.infinispan.persistence.redis.support.RedisCluster;
import org.infinispan.persistence.spi.AdvancedLoadWriteStore;
import org.infinispan.test.TestingUtil;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import org.infinispan.persistence.redis.configuration.RedisStoreConfiguration.Topology;

import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

@Test(testName = "persistence.redis.RedisStoreTest", groups = "functional")
public class RedisClusterStoreTest extends BaseStoreTest
{
    RedisCluster redisCluster;

    @BeforeClass(alwaysRun = true)
    public void beforeClass()
        throws IOException
    {
        System.out.println("RedisClusterStoreTest:Setting up");
        redisCluster = new RedisCluster();
        redisCluster.start();
    }

    @AfterClass(alwaysRun = true)
    public void afterClass()
    {
        System.out.println("RedisClusterStoreTest:Tearing down");
        redisCluster.kill();
    }

    @Override
    protected AdvancedLoadWriteStore createStore() throws Exception
    {
        ConfigurationBuilder builder = TestCacheManagerFactory.getDefaultCacheConfiguration(false);
        RedisStoreConfigurationBuilder storeConfigurationBuilder = builder.persistence().addStore(RedisStoreConfigurationBuilder.class);
        storeConfigurationBuilder
            .topology(Topology.CLUSTER)
            .addServer()
                .host("localhost")
                .port(6379)
            .addServer()
                .host("localhost")
                .port(6380)
            .addServer()
                .host("localhost")
                .port(6381)
        ;

        RedisStore store = new RedisStore();
        store.init(createContext(builder.build()));

        return store;
    }

    @Override
    public void testPreload()
    {
        // No support for pre-load
    }

    @Override
    public void testReplaceExpiredEntry() throws Exception
    {
        // Redis expires entries for us, so load can't return expired entries
        // Override the unit to prevent null pointer exception
        cl.write(marshalledEntry(internalCacheEntry("k1", "v1", 3000L)));
        assertNull(cl.load("k1"));
        long start = System.currentTimeMillis() + 100L;
        cl.write(marshalledEntry(internalCacheEntry("k1", "v2", start)));
        assertTrue(cl.load("k1").getValue().equals("v2"));
    }
}
