package yg.Master;

import yg.Job;

import java.util.ArrayList;

/**
 * Master's DONE-DECIDER thread to take jobs from the
 * doneJobs array and send them to the
 * client0DoneJobs array or the client1 doneJobsArray
 */
public class ServerThreadDoneDecider implements Runnable {

    private ServerSharedMemory sharedMemory;

    public ServerThreadDoneDecider(ServerSharedMemory sharedMemory)
    {
        this.sharedMemory = sharedMemory;
    }
    @Override
    public void run()
    {
        while (true)
        {
            ArrayList<Job> currDoneJobs;

            synchronized (sharedMemory.getDoneJobs_LOCK())
            {
                currDoneJobs = new ArrayList<>(sharedMemory.getDoneJobs());
            }

            for (Job currDoneJob : currDoneJobs)
            {
                synchronized (sharedMemory.getDoneJobs_LOCK())
                {
                    sharedMemory.getDoneJobs().remove(currDoneJob);
                }

                if (currDoneJob.getClient() == 0)
                {
                    System.out.println("DoneDecider sending to client0 array.");
                    synchronized (sharedMemory.getClient0DoneJobs_LOCK())
                    {
                        sharedMemory.getClient0DoneJobs().add(currDoneJob);
                    }
                }
                else
                {
                    System.out.println("DoneDecider sending to client1 array.");
                    synchronized (sharedMemory.getClient1DoneJobs_LOCK())
                    {
                        sharedMemory.getClient1DoneJobs().add(currDoneJob);
                    }
                }
                System.out.println("UPDATE: Client0 array: " + sharedMemory.getClient0DoneJobs() +
                        ", Client1 array: " + sharedMemory.getClient1DoneJobs());
            }

        }


    }
}
