package org.infinispan.persistence.redis;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test(testName = "persistence.redis.RemoteStoreConfigTest", groups = "functional")
public class RedisStoreConfigTest
{
    public static final String CACHE_LOADER_CONFIG = "redis-cl-config.xml";

    @BeforeTest
    public void startUp()
    {

    }
}
