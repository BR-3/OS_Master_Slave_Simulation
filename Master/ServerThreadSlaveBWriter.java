
package yg.Master;

import yg.Job;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Master's WRITER threads to slaveB
 * takes the jobs from sendToSlaveB array and
 * actually writes it over on the slave socket
 */
public class ServerThreadSlaveBWriter implements Runnable{
    // a reference to the server socket is passed in, all threads share it
    private final ObjectOutputStream objectOutSB;
    private final ServerSharedMemory sharedMemory;
    private final Object jobsForSlaveB_Lock;

    public ServerThreadSlaveBWriter(ObjectOutputStream objectOutSB, ServerSharedMemory sharedMemory)  {
        this.objectOutSB = objectOutSB;
        this.sharedMemory = sharedMemory;
        this.jobsForSlaveB_Lock = sharedMemory.getJobsForSlaveB_LOCK();
    }

    @Override
    public void run() {
        System.out.println("Hi from serverThreadSlaveBWriter before connecting");
        try (
                objectOutSB;
        ) {
            System.out.println("Hi from serverThreadSlaveBWriter! the thread is running.");
            while(true)
            {
                // to use as current status:
                ArrayList<Job> currJobsForSlaveB;

                synchronized (jobsForSlaveB_Lock)
                {
                    currJobsForSlaveB = new ArrayList<>(sharedMemory.getJobsForSlaveB());
                }

                for (Job currJob : currJobsForSlaveB)
                {
                    // remove each one from original array:
                    synchronized (jobsForSlaveB_Lock)
                    {
                        sharedMemory.getJobsForSlaveB().remove(currJob);
                    }
                    // write it to the slave A socket:
                    System.out.println("ServerTSlaveBWriter: Sending to slave B socket: Client"
                            + currJob.getClient() + ", Type: " + currJob.getType() + ", ID: " + currJob.getID());
                    objectOutSB.writeObject(currJob);
                    objectOutSB.flush();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
