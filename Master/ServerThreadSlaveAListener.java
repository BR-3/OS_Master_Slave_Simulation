package yg.Master;

import yg.Job;

import java.io.*;
import java.net.*;

/**
 * This thread will listen for finished jobs from the slave
 * and add them to the arraylist of done jobs in shared memory.
 */





public class ServerThreadSlaveAListener implements Runnable{
    private final ObjectInputStream objectInSA;
    private final ServerSharedMemory sharedMemory;
    private final Object doneJobs_Lock;
    public ServerThreadSlaveAListener(ObjectInputStream objectInSA,
                                      ServerSharedMemory sharedMemory) {
        this.objectInSA = objectInSA;
        this.doneJobs_Lock = sharedMemory.getDoneJobs_LOCK();
        this.sharedMemory = sharedMemory;
    }



    @Override
    public void run() {
        System.out.println("Hi from serverThreadSlaveAListener before connecting");
        try ( /*Socket clientSocket = serverSocket.accept();
             ObjectInputStream objectIn = new ObjectInputStream(
                     new BufferedInputStream(clientSocket.getInputStream()));*/
        objectInSA;
        )
        {
            System.out.println("Hi from ServerThreadSlaveAListener- the thread is working:))"); // THIS IS NOT WORKING YET
            Object input;
            while ((input = objectInSA.readObject()) != null)
            {
                Job finishedJob = (Job) input;
                System.out.println("Received from slave A - DONE job: Client: " +
                        finishedJob.getClient() + ", type: " + finishedJob.getType() + ", id: " + finishedJob.getID());
                int reducedLoad;
                if(finishedJob.getType() == 'a')
                {
                    reducedLoad = -2;
                } else {
                    reducedLoad = -10;
                }
                synchronized(doneJobs_Lock)
                {
                    sharedMemory.getDoneJobs().add(finishedJob);
                    sharedMemory.addSlaveALoad(reducedLoad);
                }
            }

        }
        catch (EOFException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

    }
}
