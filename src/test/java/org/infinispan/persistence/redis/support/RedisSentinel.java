package org.infinispan.persistence.redis.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RedisSentinel extends AbstractRedisServer
{
    private final int SERVER_NUM = 1;
    private final int START_PORT = 6379;
    private final int START_SENTINEL_PORT = 26379;
    private final String testPath;
    private Process serverProcess;
    List<Process> sentinelList = new ArrayList<Process>();

    public RedisSentinel()
    {
        this.testPath = System.getProperty("project.build.testOutputDirectory");
    }

    public void start() throws IOException
    {
        int portPosition = START_SENTINEL_PORT;
        String workingDir = String.format("%s/redis/server%d", this.testPath, SERVER_NUM);
        String configurationFile = String.format("%s/redis/server%d/redis.conf", this.testPath, SERVER_NUM);
        serverProcess = this.startRedisServer(configurationFile, workingDir, START_PORT, "");

        this.sleep(5000);

        for (int sentinelNum : new int[] {1,2,3}) {
            workingDir = String.format("%s/redis/sentinel%d", this.testPath, sentinelNum);
            configurationFile = String.format("%s/redis/sentinel%d/sentinel.conf", this.testPath, sentinelNum);
            Process p = this.startSentinelServer(configurationFile, workingDir, portPosition, "");

            portPosition++;
            this.sentinelList.add(p);
        }

        System.out.println("All servers started.");
        System.out.println("Waiting for Sentinel to settle...");
        this.sleep(5000);
    }

    public void kill()
    {
        for (Process p : this.sentinelList) {
            super.kill(p);
        }

        super.kill(serverProcess);

        System.out.println("Waiting for all servers to terminate and release file locks");
        this.sleep(5000);

        this.cleanup(this.testPath, "server", SERVER_NUM);

        for (int serverNum : new int[] {1,2,3}) {
            this.cleanup(this.testPath, "sentinel", serverNum);
        }
    }
}
