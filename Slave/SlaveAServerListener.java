package yg.Slave;

import yg.Job;

import java.io.*;
import java.net.Socket;

public class SlaveAServerListener implements Runnable{
    private Socket clientSocket;
    private char optimalJob;
    private Object doneJobs_Lock;

    public SlaveAServerListener(Socket clientSocket, char optimalJob, Object doneJobs_Lock)
    {
        this.clientSocket = clientSocket;
        this.optimalJob = optimalJob;
        this.doneJobs_Lock = doneJobs_Lock;
    }

    public void run()
    {
        try(
                ObjectInputStream objectIn = new ObjectInputStream(
                        new BufferedInputStream(clientSocket.getInputStream()));
                ) {
            Object input;
            while((input = objectIn.readObject()) != null)
            {
                Job currJob = (Job) input;
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

                synchronized (doneJobs_Lock)
                {
                    SlaveA.doneJobs.add(currJob);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
