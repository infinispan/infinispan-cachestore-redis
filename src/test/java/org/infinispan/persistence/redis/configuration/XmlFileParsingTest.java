package org.infinispan.persistence.redis.configuration;

import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.test.fwk.TestCacheManagerFactory;
import org.infinispan.configuration.cache.StoreConfiguration;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Test(groups = "unit", testName = "persistence.redis.configuration.XmlFileParsingTest")
public class XmlFileParsingTest
{
    private EmbeddedCacheManager cacheManager;

    public void testRedisCacheStore() throws Exception
    {
        String start_tag = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<infinispan\n" +
            "      xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "      xsi:schemaLocation=\"urn:infinispan:config:%d.%d http://www.infinispan.org/schemas/infinispan-config-%d.%d.xsd\"\n" +
            "      xmlns=\"urn:infinispan:config:%d.%d\">", 8, 0, 8, 0, 8, 0);
        String end_tag = "</infinispan>";

        String config = start_tag +
            "<cache-container default-cache=\"default\">" +
            "   <local-cache name=\"default\">\n" +
            "     <persistence>\n" +
            "       <redis-store xmlns=\"urn:infinispan:config:store:remote:8.0\" >\n" +
            "         <redis-server host=\"one\" />\n" +
            "         <redis-server host=\"two\" />\n" +
            "       </remote-store>\n" +
            "     </persistence>\n" +
            "   </local-cache>\n" +
            "</cache-container>" +
            end_tag;

        RedisStoreConfiguration store = (RedisStoreConfiguration) buildCacheManagerWithCacheStore(config);
        assert store.servers().size() == 2;
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
