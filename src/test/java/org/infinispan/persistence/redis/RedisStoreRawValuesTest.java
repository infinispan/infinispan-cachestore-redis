package org.infinispan.persistence.redis;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.persistence.BaseStoreTest;
import org.infinispan.persistence.redis.configuration.RedisStoreConfigurationBuilder;
import org.infinispan.persistence.redis.support.RedisCluster;
import org.infinispan.persistence.spi.AdvancedLoadWriteStore;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

@Test(testName = "persistence.redis.RedisStoreRawValuesTest", groups = "functional")
public class RedisStoreRawValuesTest extends BaseStoreTest
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
        try {
            super.tearDown();
        }
        finally {
            redisCluster.kill();
        }
    }

    @Override
    protected AdvancedLoadWriteStore createStore() throws Exception
    {
        ConfigurationBuilder builder = TestCacheManagerFactory.getDefaultCacheConfiguration(false);
        RedisStoreConfigurationBuilder storeConfigurationBuilder = builder.persistence().addStore(RedisStoreConfigurationBuilder.class);
        storeConfigurationBuilder
            .addServer()
            .host("localhost")
            .port(16379)
        ;

        RedisStore store = new RedisStore();
        store.init(createContext(builder.build()));

        return store;
    }

    @Override
    public void testReplaceExpiredEntry() throws Exception
    {

    }
}
