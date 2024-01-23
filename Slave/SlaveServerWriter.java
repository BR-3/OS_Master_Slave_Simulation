package yg.Slave;

import yg.Job;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SlaveServerWriter implements Runnable{
    private Socket clientSocket;
    private Object doneJobs_Lock;

    boolean runThread = true;

    public SlaveServerWriter(Socket clientSocket, Object doneJobs_Lock) {
        this.clientSocket = clientSocket;
        this.doneJobs_Lock = doneJobs_Lock;
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

                synchronized (doneJobs_Lock)
                {
                    currDoneAJobs = new ArrayList<>(Slave.getDoneAJobs());
                }

                for (Job currJob : currDoneAJobs)
                {
                    // remove each one from original array
                    if(currJob.getType() == 'a' )
                    {
                        synchronized (doneJobs_Lock)
                        {
                            Slave.getDoneAJobs().remove(currJob);
                        }
                    } else {
                        synchronized (doneJobs_Lock)
                        {
                            Slave.getDoneAJobs().remove(currJob);
                        }
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
