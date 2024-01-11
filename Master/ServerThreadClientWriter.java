package yg.Master;

import yg.Job;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * Master's WRITER threads to clients
 * takes the finished jobs from the finished jobs array
 * and writes it back to the client that sent it
 */

public class ServerThreadClientWriter implements Runnable {
    private ServerSocket serverSocket = null;
    private ServerSharedMemory sharedMemory;
    int id;
    Object doneJobs_LOCK;
    ArrayList<Job> doneJobs;

    public ServerThreadClientWriter(ServerSocket serverSocket, int id, ServerSharedMemory sharedMemory) {
        this.serverSocket = serverSocket;
        this.sharedMemory = sharedMemory;
        this.id = id;
        this.doneJobs_LOCK = sharedMemory.getDoneJobs_LOCK();
        this.doneJobs = sharedMemory.getDoneJobs();
    }

    @Override
    public void run() {
        try (Socket clientSocket = serverSocket.accept();
             // object streams:
             ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());

        ) {
            while((sharedMemory.getDoneJobs() != null))
            {
                ArrayList<Job> currDoneJobs = new ArrayList<>(sharedMemory.getDoneJobs());

                for (Job currDoneJob : currDoneJobs)
                    System.out.println("Sending finished job " + currDoneJob.getID() + " type " + currDoneJob.getType() + " to client " + currDoneJob.getClient());
                {
                    synchronized(doneJobs_LOCK)
                    {
                        objectOut.writeObject(sharedMemory.getDoneJobs().remove(0));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
