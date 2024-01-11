
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
    ArrayList<Job> jobsForSlaveB;
    private Object jobsForSlaveB_Lock;

    public ServerThreadSlaveBWriter(ServerSocket serverSocket, ServerSharedMemory sharedMemory)  {
        this.serverSocket = serverSocket;
        this.sharedMemory = sharedMemory;
        this.jobsForSlaveB = sharedMemory.getJobsForSlaveB();
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
                    System.out.println("ServerTSlaveWriter: slaveB array BEFORE: " + sharedMemory.getJobsForSlaveB());
                    // remove each one from original array:
                    synchronized (jobsForSlaveB_Lock)
                    {
                        sharedMemory.getJobsForSlaveA().remove(currJob);
                    }
                    System.out.println("ServerTSlaveWriter: slaveB array AFTER: " + sharedMemory.getJobsForSlaveB());
                    // write it to the slave A socket:
                    System.out.println("Sending to slave B socket: Client" + currJob.getClient() + ", Type: " + currJob.getType() + ", ID: " + currJob.getID());
                    objectOut.writeObject(currJob);
                    objectOut.flush();
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
