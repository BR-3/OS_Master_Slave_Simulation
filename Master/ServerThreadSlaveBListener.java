package yg.Master;

import yg.Job;

import java.io.*;
import java.net.*;

/**
 * This thread will listen for finished jobs from the slave
 * and add them to the arraylist of done jobs in shared memory.
 */

public class ServerThreadSlaveBListener implements Runnable {
    private final ObjectInputStream objectInSB;
    private final ServerSharedMemory sharedMemory;
    private final Object doneJobs_Lock;
    public ServerThreadSlaveBListener(ObjectInputStream objectInSB,
                                      ServerSharedMemory sharedMemory) {
        this.objectInSB = objectInSB;
        this.doneJobs_Lock = sharedMemory.getDoneJobs_LOCK();
        this.sharedMemory = sharedMemory;
    }
    public void run() {
        try (objectInSB;
        ) {
            Object input;
            while ((input = objectInSB.readObject()) != null)
            {
                Job finishedJob = (Job) input;
                System.out.println("Received from slave B - DONE job: Client: " +
                        finishedJob.getClient() + ", type: " + finishedJob.getType() + ", id: " + finishedJob.getID());

                // adjusting the load
                int reducedLoad;
                if(finishedJob.getType() == 'b')
                {
                    reducedLoad = -2;
                } else {
                    reducedLoad = -10;
                }
                synchronized(doneJobs_Lock)
                {
                    sharedMemory.getDoneJobs().add(finishedJob);
                }
                synchronized (sharedMemory.slaveBLoad_LOCK)
                {
                    sharedMemory.addSlaveBLoad(reducedLoad);
                }
            }

        }   catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
