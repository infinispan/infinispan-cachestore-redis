package org.infinispan.persistence.redis.configuration;

public class RedisServerConfiguration
{
    private final String type;
    private final String host;
    private final int port;

    RedisServerConfiguration(String type, String host, int port)
    {
        this.type = type;
        this.host = host;
        this.port = port;
    }

    public String type()
    {
        return this.type;
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
