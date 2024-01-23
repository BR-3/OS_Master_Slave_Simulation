package yg.Slave;

import yg.Job;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SlaveBServerWriter implements Runnable{
    private Socket clientSocket;
    private Object doneBJobs_Lock;

    boolean runThread = true;

    public SlaveBServerWriter(Socket clientSocket, Object doneBJobs_Lock) {
        this.clientSocket = clientSocket;
        this.doneBJobs_Lock = doneBJobs_Lock;
    }

    public void run()
    {
        try(
                ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
        ) {
            while(runThread)
            {
                // to use as current statuses:
                ArrayList<Job> currDoneBJobs;

                synchronized (doneBJobs_Lock)
                {
                    currDoneBJobs = new ArrayList<>(Slave.getDoneBJobs());
                }

                for (Job currJob : currDoneBJobs)
                {
                    // remove each one from original array
                    synchronized (doneBJobs_Lock)
                    {
                        Slave.getDoneBJobs().remove(currJob);
                    }
                    // write it to the socket
                    System.out.println("SlaveBServerWriter: Sending to Slave B socket: Client: "
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

