package org.infinispan.persistence.redis.configuration;

import java.util.HashMap;
import java.util.Map;

public enum Attribute
{
    // must be first
    UNKNOWN(null),

    MAX_REDIRECTIONS("max-redirections"),
    CONNECT_TIMEOUT("connect-timeout"),
    SOCKET_TIMEOUT("socket-timeout"),
    HOST("host"),
    PORT("port"),
    MIN_IDLE("min-idle"),
    MAX_IDLE("max-idle"),
    MAX_TOTAL("max-total")
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
