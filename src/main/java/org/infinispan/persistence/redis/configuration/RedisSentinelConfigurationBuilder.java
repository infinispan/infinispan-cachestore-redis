package org.infinispan.persistence.redis.configuration;

import org.infinispan.commons.configuration.Builder;
import org.infinispan.configuration.global.GlobalConfiguration;

public class RedisSentinelConfigurationBuilder extends AbstractRedisStoreConfigurationChildBuilder<RedisStoreConfigurationBuilder> implements
    Builder<RedisServerConfiguration>
{
    private String host;
    private int port = 26379;

    protected RedisSentinelConfigurationBuilder(RedisStoreConfigurationBuilder builder)
    {
        super(builder);
    }

    public RedisSentinelConfigurationBuilder host(String host)
    {
        this.host = host;
        return this;
    }

    public RedisSentinelConfigurationBuilder port(int port)
    {
        this.port = port;
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
    public RedisServerConfiguration create()
    {
        return new RedisServerConfiguration(host, port);
    }

    @Override
    public Builder<?> read(RedisServerConfiguration template)
    {
        this.host = template.host();
        this.port = template.port();

        return this;
    }
}
