package org.infinispan.persistence.redis.configuration;

import java.util.HashMap;
import java.util.Map;

public enum Attribute
{
    // must be first
    UNKNOWN(null),

    HOST("host"),
    PORT("port"),
    PASSWORD("password"),
    DATABASE("database"),
    TOPOLOGY("topology"),
    TIME_BETWEEN_EVICTION_RUNS("time-between-eviction-runs"),
    MIN_EVICTABLE_IDLE_TIME("min-evictable-idle-time"),
    MAX_TOTAL("max-total"),
    MAX_IDLE("max-idle"),
    MIN_IDLE("min-idle"),
    SOCKET_TIMEOUT("socket-timeout"),
    CONNECTION_TIMEOUT("connection-timeout"),
    MASTER_NAME("master-name"),
    MAX_REDIRECTIONS("max-redirections")
    ;

    private final String name;

    private Attribute(final String name)
    {
        this.name = name;
    }

    /**
     * Get the local name of this element.
     *
     * @return the local name
     */
    public String getLocalName()
    {
        return name;
    }

    private static final Map<String, Attribute> attributes;

    static
    {
        final Map<String, Attribute> map = new HashMap<String, Attribute>(64);

        for (Attribute attribute : values()) {
            final String name = attribute.getLocalName();
            if (name != null) {
                map.put(name, attribute);
            }
        }

        attributes = map;
    }

    public static Attribute forName(final String localName)
    {
        final Attribute attribute = attributes.get(localName);
        return attribute == null ? UNKNOWN : attribute;
    }
}
