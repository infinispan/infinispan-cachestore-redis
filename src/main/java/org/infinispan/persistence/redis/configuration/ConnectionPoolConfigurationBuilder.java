package org.infinispan.persistence.redis.configuration;

import org.infinispan.commons.configuration.Builder;
import org.infinispan.configuration.global.GlobalConfiguration;

public class ConnectionPoolConfigurationBuilder extends AbstractRedisStoreConfigurationChildBuilder<RedisStoreConfigurationBuilder>
    implements Builder<ConnectionPoolConfiguration>
{
    private int maxTotal = 8;
    private int maxIdle = 8;
    private int minIdle = 0;

    protected ConnectionPoolConfigurationBuilder(RedisStoreConfigurationBuilder builder)
    {
        super(builder);
    }

    public ConnectionPoolConfigurationBuilder maxTotal(int maxTotal)
    {
        this.maxTotal = maxTotal;
        return this;
    }

    public ConnectionPoolConfigurationBuilder maxIdle(int maxIdle)
    {
        this.maxIdle = maxIdle;
        return this;
    }

    public ConnectionPoolConfigurationBuilder minIdle(int minIdle)
    {
        this.minIdle = minIdle;
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
        return new ConnectionPoolConfiguration(maxTotal, maxIdle, minIdle);
    }

    @Override
    public Builder<?> read(ConnectionPoolConfiguration template)
    {
        maxTotal = template.maxTotal();
        maxIdle = template.maxIdle();
        minIdle = template.minIdle();
        return this;
    }
}
