package org.infinispan.persistence.redis.configuration;

final public class ConnectionPoolConfiguration
{
    private final int maxTotal;
    private final int maxIdle;
    private final int minIdle;
    private final long timeBetweenEvictionRuns;
    private final long minEvictableIdleTime;
    private final boolean testOnCreate;
    private final boolean testOnBorrow;
    private final boolean testOnReturn;
    private final boolean testOnIdle;

    ConnectionPoolConfiguration(
        int maxTotal,
        int maxIdle,
        int minIdle,
        long timeBetweenEvictionRuns,
        long minEvictableIdleTime,
        boolean testOnCreate,
        boolean testOnBorrow,
        boolean testOnReturn,
        boolean testOnIdle
    )
    {
        this.maxTotal = maxTotal;
        this.maxIdle = maxIdle;
        this.minIdle = minIdle;
        this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
        this.minEvictableIdleTime = minEvictableIdleTime;
        this.testOnCreate = testOnCreate;
        this.testOnBorrow = testOnBorrow;
        this.testOnReturn = testOnReturn;
        this.testOnIdle = testOnIdle;
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

    public boolean testOnCreate()
    {
        return this.testOnCreate;
    }

    public boolean testOnBorrow()
    {
        return this.testOnBorrow;
    }

    public boolean testOnReturn()
    {
        return this.testOnReturn;
    }

    public boolean testOnIdle()
    {
        return this.testOnIdle;
    }

    @Override
    public String toString() {
        return "ConnectionPoolConfiguration [maxTotal=" + this.maxTotal + ", maxIdle=" + this.maxIdle
            + ", minIdle=" + this.minIdle + ", timeBetweenEvictionRuns=" + this.timeBetweenEvictionRuns
            + ", minEvictableIdleTime=" + this.minEvictableIdleTime
            + ", testOnCreate=" + this.testOnCreate + ", testOnBorrow=" + this.testOnBorrow
            + ", testOnReturn=" + this.testOnReturn + ", testOnIdle=" + this.testOnIdle + "]";
    }
}
