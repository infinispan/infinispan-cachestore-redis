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
                case CONNECTION_POOL: {
                    this.parseConnectionPool(reader, builder.connectionPool());
                    break;
                }

                case SERVER: {
                    this.parseServer(reader, builder.addServer());
                    break;
                }

                default: {
                    Parser80.parseStoreElement(reader, builder);
                    break;
                }
            }
        }

        persistenceBuilder.addStore(builder);
    }

    private void parseConnectionPool(XMLExtendedStreamReader reader, ConnectionPoolConfigurationBuilder builder) throws XMLStreamException
    {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            ParseUtils.requireNoNamespaceAttribute(reader, i);
            String value = replaceProperties(reader.getAttributeValue(i));
            Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
            switch (attribute) {
                case MAX_IDLE: {
                    builder.maxIdle(Integer.parseInt(value));
                    break;
                }

                case MAX_TOTAL: {
                    builder.maxTotal(Integer.parseInt(value));
                    break;
                }

                case MIN_IDLE: {
                    builder.minIdle(Integer.parseInt(value));
                    break;
                }

                default: {
                    throw ParseUtils.unexpectedAttribute(reader, i);
                }
            }
        }

        ParseUtils.requireNoContent(reader);
    }

    private void parseServer(XMLExtendedStreamReader reader, RedisServerConfigurationBuilder builder)
        throws XMLStreamException
    {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            ParseUtils.requireNoNamespaceAttribute(reader, i);
            String value = replaceProperties(reader.getAttributeValue(i));
            Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
            switch (attribute) {
                case HOST:
                    builder.host(value);
                    break;

                case PORT:
                    builder.port(Integer.parseInt(value));
                    break;

                // todo: outbound socket?

                default:
                    throw ParseUtils.unexpectedAttribute(reader, i);
            }
        }

        ParseUtils.requireNoContent(reader);
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
                    builder.connectionTimeout(Integer.parseInt(value));
                    break;
                }

                case SOCKET_TIMEOUT: {
                    builder.socketTimeout(Integer.parseInt(value));
                    break;
                }

                case MAX_REDIRECTIONS: {
                    builder.socketTimeout(Integer.parseInt(value));
                    break;
                }
            }
        }
    }

    @Override
    public Namespace[] getNamespaces()
    {
        return ParseUtils.getNamespaceAnnotations(getClass());
    }
}
