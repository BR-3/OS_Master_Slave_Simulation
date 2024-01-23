package yg.Slave;

import yg.Job;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SlaveAServerWriter implements Runnable{
    private Socket clientSocket;
    private Object doneAJobs_Lock;

    boolean runThread = true;

    public SlaveAServerWriter(Socket clientSocket, Object doneAJobs_Lock) {
        this.clientSocket = clientSocket;
        this.doneAJobs_Lock = doneAJobs_Lock;
    }

    public void run()
    {
        try(
                ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
                ) {
            while(runThread)
            {
                // to use as current statuses:
                ArrayList<Job> currDoneAJobs;

                synchronized (doneAJobs_Lock)
                {
                    currDoneAJobs = new ArrayList<>(Slave.getDoneAJobs());
                }

                for (Job currJob : currDoneAJobs)
                {
                    // remove each one from original array
                    synchronized (doneAJobs_Lock)
                    {
                        Slave.getDoneAJobs().remove(currJob);
                    }
                    // write it to the socket
                    System.out.println("SlaveAServerWriter: Sending to Slave A socket: Client: "
                            + currJob.getClient() + ", Type: " + currJob.getType() + ", ID: " + currJob.getID());
                    objectOut.writeObject(currJob);
                    objectOut.flush();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
