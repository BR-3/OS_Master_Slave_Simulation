package yg;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientThread extends Thread{
    boolean runThread = true;
    String hostName;
    int portNumber;
    int id = 0;

    public ClientThread(String hostName, int portNumber) {
        this.hostName= hostName;
        this.portNumber = portNumber;
    }

    public void run() {
        try (
                //sockets for connections between client and master (server)
                Socket clientSocket = new Socket(hostName, portNumber);
                ObjectOutputStream clientObjectOutput = new ObjectOutputStream (clientSocket.getOutputStream());
                PrintWriter requestWriter = //stream to write text requests to server
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader infoReader = //stream to read response from server
                        new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream()));
        ) {
            while(runThread) {
                // this will read from the user
                Scanner kb = new Scanner(System.in);
                System.out.println("Please enter a type of job (a or b): ");
                String jobType = kb.nextLine();
                if (jobType.equals("a") || jobType.equals("b"))
                {
                    System.out.println("Received: User entered a new job of type: " + jobType);
                }
                else
                {
                    System.out.println("Invalid entry. Please enter a type of job (a or b): ");
                }


                // this determines the type
                Type type = null;
                if(jobType.equals('a')) {
                    type = Type.a;
                } else if(jobType.equals('b')) {
                    type = Type.b;
                }
                // this is the new job that it will send to master
                Job newJob = new Job(type,id);
                id++;

                // this sends the newJob to the master
                clientObjectOutput.writeObject(newJob);
                //write message to console:
                System.out.println("Sending job to master, Type: " + newJob.getType() + " ID: " + newJob.getID());

            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }

        // we might need to close socket and inputs/outputs but will leave for now
    }
}