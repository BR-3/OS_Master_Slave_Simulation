package yg.Slave;

import yg.Job;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class SlaveBServerListener implements Runnable {
    private Socket clientSocket;
    private char optimalJob;
    private Object doneBJobs_Lock;

    public SlaveBServerListener(Socket clientSocket, char optimalJob, Object doneBJobs_Lock)
    {
        this.clientSocket = clientSocket;
        this.optimalJob = optimalJob;
        this.doneBJobs_Lock = doneBJobs_Lock;
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
                System.out.println("Slave " + optimalJob +" Received Job. Client: " + currJob.getClient() + ", Type: " + currJob.getType() + ", ID: " + currJob.getID());
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

                synchronized (doneBJobs_Lock)
                {
                    Slave.doneBJobs.add(currJob);
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