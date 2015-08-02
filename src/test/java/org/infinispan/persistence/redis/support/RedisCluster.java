package org.infinispan.persistence.redis.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class RedisCluster extends AbstractRedisServer
{
    List<Process> serverList = new ArrayList<Process>();

    public void start() throws IOException
    {
        String testPath = System.getProperty("project.build.testOutputDirectory");

        for (int serverNum : new int[] {1,2,3}) {
            String configurationFile = String.format("%s/redis/server%d/redis.conf", testPath, serverNum);
            Process p = this.startServer(configurationFile);

            pipe(p.getInputStream(), System.out);
            pipe(p.getErrorStream(), System.err);

            this.serverList.add(p);
        }

        try {
            // Give some time for Redis servers to start
            Thread.sleep(50);
        }
        catch (InterruptedException ex) {
            // ignore
        }

        // todo: allocate slots
        // todo: meet servers
    }

    public void kill()
    {
        for (Process p : this.serverList) {
            System.out.println("Terminating redis server");
            p.destroy();
        }

        try {
            // Give some time for Redis to close
            Thread.sleep(50);
        }
        catch (InterruptedException ex) {
            // ignore
        }

        // todo: cleanup Redis data files
    }

    /**
     * Links source and destination pipes
     *
     * @param src Source
     * @param dst Destination
     */
    private static void pipe(final InputStream src, final PrintStream dst)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] buffer = new byte[1024];
                    for (int n  = 0; n != -1; n = src.read(buffer, 0, 1024)) {
                        dst.write(buffer, 0, n);
                    }
                }
                catch(IOException ex) {
                    // exit on error
                }
            }
        }).start();
    }
}
