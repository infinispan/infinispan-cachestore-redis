package org.infinispan.persistence.redis.configuration;

final public class ConnectionPoolConfiguration
{
    private final int maxMasterTotal;
    private final int maxSlaveTotal;

    ConnectionPoolConfiguration(int maxMasterTotal, int maxSlaveTotal)
    {
        this.maxMasterTotal = maxMasterTotal;
        this.maxSlaveTotal = maxSlaveTotal;
    }

    public int maxMasterTotal()
    {
        return maxMasterTotal;
    }

    public int maxSlaveTotal()
    {
        return maxSlaveTotal;
    }
}
