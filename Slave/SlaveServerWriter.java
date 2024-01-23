package yg.Slave;

import yg.Job;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SlaveServerWriter implements Runnable{
    private Socket clientSocket;
    private Object doneJobs_Lock;
    private char slaveType;
    private Job currJob;

    boolean runThread = true;

    public SlaveServerWriter(Socket clientSocket, Object doneJobs_Lock, char slaveType) {
        this.clientSocket = clientSocket;
        this.doneJobs_Lock = doneJobs_Lock;
        this.slaveType = slaveType;
    }

    public void run()
    {
        try(
                ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
                ) {
            while(runThread)
            {
                ArrayList<Job> currDoneJobs = null;
                
                if(slaveType == 'A')
                {
                    synchronized (doneJobs_Lock)
                    {
                        currDoneJobs = new ArrayList<>(Slave.getDoneAJobs());
                    }
                }
                else if(slaveType == 'B')
                {
                    synchronized (doneJobs_Lock)
                    {
                        currDoneJobs = new ArrayList<>(Slave.getDoneAJobs());
                    }
                }
                
                for (Job currJob : currDoneJobs)
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
                            Slave.getDoneBJobs().remove(currJob);
                        }
                    }
                }
                
                // to use as current statuses:
                // write it to the socket
                System.out.println("SlaveServerWriter: Sending to Slave socket on master: Client: "
                        + currJob.getClient() + ", Type: " + currJob.getType() + ", ID: " + currJob.getID());
                objectOut.writeObject(currJob);
                objectOut.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}