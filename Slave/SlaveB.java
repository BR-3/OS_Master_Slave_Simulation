package yg.Slave;

import yg.Job;
import yg.Master.ServerSharedMemory;

import java.io.*;
import java.net.*;

public class SlaveB {
    public static void main(String[] args) {
        args = new String[]{"127.0.0.1", "30123"};

        if (args.length != 2) {
            System.err.println("Usage: java client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                //sockets for connections between client and master (server)
                Socket clientSocket = new Socket(hostName, portNumber);
                ObjectInputStream jobInputStream = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                ObjectOutputStream jobOutputStream = new ObjectOutputStream(clientSocket.getOutputStream())
        ) {
            // this is what it does better
            char optimalJob = 'b';

            Object input;
            while((input = jobInputStream.readObject()) != null)
            {
                Job currentJob = (Job) input;
                if(currentJob.getType() == optimalJob)
                {
                    System.out.println("Job is optimal, takes 2 seconds to complete.");
                    Thread.sleep(2000);
                }
                else
                {
                    System.out.println("Job is not optimal, takes 10 seconds to complete.");
                    Thread.sleep(10000);
                }
                System.out.println("Completed job and sending to master. Client: " + currentJob.getClient() + ", Type: " + currentJob.getType() + " ID: " + currentJob.getID());
                jobOutputStream.writeObject(currentJob); // sending the done job to the master
                jobOutputStream.flush();
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