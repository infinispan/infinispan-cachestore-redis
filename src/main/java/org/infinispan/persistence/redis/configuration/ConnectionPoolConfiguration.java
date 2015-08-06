package org.infinispan.persistence.redis.configuration;

public class ConnectionPoolConfiguration
{
    private final int maxTotal;
    private final int maxIdle;
    private final int minIdle;
    private final long timeBetweenEvictionRuns;
    private final long minEvictableIdleTime;

    ConnectionPoolConfiguration(
        int maxTotal,
        int maxIdle,
        int minIdle,
        long timeBetweenEvictionRuns,
        long minEvictableIdleTime
    )
    {
        this.maxTotal = maxTotal;
        this.maxIdle = maxIdle;
        this.minIdle = minIdle;
        this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
        this.minEvictableIdleTime = minEvictableIdleTime;
    }

    public int maxTotal()
    {
        return this.maxTotal;
    }

    public int maxIdle()
    {
        return this.maxIdle;
    }

    public int minIdle()
    {
        return this.minIdle;
    }

    public long timeBetweenEvictionRuns()
    {
        return this.timeBetweenEvictionRuns;
    }

    public long minEvictableIdleTime()
    {
        return this.minEvictableIdleTime;
    }

    @Override
    public String toString() {
        return "ConnectionPoolConfiguration [maxTotal=" + this.maxTotal + ", maxIdle=" + this.maxIdle
            + ", minIdle=" + this.minIdle + ", timeBetweenEvictionRuns="
            + this.timeBetweenEvictionRuns + ", minEvictableIdleTime=" + this.minEvictableIdleTime + "]";
    }
}
