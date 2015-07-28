package com.barbon.infinispan.redis.configuration;

import org.infinispan.configuration.parsing.*;
import org.kohsuke.MetaInfServices;

import javax.xml.stream.XMLStreamException;

@MetaInfServices
@Namespaces({
    @Namespace(uri = "urn:infinispan:config:store:redis:7.1", root = "redis-store"),
    @Namespace(root = "redis-store")
})
public class RedisStoreConfigurationParser71 implements ConfigurationParser
{
    @Override
    public void readElement(
        XMLExtendedStreamReader xmlExtendedStreamReader,
        ConfigurationBuilderHolder configurationBuilderHolder
    )
        throws XMLStreamException
    {

    }

    @Override
    public Namespace[] getNamespaces() {
        return ParseUtils.getNamespaceAnnotations(getClass());
    }
}
