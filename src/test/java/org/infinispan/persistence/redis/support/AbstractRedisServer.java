package org.infinispan.persistence.redis.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public abstract class AbstractRedisServer
{
    protected Process startRedisServer(String configurationFile, String workingDir, int port, String extraParameters)
        throws IOException
    {
        Process p = Runtime.getRuntime().exec(String.format("redis-server %s --dir %s --port %d %s",
            configurationFile, workingDir, port, extraParameters));
        this.pipe(p.getInputStream(), System.out);
        this.pipe(p.getErrorStream(), System.err);

        this.sleep(50);

        return p;
    }

    protected Process startSentinelServer(String configurationFile, String workingDir, int port, String extraParameters)
        throws IOException
    {
        Process p = Runtime.getRuntime().exec(String.format("redis-sentinel %s --dir %s --port %d %s",
            configurationFile, workingDir, port, extraParameters));
        this.pipe(p.getInputStream(), System.out);
        this.pipe(p.getErrorStream(), System.err);

        this.sleep(50);

        return p;
    }

    public void kill(Process p)
    {
        System.out.println("Terminating redis server");
        p.destroy();

        while (true) {
            try {
                p.exitValue();
                break;
            } catch (IllegalThreadStateException ex) {
                // ignore
            }

            try {
                p.waitFor();
            } catch (InterruptedException ex) {
                // ignore
            }
        }
    }

    protected void runCommand(String command)
        throws IOException
    {
        Process p = Runtime.getRuntime().exec(command);
        this.pipe(p.getInputStream(), System.out);
        this.pipe(p.getErrorStream(), System.err);

        while (true) {
            try {
                p.exitValue();
                break;
            } catch (IllegalThreadStateException ex) {
                // ignore
            }

            try {
                p.waitFor();
            } catch (InterruptedException ex) {
                // ignore
            }
        }
    }

    protected void pipe(final InputStream src, final PrintStream dst)
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

    protected void sleep(long duration)
    {
        try {

            Thread.sleep(duration);
        }
        catch(Exception ex){
            System.out.println("Cut sleep early");
        }
    }

    protected void cleanup(String testPath, String serverPath, int serverNum)
    {
        String dumpFileName = String.format("%s/redis/%s%d/dump.rdb", testPath, serverPath, serverNum);
        File dumpFile = new File(dumpFileName);

        if ( dumpFile.exists() && ! dumpFile.delete()) {
            System.out.println(String.format("Failed to delete Redis dump file %s", dumpFileName));
        }

        String nodeFileName = String.format("%s/redis/%s%d/nodes.conf", testPath, serverPath, serverNum);
        File nodeFile = new File(nodeFileName);

        if ( nodeFile.exists() && ! nodeFile.delete()) {
            System.out.println(String.format("Failed to delete Redis node file %s", nodeFileName));
        }
    }
}
