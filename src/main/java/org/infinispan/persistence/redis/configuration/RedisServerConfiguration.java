package org.infinispan.persistence.redis.configuration;

public class RedisServerConfiguration
{
    private final boolean ssl;
    private final String host;
    private final int port;

    RedisServerConfiguration(boolean ssl, String host, int port)
    {
        this.ssl = ssl;
        this.host = host;
        this.port = port;
    }

    public boolean ssl()
    {
        return this.ssl;
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
