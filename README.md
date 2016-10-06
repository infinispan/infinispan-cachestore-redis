
# infinispan-cachestore-redis

A cache store for storing cache data in Redis using Infinispan.

Supports single servers, Sentinel and Redis cluster.

## Single server configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<infinispan
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="urn:infinispan:config:8.0 http://www.infinispan.org/schemas/infinispan-config-8.0.xsd
                          urn:infinispan:config:store:redis:8.0 http://www.infinispan.org/schemas/infinispan-cachestore-redis-config-8.0.xsd"
    xmlns="urn:infinispan:config:8.0"
    xmlns:redis="urn:infinispan:config:store:redis:8.0" >

    <cache-container>
        <local-cache>
            <persistence passivation="false">
                <redis-store xmlns="urn:infinispan:config:store:redis:8.0"
                    topology="server" socket-timeout="10000" connection-timeout="10000">
                    <redis-server host="server1" port="6379" />
                    <connection-pool min-idle="6" max-idle="10" max-total="20"
                        min-evictable-idle-time="30000" time-between-eviction-runs="30000" />
                </redis-store>
            </persistence>
        </local-cache>
    </cache-container>
</infinispan>
```

## Sentinel configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<infinispan
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="urn:infinispan:config:8.0 http://www.infinispan.org/schemas/infinispan-config-8.0.xsd
                          urn:infinispan:config:store:redis:8.0 http://www.infinispan.org/schemas/infinispan-cachestore-redis-config-8.0.xsd"
    xmlns="urn:infinispan:config:8.0"
    xmlns:redis="urn:infinispan:config:store:redis:8.0" >

    <cache-container>
        <local-cache>
            <persistence passivation="false">
                <redis-store xmlns="urn:infinispan:config:store:redis:8.0"
                    topology="sentinel" socket-timeout="10000" connection-timeout="10000" master-name="mymaster">
                    <sentinel-server host="server1" port="26379" />
                    <sentinel-server host="server2" port="26379" />
                    <sentinel-server host="server3" port="26379" />
                    <connection-pool min-idle="6" max-idle="10" max-total="20"
                        min-evictable-idle-time="30000" time-between-eviction-runs="30000" />
                </redis-store>
            </persistence>
        </local-cache>
    </cache-container>
</infinispan>
```

## Cluster configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<infinispan
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="urn:infinispan:config:8.0 http://www.infinispan.org/schemas/infinispan-config-8.0.xsd
                          urn:infinispan:config:store:redis:8.0 http://www.infinispan.org/schemas/infinispan-cachestore-redis-config-8.0.xsd"
    xmlns="urn:infinispan:config:8.0"
    xmlns:redis="urn:infinispan:config:store:redis:8.0" >

    <cache-container>
        <local-cache>
            <persistence passivation="false">
                <redis-store xmlns="urn:infinispan:config:store:redis:8.0"
                    topology="cluster" socket-timeout="10000" connection-timeout="10000">
                    <redis-server host="server1" port="6379" />
                    <redis-server host="server2" port="6379" />
                    <redis-server host="server3" port="6379" />
                    <connection-pool min-idle="6" max-idle="10" max-total="20"
                        min-evictable-idle-time="30000" time-between-eviction-runs="30000" />
                </redis-store>
            </persistence>
        </local-cache>
    </cache-container>
</infinispan>
```

## Caveats

* When using AWS ElastiCache, always specify an explicit non-zero database index for all `<redis-store/>` configurations, for example `<redis-store database="1"/>`. AWS ElastiCache inserts a special *ElastiCacheMasterReplicationTimestamp* key in the default database (at zero index) to aid replication, which may lead to unexpected unmarshalling IO exceptions when the Infinispan cache needs to iterate over the stored keys.


## License

Copyright 2015 Simon Paulger <spaulger@codezen.co.uk>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
