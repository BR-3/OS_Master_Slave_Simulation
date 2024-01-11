package yg.Master;

import yg.Job;

import java.io.*;
import java.net.*;

/**
 * This thread will listen for finished jobs from the slave
 * and add them to the arraylist of done jobs in shared memory.
 */

public class ServerThreadSlaveAListener implements Runnable{
    private ServerSocket serverSocket = null;
    private ServerSharedMemory sharedMemory;
    int args;
    private Object doneJobs_Lock;
    public ServerThreadSlaveAListener(ServerSocket serverSocket, int args,
                                      ServerSharedMemory sharedMemory) {
        this.args = args;
        this.serverSocket = serverSocket;
        this.doneJobs_Lock = sharedMemory.getDoneJobs_LOCK();
    }
    public void run() {
        try (
                Socket clientSocket = serverSocket.accept();
                ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
        ) {
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
