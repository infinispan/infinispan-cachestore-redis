package org.infinispan.persistence.redis.client;

import org.infinispan.commons.marshall.StreamingMarshaller;
import org.infinispan.persistence.redis.configuration.RedisStoreConfiguration;

import java.security.InvalidParameterException;

public class RedisConnectionPoolFactory
{
    public static RedisConnectionPool factory(RedisStoreConfiguration configuration, StreamingMarshaller marshaller)
    {
        switch (configuration.topology()) {
            case CLUSTER: {
                return new RedisClusterConnectionPool(configuration, marshaller);
            }

            case SERVER: {
                return new RedisServerConnectionPool(marshaller);
            }
        }

        throw new InvalidParameterException();
    }
}
