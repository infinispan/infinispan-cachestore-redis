package org.infinispan.persistence.redis;

import org.infinispan.persistence.BaseStoreTest;
import org.infinispan.persistence.spi.AdvancedLoadWriteStore;
import org.testng.annotations.Test;

@Test(testName = "persistence.redis.RedisStoreTest", groups = "functional")
public class RedisStoreTest extends BaseStoreTest
{
    @Override
    protected AdvancedLoadWriteStore createStore() throws Exception
    {
        return null;
    }

    @Override
    public void testReplaceExpiredEntry() throws Exception
    {

    }
}
