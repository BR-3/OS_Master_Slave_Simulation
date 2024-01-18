package yg.Master;

import yg.Job;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Master's LISTENER threads for client 1 and client 2
 * listen to jobs being sent by clients and adds to jobsToBeDone array
 */

public class ServerThreadClientListener implements Runnable{


    // a reference to the server socket is passed in, all threads share it
    private final ServerSocket serverSocket;
    int clientID;
    private final ServerSharedMemory sharedMemory;
    private final Object jobsToComplete_LOCK;

    public ServerThreadClientListener(ServerSocket serverSocket, int clientID, ServerSharedMemory sharedMemory) {
        this.serverSocket = serverSocket;
        this.clientID = clientID;
        this.sharedMemory = sharedMemory;
        this.jobsToComplete_LOCK = sharedMemory.getJobsToComplete_LOCK();
    }

    @Override
    public void run() {
        // This thread accepts its own client socket from the shared server socket
        try (Socket clientSocket = serverSocket.accept();
             ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
             )
        {
            Object input;
            while ((input = objectIn.readObject()) != null)
            {
                Job newJob = (Job) input;

                System.out.println("Received new job. Client: " + newJob.getClient() +
                        ", Type: " + newJob.getType() +
                        ", ID: " + newJob.getID());

                // place new job on shared arraylist with lock
                synchronized(jobsToComplete_LOCK) {
                    sharedMemory.getJobsToComplete().add(newJob);
                }



            }


        }
        catch (IOException e)
        {
            System.out.println("Exception caught while trying to listen on port " +
                    serverSocket.getLocalPort() + " or listening for a connection");
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
