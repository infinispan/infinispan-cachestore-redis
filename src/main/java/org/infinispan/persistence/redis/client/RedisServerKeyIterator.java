package org.infinispan.persistence.redis.client;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;

import java.util.Iterator;
import java.util.List;

public class RedisServerKeyIterator implements Iterator<Object>
{
    private Jedis client;
    private RedisMarshaller<String> marshaller;
    private ScanResult<String> scanCursor;
    private List<String> keyResults;
    private int position = 0;

    public RedisServerKeyIterator(Jedis client, RedisMarshaller<String> marshaller)
    {
        this.client = client;
        this.marshaller = marshaller;
        this.scanCursor = client.scan("0");
        this.keyResults = this.scanCursor.getResult();
    }

    @Override
    public boolean hasNext()
    {
        if (this.position < this.keyResults.size()) {
            return true;
        }
        else if ( ! this.scanCursor.getStringCursor().equals("0")) {
            this.scanCursor = this.client.scan(this.scanCursor.getStringCursor());
            this.keyResults = this.scanCursor.getResult();
            this.position = 0;

            if (this.keyResults.size() > 0) {
                return true;
            }
        }

        client.close();
        return false;
    }

    @Override
    public Object next()
    {
        return this.marshaller.unmarshall(this.keyResults.get(this.position++));
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
