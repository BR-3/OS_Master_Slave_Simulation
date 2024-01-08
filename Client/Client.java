package yg.Client;

import yg.Job;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * needs to be split into threads...
 * right now it's only writing to master
 * but need to split up so that 1 threads writes to master
 * and 1 thread reads from master
 */
public class Client {

    public static void main(String[] args) {
        // this is going to be a temporary list to hold jobs

        // these args are hardcoded, but we can enter them if we want into the command line
        args = new String[]{"127.0.0.1", "30121"};

        if (args.length != 2) {
            System.err.println("Usage: java client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        /*------------------------------------------------------------*/
        try (
                //sockets for connections between client and master (server)
                Socket clientSocket = new Socket(hostName, portNumber);
//                ObjectOutputStream clientObjectOutput = new ObjectOutputStream (clientSocket.getOutputStream());
                ObjectOutputStream clientObjectOutput = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                PrintWriter out = //stream to write text requests to server
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = //stream to read response from server
                        new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream()));
                BufferedReader stdIn = // read from user
                        new BufferedReader(
                                new InputStreamReader(System.in))
                )
        {
            String userInput;
            int id = 0;
            while((userInput = stdIn.readLine()) != null)
            {
                if (userInput.equals("a") || userInput.equals("b"))
                {
                    char type = userInput.charAt(0);  // Extract the first character
                    // this is the new job OBJECT that it will send to master
                    Job newJob = new Job(type, id, 0);
                    System.out.println("New job created. Type: " + type + " ID: " + id);
                    id++;

                    // this sends the newJob to the master
                    clientObjectOutput.writeObject(newJob);
                    clientObjectOutput.flush();

                    //write message to console:
                    System.out.println("Sending to master.");

                }
                else
                {
                    System.out.println("Invalid entry. Please enter a type of job (a or b): ");
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



        /*------------------------------------------------------------*/





        // everything the client does will be on a thread
        //RB edited this since prof. Novick said on slack that we just run the same client program twice.
        // so we only need one instanciation of it, but when we run it in command line we should run it twice.
        /*
        ClientThread clientThread = new ClientThread(hostName, portNumber);
        clientThread.start();
        */


    }
}