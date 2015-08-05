package org.infinispan.persistence.redis.client;

import org.infinispan.commons.marshall.StreamingMarshaller;
import org.infinispan.persistence.redis.configuration.RedisStoreConfiguration;

import java.security.InvalidParameterException;

public class RedisConnectionPoolFactory
{
    public static RedisConnectionPool factory(RedisStoreConfiguration configuration, StreamingMarshaller marshaller)
    {
        RedisMarshaller<String> redisMarshaller = new StringRedisMarshaller(marshaller);

        switch (configuration.topology()) {
            case CLUSTER: {
                return new RedisClusterConnectionPool(configuration, redisMarshaller);
            }

            case SERVER: {
                return new RedisServerConnectionPool(redisMarshaller);
            }
        }

        throw new InvalidParameterException();
    }
}
