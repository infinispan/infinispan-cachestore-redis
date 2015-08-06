package org.infinispan.persistence.redis.configuration;

final public class RedisServerConfiguration
{
    private final String host;
    private final int port;

    RedisServerConfiguration(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    public String host()
    {
        return this.host;
    }

    public int port()
    {
        return this.port;
    }
}
