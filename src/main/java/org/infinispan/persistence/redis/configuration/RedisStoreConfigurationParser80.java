package org.infinispan.persistence.redis.configuration;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.PersistenceConfigurationBuilder;
import org.infinispan.configuration.parsing.*;
import org.kohsuke.MetaInfServices;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import static org.infinispan.commons.util.StringPropertyReplacer.replaceProperties;
import org.infinispan.persistence.redis.configuration.RedisStoreConfiguration.Topology;

@MetaInfServices
@Namespaces({
    @Namespace(uri = "urn:infinispan:config:store:redis:8.0", root = "redis-store"),
    @Namespace(root = "redis-store")
})
final public class RedisStoreConfigurationParser80 implements ConfigurationParser
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
                case SERVER: {
                    this.parseServer(reader, builder.addServer());
                    break;
                }

                case CONNECTION_POOL: {
                    this.parseConnectionPool(reader, builder.connectionPool());
                }

                default: {
                    Parser80.parseStoreElement(reader, builder);
                    break;
                }
            }
        }

        persistenceBuilder.addStore(builder);
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

                default:
                    throw ParseUtils.unexpectedAttribute(reader, i);
            }
        }

        ParseUtils.requireNoContent(reader);
    }

    private void parseConnectionPool(XMLExtendedStreamReader reader, ConnectionPoolConfigurationBuilder builder)
        throws XMLStreamException
    {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            ParseUtils.requireNoNamespaceAttribute(reader, i);
            String value = replaceProperties(reader.getAttributeValue(i));
            Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
            switch (attribute) {
                case TIME_BETWEEN_EVICTION_RUNS: {
                    builder.timeBetweenEvictionRuns(Long.parseLong(value));
                    break;
                }

                case MIN_EVICTABLE_IDLE_TIME: {
                    builder.minEvictableIdleTime(Long.parseLong(value));
                    break;
                }

                case MAX_TOTAL: {
                    builder.maxTotal(Integer.parseInt(value));
                    break;
                }

                case MAX_IDLE: {
                    builder.maxIdle(Integer.parseInt(value));
                    break;
                }

                case MIN_IDLE: {
                    builder.minIdle(Integer.parseInt(value));
                    break;
                }

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
                case DATABASE: {
                    builder.database(Integer.parseInt(value));
                    break;
                }

                case PASSWORD: {
                    builder.password(value);
                    break;
                }

                case TOPOLOGY: {
                    builder.topology(Topology.valueOf(value.toUpperCase()));
                }

                case SOCKET_TIMEOUT: {
                    builder.socketTimeout(Long.parseLong(value));
                }

                case CONNECTION_TIMEOUT: {
                    builder.connectionTimeout(Long.parseLong(value));
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
