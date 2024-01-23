package yg.Master;

import yg.Job;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * Master's WRITER threads to clients
 * takes the finished jobs from the finished jobs array
 * and writes it back to the correct client that sent it
 * over the socket
 */

public class ServerThreadClientWriter implements Runnable {
    //private ServerSocket serverSocket = null;
    private Socket clientSocket;
    private ServerSharedMemory sharedMemory;
    int clientId;
    Object doneJobs_LOCK;
    ArrayList<Job> doneJobs;

    public ServerThreadClientWriter(Socket clientSocket, int clientId, ServerSharedMemory sharedMemory) {
        //this.serverSocket = serverSocket;
        this.clientSocket = clientSocket;
        this.sharedMemory = sharedMemory;
        this.clientId = clientId;
        this.doneJobs_LOCK = sharedMemory.getDoneJobs_LOCK();
        this.doneJobs = sharedMemory.getDoneJobs();
    }

    @Override
    public void run() {
        try (
             // object streams:
             ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());

        ) {
            while (true)
            {
                ArrayList<Job> currDoneJobsForClient0;

                synchronized (sharedMemory.getClient0DoneJobs_LOCK())
                {
                    currDoneJobsForClient0 = new ArrayList<>(sharedMemory.getClient0DoneJobs());
                }

                for (Job currDoneJob : currDoneJobsForClient0)
                {
                    // remove each one from original array
                    synchronized (sharedMemory.getClient0DoneJobs_LOCK())
                    {
                        sharedMemory.getClient0DoneJobs().remove(currDoneJob);
                    }

                    // write it to the client0 socket:
                    System.out.println("ServerTClient0Writer: Sending to client0 socket: " +
                            "Client: " + currDoneJob.getClient() + ", Type: " + currDoneJob.getType() +
                            "ID: " + currDoneJob.getID());
                    objectOut.writeObject(currDoneJob);
                    objectOut.flush();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
