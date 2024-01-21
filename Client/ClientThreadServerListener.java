package yg.Client;

import yg.Job;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This thread will listen for the messages from the server
 * saying which jobs are finished. When it receives a finished job, it will
 * print it out
 */

public class ClientThreadServerListener implements Runnable{
    private Socket clientSocket;
    private int clientID;
    private final ObjectInputStream objectIn;

    public ClientThreadServerListener(Socket clientSocket, int clientID, ObjectInputStream objectIn)
    {
        this.clientSocket = clientSocket;
        this.clientID = clientID;
        this.objectIn = objectIn;
    }

    public void run()
    {
        System.out.println("serverListener is working");

        try (
                objectIn;
        ) {
            Object input;
            while((input = objectIn.readObject()) != null)
            {
                Job finishedJob = (Job) input;

                // this will print out the received job only if it is the client that sent it
                if(clientID == finishedJob.getClient())
                {
                    System.out.println("Client " + clientID + " received finished job type " + finishedJob.getType() + ", id " + finishedJob.getID());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
