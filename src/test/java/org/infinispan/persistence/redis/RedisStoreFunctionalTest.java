package org.infinispan.persistence.redis;

import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import org.infinispan.persistence.BaseStoreFunctionalTest;
import org.testng.annotations.Test;

@Test(testName = "persistence.redis.RedisStoreFunctionalTest", groups = "functional")
public class RedisStoreFunctionalTest extends BaseStoreFunctionalTest
{
    @Override
    protected PersistenceConfigurationBuilder createCacheStoreConfig(
        PersistenceConfigurationBuilder persistenceConfigurationBuilder,
        boolean b
    )
    {
        return null;
    }
}
