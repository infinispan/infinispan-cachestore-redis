package org.infinispan.persistence.redis.configuration;

import org.infinispan.commons.configuration.Builder;
import org.infinispan.configuration.global.GlobalConfiguration;

final public class ConnectionPoolConfigurationBuilder extends AbstractRedisStoreConfigurationChildBuilder<RedisStoreConfigurationBuilder> implements
    Builder<ConnectionPoolConfiguration>
{
    private int maxTotal = -1;
    private int maxIdle = -1;
    private int minIdle = 1;
    private long timeBetweenEvictionRuns = 120000;
    private long minEvictableIdleTime = 1800000;
    private boolean testOnCreate = false;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;
    private boolean testOnIdle = false;

    ConnectionPoolConfigurationBuilder(RedisStoreConfigurationBuilder builder)
    {
        super(builder);
    }

    /**
     * Sets a global limit on the number persistent connections that can be in circulation within the
     * combined set of servers. When non-positive, there is no limit to the total number of
     * persistent connections in circulation. When maxTotal is exceeded, all connections pools are
     * exhausted. The default setting for this parameter is -1 (no limit).
     */
    public ConnectionPoolConfigurationBuilder maxTotal(int maxTotal)
    {
        this.maxTotal = maxTotal;
        return this;
    }

    /**
     * Controls the maximum number of idle persistent connections, per server, at any time. When
     * negative, there is no limit to the number of connections that may be idle per server. The
     * default setting for this parameter is -1.
     */
    public ConnectionPoolConfigurationBuilder maxIdle(int maxIdle)
    {
        this.maxIdle = maxIdle;
        return this;
    }

    /**
     * Sets a target value for the minimum number of idle connections (per server) that should always
     * be available. If this parameter is set to a positive number and timeBetweenEvictionRunsMillis
     * > 0, each time the idle connection eviction thread runs, it will try to create enough idle
     * instances so that there will be minIdle idle instances available for each server. The default
     * setting for this parameter is 1.
     */
    public ConnectionPoolConfigurationBuilder minIdle(int minIdle)
    {
        this.minIdle = minIdle;
        return this;
    }

    /**
     * Indicates how long the eviction thread should sleep before "runs" of examining idle
     * connections. When non-positive, no eviction thread will be launched. The default setting for
     * this parameter is 2 minutes.
     */
    public ConnectionPoolConfigurationBuilder timeBetweenEvictionRuns(long timeBetweenEvictionRuns)
    {
        this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
        return this;
    }

    /**
     * Specifies the minimum amount of time that an connection may sit idle in the pool before it is
     * eligible for eviction due to idle time. When non-positive, no connection will be dropped from
     * the pool due to idle time alone. This setting has no effect unless
     * timeBetweenEvictionRunsMillis > 0. The default setting for this parameter is 1800000(30
     * minutes).
     */
    public ConnectionPoolConfigurationBuilder minEvictableIdleTime(long minEvictableIdleTime)
    {
        this.minEvictableIdleTime = minEvictableIdleTime;
        return this;
    }

    public ConnectionPoolConfigurationBuilder testOnCreate(boolean testOnCreate)
    {
        this.testOnCreate = testOnCreate;
        return this;
    }

    public ConnectionPoolConfigurationBuilder testOnBorrow(boolean testOnBorrow)
    {
        this.testOnBorrow = testOnBorrow;
        return this;
    }

    public ConnectionPoolConfigurationBuilder testOnReturn(boolean testOnReturn)
    {
        this.testOnReturn = testOnReturn;
        return this;
    }

    public ConnectionPoolConfigurationBuilder testOnIdle(boolean testOnIdle)
    {
        this.testOnIdle = testOnIdle;
        return this;
    }

    @Override
    public void validate()
    {

    }

    @Override
    public void validate(GlobalConfiguration globalConfig)
    {

    }

    @Override
    public ConnectionPoolConfiguration create()
    {
        return new ConnectionPoolConfiguration(maxTotal, maxIdle, minIdle, timeBetweenEvictionRuns, minEvictableIdleTime, testOnCreate, testOnBorrow, testOnReturn, testOnIdle);
    }

    @Override
    public Builder<?> read(ConnectionPoolConfiguration template)
    {
        this.maxTotal = template.maxTotal();
        this.maxIdle = template.maxIdle();
        this.minIdle = template.minIdle();
        this.timeBetweenEvictionRuns = template.timeBetweenEvictionRuns();
        this.minEvictableIdleTime = template.minEvictableIdleTime();
        this.testOnCreate = template.testOnCreate();
        this.testOnBorrow = template.testOnBorrow();
        this.testOnReturn = template.testOnReturn();
        this.testOnIdle = template.testOnIdle();
        return this;
    }
}
