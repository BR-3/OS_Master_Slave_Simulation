
package yg.Master;

import yg.Job;
import yg.Slave.*;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Master's WRITER threads to slaves
 * takes the jobs from sendToSlaveA array and sendToSlaveB array and
 * actually writes it over on the slave socket
 */
public class ServerThreadSlaveWriter implements Runnable{

    // a reference to the server socket is passed in, all threads share it
    private ServerSocket serverSocket = null;
    private ServerSharedMemory sharedMemory;
    int id;
    ArrayList<Job> jobsForSlaveA;
    ArrayList<Job> jobsForSlaveB;
    private Object jobsForSlaveA_Lock;
    private Object jobsForSlaveB_Lock;

    public ServerThreadSlaveWriter(ServerSocket serverSocket, int id, ServerSharedMemory sharedMemory)  {
        this.serverSocket = serverSocket;
        this.id = id;
        this.jobsForSlaveA = sharedMemory.getJobsForSlaveA();
        this.jobsForSlaveB = sharedMemory.getJobsForSlaveB();
        this.jobsForSlaveA_Lock = sharedMemory.getJobsForSlaveA_LOCK();
        this.jobsForSlaveB_Lock = sharedMemory.getJobsForSlaveB_LOCK();
    }

    @Override
    public void run() {
        try (Socket clientSocket = serverSocket.accept();
             // object streams to send  jobs to slaves:
             ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());

        ) {
            while (!jobsForSlaveA.isEmpty() || !jobsForSlaveB.isEmpty())
            {
                while(sharedMemory.getSlaveAIsOpen())
                {
                    synchronized(jobsForSlaveA_Lock)
                    {
                        objectOut.writeObject(jobsForSlaveA.remove(0));
                    }
                }
                while(sharedMemory.getSlaveAIsOpen()) // switch to B when working
                {
                    synchronized(jobsForSlaveB_Lock)
                    {
                        objectOut.writeObject(jobsForSlaveB.remove(0));
                    }
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
