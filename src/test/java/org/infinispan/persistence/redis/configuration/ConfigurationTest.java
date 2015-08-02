package org.infinispan.persistence.redis.configuration;

import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "persistence.redis.configuration.ConfigurationTest")
public class ConfigurationTest
{
    public void testRedisCacheStoreConfigurationAdaptor()
    {
        ConfigurationBuilder b = new ConfigurationBuilder();
        b.persistence().addStore(RedisStoreConfigurationBuilder.class)
            .addServer()
                .host("one").port(6379)
            .addServer()
                .host("two")
        ;

        Configuration configuration = b.build();
        RedisStoreConfiguration store = (RedisStoreConfiguration) configuration.persistence().stores().get(0);
        assert store.servers().size() == 2;

        b = new ConfigurationBuilder();
        b.persistence().addStore(RedisStoreConfigurationBuilder.class).read(store);
        Configuration configuration2 = b.build();
        RedisStoreConfiguration store2 = (RedisStoreConfiguration) configuration2.persistence().stores().get(0);
        assert store2.servers().size() == 2;
    }
}
