package org.infinispan.persistence.redis.configuration;

import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.test.AbstractInfinispanTest;
import org.infinispan.test.TestingUtil;
import org.infinispan.test.TestingUtil.InfinispanStartTag;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.infinispan.configuration.cache.StoreConfiguration;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Test(groups = "unit", testName = "persistence.redis.configuration.XmlFileParsingTest")
public class XmlFileParsingTest extends AbstractInfinispanTest
{
    private EmbeddedCacheManager cacheManager;

    public void testRedisCacheStore() throws Exception
    {
        String config = InfinispanStartTag.LATEST +
            "<cache-container default-cache=\"default\">" +
            "   <local-cache name=\"default\">\n" +
            "     <persistence>\n" +
            "       <redis-store xmlns=\"urn:infinispan:config:store:redis:"+ InfinispanStartTag.LATEST.majorMinor()+"\" >\n" +
            "         <redis-server host=\"one\" />\n" +
            "         <redis-server host=\"two\" />\n" +
            "       </redis-store>\n" +
            "     </persistence>\n" +
            "   </local-cache>\n" +
            "</cache-container>" +
            TestingUtil.INFINISPAN_END_TAG;

        RedisStoreConfiguration store = (RedisStoreConfiguration) buildCacheManagerWithCacheStore(config);
        assert store.servers().size() == 2;
    }

    public void testInheritedCacheStoreAttributes() throws Exception
    {
        String config = InfinispanStartTag.LATEST +
            "<cache-container default-cache=\"default\">" +
            "   <local-cache name=\"default\">\n" +
            "     <persistence>\n" +
            "       <redis-store xmlns=\"urn:infinispan:config:store:redis:"+ InfinispanStartTag.LATEST.majorMinor()+ "\"" +
            "             shared=\"true\" preload=\"true\" >\n" +
            "         <redis-server host=\"one\" />\n" +
            "       </redis-store>\n" +
            "     </persistence>\n" +
            "   </local-cache>\n" +
            "</cache-container>" +
            TestingUtil.INFINISPAN_END_TAG;

        RedisStoreConfiguration store = (RedisStoreConfiguration) buildCacheManagerWithCacheStore(config);
        assert store.shared();
        assert store.preload();
        assert store.servers().size() == 1;
    }

    private StoreConfiguration buildCacheManagerWithCacheStore(final String config)
        throws IOException
    {
        InputStream is = new ByteArrayInputStream(config.getBytes());
        cacheManager = TestCacheManagerFactory.fromStream(is);
        assert cacheManager.getDefaultCacheConfiguration().persistence().stores().size() == 1;
        return cacheManager.getDefaultCacheConfiguration().persistence().stores().get(0);
    }
}
