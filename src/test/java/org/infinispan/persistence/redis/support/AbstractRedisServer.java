package org.infinispan.persistence.redis.support;

import java.io.IOException;

public abstract class AbstractRedisServer
{
    protected Process startServer(String workingDir, String configurationFile) throws IOException
    {
        return Runtime.getRuntime().exec(String.format("redis-server %s --dir %s", configurationFile, workingDir));
    }
}
