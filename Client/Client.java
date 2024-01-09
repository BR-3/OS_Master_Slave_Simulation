package yg.Client;

import yg.Job;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The client instantiates its writer and listener threads,
 * which listen from the user and master and write to the master.
 */
public class Client {

    public static void main(String[] args) {
        // these args are hardcoded, but we can enter them if we want into the command line
        args = new String[]{"127.0.0.1", "30121"};

        if (args.length != 2)
        {
            System.err.println("Usage: java client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumberC = Integer.parseInt(args[1]);

        /*------------------------------------------------------------*/
        try (
                //sockets for connections between client and master (server)
                Socket clientSocket = new Socket(hostName, portNumberC);
                )
        {
            // array for the client threads:
            ArrayList<Thread> clientThreads = new ArrayList<>();

            // need some instance of shared memory here
            ClientSharedMemory sharedMemory = new ClientSharedMemory();

            // creating the threads
            clientThreads.add(new Thread(new ClientThreadServerListener(clientSocket, 1, sharedMemory)));
            clientThreads.add(new Thread(new ClientThreadServerWriter(clientSocket, 1, sharedMemory)));

            // starting the client threads
            for (Thread t : clientThreads)
            {
                t.start();
            }

            // waiting for all the threads to finish
            for(Thread t : clientThreads)
            {
                try
                {
                    t.join();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();;
                }
            }

        }
        catch (UnknownHostException e)
        {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
            }
}