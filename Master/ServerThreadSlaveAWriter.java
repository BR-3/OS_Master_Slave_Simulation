
package yg.Master;

import yg.Job;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Master's WRITER threads to slaveA
 * takes the jobs from sendToSlaveA array and
 * actually writes it over on the slave socket
 */
public class ServerThreadSlaveAWriter implements Runnable{
    // a reference to the server socket is passed in, all threads share it
    private final ServerSocket serverSocket;
    private final ServerSharedMemory sharedMemory;
    private final Object jobsForSlaveA_Lock;

    public ServerThreadSlaveAWriter(ServerSocket serverSocket, ServerSharedMemory sharedMemory)  {
        this.serverSocket = serverSocket;
        this.sharedMemory = sharedMemory;
        this.jobsForSlaveA_Lock = sharedMemory.getJobsForSlaveA_LOCK();
    }

    @Override
    public void run() {

        try (
                Socket clientSocket = serverSocket.accept();
             // object streams to send  jobs to slaves:
             ObjectOutputStream objectOut = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));

        ) {
            System.out.println("Hi from serverThreadSlaveAWriter!");
            while(true)
            {
                // to use as current status:
                ArrayList<Job> currJobsForSlaveA;

                synchronized (jobsForSlaveA_Lock)
                {
                    currJobsForSlaveA = new ArrayList<>(sharedMemory.getJobsForSlaveA());
                }

                for (Job currJob : currJobsForSlaveA)
                {
                    // remove each one from original array:
                    synchronized (jobsForSlaveA_Lock)
                    {
                        sharedMemory.getJobsForSlaveA().remove(currJob);
                    }
                    // write it to the slave A socket:
                    System.out.println("ServerTSlaveAWriter: Sending to slave A socket: Client"
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
