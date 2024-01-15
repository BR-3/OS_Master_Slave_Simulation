
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
    private ServerSocket serverSocket;
    private ServerSharedMemory sharedMemory;
    private Object jobsForSlaveB_Lock;

    public ServerThreadSlaveBWriter(ServerSocket serverSocket, ServerSharedMemory sharedMemory)  {
        this.serverSocket = serverSocket;
        this.sharedMemory = sharedMemory;
        this.jobsForSlaveB_Lock = sharedMemory.getJobsForSlaveB_LOCK();
    }

    @Override
    public void run() {
        try (Socket clientSocket = serverSocket.accept();
             // object streams to send  jobs to slaves:
             ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());

        ) {
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
                    System.out.println("ServerTSlaveBWriter: Sending to slave A socket: Client"
                            + currJob.getClient() + ", Type: " + currJob.getType() + ", ID: " + currJob.getID());
                    objectOut.writeObject(currJob);
                    objectOut.flush();
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
