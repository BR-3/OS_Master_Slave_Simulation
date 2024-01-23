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
                ArrayList<Job> currDoneJobs = null;
                synchronized(doneJobs_Lock)
                {
                    currDoneJobs = new ArrayList<>(Slave.getSlaveDoneJobs());
                }

                for(Job currJob : currDoneJobs)
                {
                    synchronized(doneJobs_Lock)
                    {
                        objectOut.writeObject(Slave.getSlaveDoneJobs().remove(currJob));
                        objectOut.flush();
                    }
                    System.out.println("SlaveServerWriter: Sending to Slave socket on master: Client: "
                            + currJob.getClient() + ", Type: " + currJob.getType() + ", ID: " + currJob.getID());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
