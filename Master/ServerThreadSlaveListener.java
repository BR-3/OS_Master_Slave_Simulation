package yg.Master;

import yg.Job;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This thread will listen for finished jobs from the slave
 * and add them to the arraylist of done jobs in shared memory.
 */
public class ServerThreadSlaveListener implements Runnable{
    private ServerSocket serverSocket = null;
    int id;
    int args;
    ArrayList<Job> doneJobs;
    private Object doneJobs_Lock;
    public ServerThreadSlaveListener(ServerSocket serverSocket, int id, int args,
                                     ArrayList<Job> doneJobs, Object doneJobs_Lock) {
        this.args = args;
        this.id = id;
        this.serverSocket = serverSocket;
        this.doneJobs = doneJobs;
        this.doneJobs_Lock = doneJobs_Lock;
    }
    public void run() {
        try (
                Socket clientSocket = serverSocket.accept();
                ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                //to read incoming messages from slave:
                BufferedReader inSlave = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            Object input;
            while ((input = objectIn.readObject()) != null)
            {
                Job finishedJob = (Job) input;
                synchronized(doneJobs_Lock)
                {
                    doneJobs.add(finishedJob); // need a synchronized lock on this
                }
            }

        }   catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
