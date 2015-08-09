package org.infinispan.persistence.redis.client;

import java.io.Serializable;

final public class RedisCacheEntry implements Serializable
{
    private byte[] valueBuf;
    private byte[] metadataBuf;

    public RedisCacheEntry(byte[] valueBuf, byte[] metadataBuf)
    {
        this.valueBuf = valueBuf;
        this.metadataBuf = metadataBuf;
    }

    public byte[] getValueBytes()
    {
        return this.valueBuf;
    }

    public byte[] getMetadataBytes()
    {
        return this.metadataBuf;
    }
}
