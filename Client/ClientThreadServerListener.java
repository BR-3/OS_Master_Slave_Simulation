package yg.Client;

import yg.Job;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This thread will listen for the messages from the server
 * saying which jobs are finished. When it receives a finished job, it will
 * remove it from its list of jobs
 */

public class ClientThreadServerListener implements Runnable{
    private Socket clientSocket;
    private int id;
    private ClientSharedMemory sharedMemory;
    private Object jobList_LOCK;

    public ClientThreadServerListener(Socket clientSocket, int id, ClientSharedMemory sharedMemory)
    {
        this.clientSocket = clientSocket;
        this.id = id;
        this.sharedMemory = sharedMemory;
        this.jobList_LOCK= sharedMemory.getJobList_LOCK();
    }

    public void run()
    {
        try (
                ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        ) {
            Object input;
            while((input = objectIn.readObject()) != null)
            {
                Job finishedJob = (Job) input;
                // this will remove any finished job that is sent back to the client from the job list
                synchronized (jobList_LOCK)
                {
                    ArrayList<Job> copyJobs = sharedMemory.getJobList();
                    for(Job j : copyJobs)
                    {
                        if (j.equals(finishedJob))
                        {
                            sharedMemory.getJobList().remove(j);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
