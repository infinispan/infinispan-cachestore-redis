package org.infinispan.persistence.redis;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.persistence.BaseStoreTest;
import org.infinispan.persistence.redis.configuration.RedisStoreConfigurationBuilder;
import org.infinispan.persistence.spi.AdvancedLoadWriteStore;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.testng.annotations.Test;

@Test(testName = "persistence.redis.RedisStoreTest", groups = "functional")
public class RedisStoreTest extends BaseStoreTest
{
    @Override
    protected AdvancedLoadWriteStore createStore() throws Exception
    {
        ConfigurationBuilder builder = TestCacheManagerFactory.getDefaultCacheConfiguration(false);
        RedisStoreConfigurationBuilder storeConfigurationBuilder = builder.persistence().addStore(RedisStoreConfigurationBuilder.class);
        storeConfigurationBuilder
            .addServer()
                .host("localhost")
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
