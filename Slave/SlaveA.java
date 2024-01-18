package yg.Slave;

import yg.Job;
import yg.SendMessage;

import java.io.*;
import java.net.*;

public class SlaveA {
    public static void main(String[] args)  {
      // args = new String[]{"127.0.0.1", "30122"};

        if (args.length != 2) {
            System.err.println("Usage: java client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                //sockets for connections between client (= slave) and master (server)
                Socket clientSocket = new Socket(hostName, portNumber);
                ObjectInputStream jobInputStream = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                ObjectOutputStream jobOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        ) {
            while (true)
            {
                System.out.println("hi from slave A");
                char optimalJob = 'a';
                //while(SendMessage.sendingToSlaveA >=0) {
                    Object input = jobInputStream.readObject();
                    Job currJob = (Job) input;
                    SendMessage.readFromSlaveA();
                    System.out.println("Received Job. Client: " + currJob.getClient() + ", Type: " + currJob.getType() + ", ID: " + currJob.getID());
                    if(currJob.getType() == optimalJob)
                    {
                        System.out.println("Job is optimal, takes 2 seconds to complete.");
                        Thread.sleep(2000);
                    }
                    else
                    {
                        System.out.println("Job is not optimal, takes 10 seconds to complete.");
                        Thread.sleep(10000);
                    }
                    System.out.println("Completed job and sending to master. Client: " + currJob.getClient() + ", Type: " + currJob.getType() + " ID: " + currJob.getID() + "\n");
                    jobOutputStream.writeObject(currJob);
                    jobOutputStream.flush();
               // }
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}