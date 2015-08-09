package org.infinispan.persistence.redis.support;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RedisCluster extends AbstractRedisServer
{
    private final int SLOTS_PER_CLUSTER_NODE = 5462;
    private final int START_PORT = 6379;
    List<Process> serverList = new ArrayList<Process>();

    private final String testPath;

    public RedisCluster()
    {
        this.testPath = System.getProperty("project.build.testOutputDirectory");
    }

    public void start() throws IOException
    {
        int startSlot = 0;
        int portPosition = START_PORT;

        for (int serverNum : new int[] {1,2,3}) {
            String workingDir = String.format("%s/redis/server%d", this.testPath, serverNum);
            String configurationFile = String.format("%s/redis/server%d/redis.conf", this.testPath, serverNum);
            Process p = this.startServer(configurationFile, workingDir, portPosition, "--cluster-enabled yes");

            this.addSlots(portPosition, startSlot, startSlot + SLOTS_PER_CLUSTER_NODE);

            if (portPosition != START_PORT) {
                this.meetServer(START_PORT, portPosition);
            }

            startSlot += SLOTS_PER_CLUSTER_NODE + 1;
            portPosition++;

            this.serverList.add(p);
        }

        System.out.println("All servers started...");

        try {
            System.out.println("Waiting for Redis cluster to settle");
            Thread.sleep(5000);
        }
        catch(Exception ex){
            System.out.println("Cut sleep early");
        }
    }

    public void kill()
    {
        for (Process p : this.serverList) {
            super.kill(p);
        }

        for (int serverNum : new int[] {1,2,3}) {
            String dumpFileName = String.format("%s/redis/server%d/dump.rdb", this.testPath, serverNum);
            File dumpFile = new File(dumpFileName);

            if ( ! dumpFile.delete()) {
                System.out.println(String.format("Failed to delete Redis dump file %s", dumpFileName));
            }

            String nodeFileName = String.format("%s/redis/server%d/nodes.conf", this.testPath, serverNum);
            File nodeFile = new File(nodeFileName);

            if ( ! nodeFile.delete()) {
                System.out.println(String.format("Failed to delete Redis node file %s", dumpFileName));
            }
        }
    }

    private void addSlots(int port, int start, int end)
        throws IOException
    {
        String slots = "";

        if (end > 16383) {
            end = 16383;
        }

        for (int slot = start; slot <= end; slot++) {
            slots += " " + slot;
        }

        this.runCommand(String.format("redis-cli -p %d cluster addslots %s", port, slots));
    }

    private void meetServer(int server1Port, int server2Port)
        throws IOException
    {
        this.runCommand(String.format("redis-cli -p %d cluster meet 127.0.0.1 %d", server2Port, server1Port));
    }
}
