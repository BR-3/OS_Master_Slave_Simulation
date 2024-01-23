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
                    synchronized(doneJobs_Lock)
                    {
                        currDoneJobs = new ArrayList<>(Slave.getDoneAJobs());
                    }

                    for(Job currJob : currDoneJobs)
                    {
                        synchronized(doneJobs_Lock)
                        {
                            objectOut.writeObject(Slave.getDoneAJobs().remove(currJob));
                            objectOut.flush();
                        }
                        System.out.println("SlaveServerAWriter: Sending to Slave socket on master: Client: "
                                + currJob.getClient() + ", Type: " + currJob.getType() + ", ID: " + currJob.getID());
                    }
                }
                else if(slaveType == 'B')
                {
                    synchronized(doneJobs_Lock)
                    {
                        currDoneJobs = new ArrayList<>(Slave.getDoneBJobs());
                    }

                    for(Job currJob : currDoneJobs)
                    {
                        synchronized(doneJobs_Lock)
                        {
                            objectOut.writeObject(Slave.getDoneBJobs().remove(currJob));
                            objectOut.flush();
                        }
                        System.out.println("SlaveServerBWriter: Sending to Slave socket on master: Client: "
                                + currJob.getClient() + ", Type: " + currJob.getType() + ", ID: " + currJob.getID());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
