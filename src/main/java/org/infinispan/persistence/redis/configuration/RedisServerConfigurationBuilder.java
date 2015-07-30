package org.infinispan.persistence.redis.configuration;

import org.infinispan.commons.configuration.Builder;
import org.infinispan.configuration.global.GlobalConfiguration;

public class RedisServerConfigurationBuilder extends AbstractRedisStoreConfigurationChildBuilder<RedisStoreConfigurationBuilder> implements
    Builder<RedisServerConfiguration>
{
    private String host;
    private int port = 6380;

    protected RedisServerConfigurationBuilder(RedisStoreConfigurationBuilder builder)
    {
        super(builder);
    }

    public RedisServerConfigurationBuilder host(String host) {
        this.host = host;
        return this;
    }

    public RedisServerConfigurationBuilder port(int port) {
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
