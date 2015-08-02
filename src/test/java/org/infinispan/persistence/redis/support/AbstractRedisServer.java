package org.infinispan.persistence.redis.support;

import java.io.IOException;

public abstract class AbstractRedisServer
{
    protected Process startServer(String configurationFile) throws IOException
    {
        return Runtime.getRuntime().exec(String.format("redis-server %s", configurationFile));
    }
}
