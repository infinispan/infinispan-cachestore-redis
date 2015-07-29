package org.infinispan.persistence.redis.configuration;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import org.infinispan.configuration.parsing.*;
import org.kohsuke.MetaInfServices;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import static org.infinispan.commons.util.StringPropertyReplacer.replaceProperties;

@MetaInfServices
@Namespaces({
    @Namespace(uri = "urn:infinispan:config:store:redis:8.0", root = "redis-store"),
    @Namespace(root = "redis-store")
})
public class RedisStoreConfigurationParser80 implements ConfigurationParser
{
    @Override
    public void readElement(XMLExtendedStreamReader reader, ConfigurationBuilderHolder holder)
        throws XMLStreamException
    {
        ConfigurationBuilder builder = holder.getCurrentConfigurationBuilder();

        Element element = Element.forName(reader.getLocalName());
        switch (element) {
            case REDIS_STORE: {
                this.parseRedisStore(reader, builder.persistence(), holder.getClassLoader());
                break;
            }
            default: {
                throw ParseUtils.unexpectedElement(reader);
            }
        }
    }

    private void parseRedisStore(
        XMLExtendedStreamReader reader,
        PersistenceConfigurationBuilder persistenceBuilder,
        ClassLoader classLoader
    )
        throws XMLStreamException
    {
        RedisStoreConfigurationBuilder builder = new RedisStoreConfigurationBuilder(persistenceBuilder);
        this.parseRedisStoreAttributes(reader, builder);

        while (reader.hasNext() && (reader.nextTag() != XMLStreamConstants.END_ELEMENT)) {
            Element element = Element.forName(reader.getLocalName());
            switch (element) {
                default: {
                    Parser80.parseStoreElement(reader, builder);
                    break;
                }
            }
        }

        persistenceBuilder.addStore(builder);
    }

    private void parseRedisStoreAttributes(XMLExtendedStreamReader reader, RedisStoreConfigurationBuilder builder)
        throws XMLStreamException
    {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            ParseUtils.requireNoNamespaceAttribute(reader, i);
            String value = replaceProperties(reader.getAttributeValue(i));
            Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
            switch (attribute) {
                case CONNECT_TIMEOUT: {
                    builder.connectionTimeout(Long.parseLong(value));
                    break;
                }
                case REMOTE_CACHE_NAME: {
                    builder.remoteCacheName(value);
                    break;
                }
                case SOCKET_TIMEOUT: {
                    builder.socketTimeout(Long.parseLong(value));
                    break;
                }
            }
        }
    }

    @Override
    public Namespace[] getNamespaces() {
        return ParseUtils.getNamespaceAnnotations(getClass());
    }
}
