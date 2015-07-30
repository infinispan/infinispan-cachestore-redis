package org.infinispan.persistence.redis.configuration;

import org.infinispan.commons.configuration.Builder;
import org.infinispan.configuration.global.GlobalConfiguration;

public class ConnectionPoolConfigurationBuilder extends AbstractRedisStoreConfigurationChildBuilder<RedisStoreConfigurationBuilder>
    implements Builder<ConnectionPoolConfiguration>
{
    private int maxMasterTotal = 100;
    private int maxSlaveTotal = 100;

    protected ConnectionPoolConfigurationBuilder(RedisStoreConfigurationBuilder builder)
    {
        super(builder);
    }

    public ConnectionPoolConfigurationBuilder maxMasterTotal(int maxMasterTotal)
    {
        this.maxMasterTotal = maxMasterTotal;
        return this;
    }

    public ConnectionPoolConfigurationBuilder maxSlaveTotal(int maxSlaveTotal)
    {
        this.maxSlaveTotal = maxSlaveTotal;
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
        return new ConnectionPoolConfiguration(maxMasterTotal, maxSlaveTotal);
    }

    @Override
    public Builder<?> read(ConnectionPoolConfiguration template)
    {
        maxMasterTotal = template.maxMasterTotal();
        maxSlaveTotal = template.maxSlaveTotal();
        return this;
    }
}
