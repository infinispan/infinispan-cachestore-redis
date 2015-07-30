package org.infinispan.persistence.redis.configuration;

import org.infinispan.commons.executors.ExecutorFactory;
import org.infinispan.commons.util.Util;
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

                case LOAD_BALANCER: {
                    this.parseLoadBalancer(reader, builder.loadBalancerFactory(), classLoader);
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

    private void parseConnectionPool(XMLExtendedStreamReader reader, ConnectionPoolConfigurationBuilder builder)
        throws XMLStreamException
    {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            ParseUtils.requireNoNamespaceAttribute(reader, i);
            String value = replaceProperties(reader.getAttributeValue(i));
            Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
            switch (attribute) {
                case MAX_MASTER_TOTAL: {
                    builder.maxMasterTotal(Integer.parseInt(value));
                    break;
                }

                case MAX_SLAVE_TOTAL: {
                    builder.maxSlaveTotal(Integer.parseInt(value));
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
                case TYPE:
                    builder.type(value);
                    break;

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

    private void parseLoadBalancer(
        XMLExtendedStreamReader reader,
        ExecutorFactoryConfigurationBuilder builder,
        ClassLoader classLoader
    )
        throws XMLStreamException
    {
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            ParseUtils.requireNoNamespaceAttribute(reader, i);
            String value = replaceProperties(reader.getAttributeValue(i));
            Attribute attribute = Attribute.forName(reader.getAttributeLocalName(i));
            switch (attribute) {
                case FACTORY: {
                    builder.factory(Util.<ExecutorFactory> getInstance(value, classLoader));
                    break;
                }

                default: {
                    throw ParseUtils.unexpectedAttribute(reader, i);
                }
            }
        }

        builder.withExecutorProperties(Parser80.parseProperties(reader));
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

                case CLIENT_NAME: {
                    builder.clientName(value);
                    break;
                }

                case DATABASE: {
                    builder.database(Integer.parseInt(value));
                    break;
                }

                case PASSWORD: {
                    builder.password(value);
                    break;
                }

                case RETRY_ATTEMPTS: {
                    builder.retryAttempts(Integer.parseInt(value));
                    break;
                }

                case RETRY_INTERVAL: {
                    builder.retryInterval(Integer.parseInt(value));
                    break;
                }

                case EXECUTION_TIMEOUT: {
                    builder.executionTimeout(Integer.parseInt(value));
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
