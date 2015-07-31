package org.infinispan.persistence.redis.configuration;

public class RedisServerConfiguration
{
    private final String type;
    private final boolean ssl;
    private final String host;
    private final int port;

    RedisServerConfiguration(String type, boolean ssl, String host, int port)
    {
        this.type = type;
        this.ssl = ssl;
        this.host = host;
        this.port = port;
    }

    public String type()
    {
        return this.type;
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
