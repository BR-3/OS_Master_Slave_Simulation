package yg;

import java.io.*;
import java.net.*;
import java.util.*;

public class SlaveA {
    public static void main(String[] args) {
        args = new String[]{"127.0.0.1", "30122"};

        if (args.length != 2) {
            System.err.println("Usage: java client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                //sockets for connections between client and master (server)
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter requestWriter = //stream to write text requests to server
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader infoReader = //stream to read response from server
                        new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream()));
                ObjectInputStream jobInputStream = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            // this is hwat it does better
            String optimalJob = String.valueOf('a');
            // this will hold done jobs
            ArrayList<Job> doneJobs = new ArrayList<Job>();

            Job currentJob;
            while(jobInputStream.readObject() != null) {
                currentJob = (Job) jobInputStream.readObject();
                if(currentJob.getType().equals(optimalJob)) {
                    Thread.sleep(3000);// sleep for 3 seconds
                } else {
                    Thread.sleep(5000);// sleep for 5 seconds
                }
                doneJobs.add(currentJob);

                // this tells the master that it is now available
                requestWriter.println(true);
            }



        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



}