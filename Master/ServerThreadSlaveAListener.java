package yg.Master;

import yg.Job;

import java.io.*;
import java.net.*;

/**
 * This thread will listen for finished jobs from the slave
 * and add them to the arraylist of done jobs in shared memory.
 */

// chatGPT's idea: not sure if works yet...
public class ServerThreadSlaveAListener implements Runnable {
    private ServerSocket serverSocket;
    private ServerSharedMemory sharedMemory;
    private Object doneJobs_Lock;

    public ServerThreadSlaveAListener(ServerSocket serverSocket, ServerSharedMemory sharedMemory) {
        this.serverSocket = serverSocket;
        this.doneJobs_Lock = sharedMemory.getDoneJobs_LOCK();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ConnectionHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught while trying to listen on port " +
                    serverSocket.getLocalPort() + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    private class ConnectionHandler implements Runnable {
        private Socket clientSocket;

        public ConnectionHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (ObjectInputStream objectIn = new ObjectInputStream(
                    new BufferedInputStream(clientSocket.getInputStream()))) {

                System.out.println("Hi from ServerThreadSlaveAListener - the thread is working:))");

                Object input;
                while ((input = objectIn.readObject()) != null) {
                    Job finishedJob = (Job) input;
                    System.out.println("Received from slave A - DONE job: Client: " +
                            finishedJob.getClient() + ", type: " + finishedJob.getType() + ", id: " + finishedJob.getID());
                    synchronized (doneJobs_Lock) {
                        sharedMemory.getDoneJobs().add(finishedJob);
                    }
                }

            } catch (EOFException e) {
                // Client disconnected, continue accepting connections
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}



/*public class ServerThreadSlaveAListener implements Runnable{
    private ServerSocket serverSocket = null;
    private ServerSharedMemory sharedMemory;
    int args;
    private Object doneJobs_Lock;
    public ServerThreadSlaveAListener(ServerSocket serverSocket,
                                      ServerSharedMemory sharedMemory) {
        this.serverSocket = serverSocket;
        this.doneJobs_Lock = sharedMemory.getDoneJobs_LOCK();
    }


    @Override
    public void run() {
        try (Socket clientSocket = serverSocket.accept();
             ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));

        )
        {
            System.out.println("Hi from ServerThreadSlaveAListener- the thread is working:))");

            Object input;
            while ( (input = objectIn.readObject()) != null)
            {
                Job finishedJob = (Job) input;
                System.out.println("Received from slave A - DONE job: Client: " +
                        finishedJob.getClient() + ", type: " + finishedJob.getType() + ", id: " + finishedJob.getID());
                synchronized(doneJobs_Lock)
                {
                    sharedMemory.getDoneJobs().add(finishedJob);
                }
            }

        }
        catch (EOFException e)
        {
            //Client disconnected, continue accepting connections
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
}*/
