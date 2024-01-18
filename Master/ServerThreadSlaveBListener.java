package yg.Master;

import yg.Job;

import java.io.*;
import java.net.*;

/**
 * This thread will listen for finished jobs from the slave
 * and add them to the arraylist of done jobs in shared memory.
 */

public class ServerThreadSlaveBListener implements Runnable {
    private final ServerSocket serverSocket;
    private final ServerSharedMemory sharedMemory;
    private final Object doneJobs_Lock;
    public ServerThreadSlaveBListener(ServerSocket serverSocket,
                                      ServerSharedMemory sharedMemory) {
        this.serverSocket = serverSocket;
        this.doneJobs_Lock = sharedMemory.getDoneJobs_LOCK();
        this.sharedMemory = sharedMemory;
    }
    public void run() {
        System.out.println("Hi from serverThreadSlaveBListener before connecting");
        try (
                Socket clientSocket = serverSocket.accept();
                ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        ) {
            System.out.println("Hi from the serverThreadSlaveBListener! This thread is working.");
            Object input;
            while ((input = objectIn.readObject()) != null)
            {
                Job finishedJob = (Job) input;
                System.out.println("Received job type " + finishedJob.getType() + " from slave A");

                synchronized(doneJobs_Lock)
                {
                    sharedMemory.getDoneJobs().add(finishedJob);
                }
            }

        }   catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
