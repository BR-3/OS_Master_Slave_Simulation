package yg.Client;

import yg.Job;

import java.io.*;
import java.net.Socket;

/**
 * Client's WRITER threads write to the master. It takes the user's
 * input from another thread, and sends it to the master, who then
 * decides which slave to send it to.
 */
public class ClientThreadServerWriter implements Runnable{
    private Socket clientSocket;
    private int clientId;
    private ClientSharedMemory sharedMemory;
    private Object jobList_LOCK;
    boolean runThread = true;
    public ClientThreadServerWriter(Socket clientSocket, int clientId, ClientSharedMemory sharedMemory)
    {
        this.clientSocket = clientSocket;
        this.clientId = clientId;
        this.jobList_LOCK = sharedMemory.getJobList_LOCK();
    }

    public void run() {
        try (
                // this output stream will send jobs to master.
                ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
                BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
                ) {
            while(runThread)
            {
                String user;
                int jobId = 0;

                while( (user = userIn.readLine()) != null)
                {
                    if (user.equals("a") || user.equals("b"))
                    {
                        System.out.println("Received: User entered a new job of type: " + user);
                        char type = user.charAt(0);  // Extract the first character
                        // this is the new job OBJECT that it will send to master
                        Job newJob = new Job(type, jobId, clientId);
                        System.out.println("New job created. Type: " + type + ", ID: " + jobId);
                        jobId ++;

//                        sharedMemory.getJobList().add(newJob);

                        //write message to console:
                        System.out.println("Sending to master.");

                        // this sends the newJob to the master
                        objectOut.writeObject(newJob);
                        objectOut.flush();

                    }
                    else
                    {
                        System.out.println("Invalid entry. Please enter a type of job (a or b): ");
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
