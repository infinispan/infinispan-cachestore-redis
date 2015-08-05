package org.infinispan.persistence.redis.client;

import org.infinispan.commons.marshall.StreamingMarshaller;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

public class RedisServerKeyIterator implements Iterator<Object>
{
    private Jedis client;
    private StreamingMarshaller marshaller;
    private ScanResult<String> scanCursor;
    private List<String> keyResults;
    private int position = 0;

    public RedisServerKeyIterator(Jedis client, StreamingMarshaller marshaller)
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

            return this.keyResults.size() > 0;
        }
        else {
            return false;
        }
    }

    @Override
    public Object next()
    {
        String keyByteString = this.keyResults.get(this.position++);

        try {
            byte[] valueBuf = keyByteString.getBytes(Charset.forName("UTF-8"));
            return this.marshaller.objectFromByteBuffer(valueBuf);
        }
        catch(IOException | ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
