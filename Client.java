package yg;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

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
                ObjectOutputStream clientObjectOutput = new ObjectOutputStream (clientSocket.getOutputStream());
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
//            Scanner kb = new Scanner(System.in);
            String userInput;
            int id = 0;
            while((userInput = stdIn.readLine()) != null)
            {
//                String job = kb.nextLine();
                if (userInput.equals("a") || userInput.equals("b"))
                {
                    String job = userInput + "-" + id;
                    System.out.println("New job created: " + job);

//                    char type = job.charAt(0);  // Extract the first character
                    // this is the new job that it will send to master
//                    Job newJob = new Job(type,id);
//                    System.out.println("New job created. Type: " + job + " ID: " + id);
                    id++;

                    // this sends the newJob to the master
//                    clientObjectOutput.writeObject(newJob);
                    out.println(job);
                    //write message to console:
                    System.out.println("Sending to master: " + job);

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