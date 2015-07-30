package org.infinispan.persistence.redis.configuration;

final public class ConnectionPoolConfiguration
{
    private final int maxTotal;
    private final int maxIdle;
    private final int minIdle;

    ConnectionPoolConfiguration(int maxTotal, int maxIdle, int minIdle)
    {
        this.maxTotal = maxTotal;
        this.maxIdle = maxIdle;
        this.minIdle = minIdle;
    }

    public int maxTotal()
    {
        return maxTotal;
    }

    public int maxIdle()
    {
        return maxIdle;
    }

    public int minIdle()
    {
        return minIdle;
    }
}
