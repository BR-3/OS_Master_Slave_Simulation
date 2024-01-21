package yg.Master;

import yg.Job;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Master's DECIDER thread to decide which slave to send job to
 * Takes each job from jobsToCompleteArray and puts it either on
 * the jobsForSlaveA array or jobsForSlaveB array
 * This thread DOES NOT deal with sockets.
 * (writer threads will take from those arrays and actually write
 * to slaves over socket)
 */
public class ServerThreadDecider implements Runnable{
    private ServerSharedMemory sharedMemory;
    private Object jobsToComplete_LOCK;
    private Object jobsForSlaveA_LOCK;
    private Object jobsForSlaveB_LOCK;
    private Object slaveALoad_LOCK;
    private Object slaveBLoad_LOCK;

    public ServerThreadDecider(ServerSharedMemory sharedMemory) {
        this.sharedMemory = sharedMemory;
        // Objects for locking
        this.jobsToComplete_LOCK = sharedMemory.getJobsToComplete_LOCK();
        this.jobsForSlaveA_LOCK = sharedMemory.getJobsForSlaveA_LOCK();
        this.jobsForSlaveB_LOCK = sharedMemory.getJobsForSlaveB_LOCK();
        this.slaveALoad_LOCK = sharedMemory.getSlaveALoad_LOCK();
        this.slaveBLoad_LOCK = sharedMemory.getSlaveBLoad_LOCK();
    }

    @Override
    public void run() {

        while (true)
        {
            System.out.println("ServerThreadDoneDecider is running! ");
            // to use as current statuses:
            ArrayList<Job> currJobsToComplete;
            int currSlaveALoad;
            int currSlaveBLoad;

            synchronized (jobsToComplete_LOCK)
            {
                currJobsToComplete = new ArrayList<>(sharedMemory.getJobsToComplete());
            }

            for (Job currJob : currJobsToComplete)
            {
                // remove each one from original array
                synchronized (jobsToComplete_LOCK)
                {
                    sharedMemory.getJobsToComplete().remove(currJob);
                }

                synchronized (slaveALoad_LOCK)
                {
                    currSlaveALoad = sharedMemory.getSlaveALoad();
                }
                synchronized (slaveBLoad_LOCK)
                {
                    currSlaveBLoad = sharedMemory.getSlaveBLoad();
                }

                // Perform job type-specific logic
                if (currJob.getType() == 'a') {
                    if ((currSlaveALoad + 2) <= (currSlaveBLoad + 10) ) {
                        //send to slave A array
                        synchronized (jobsForSlaveA_LOCK) { sharedMemory.getJobsForSlaveA().add(currJob); }
                        System.out.println("Sending to slave A array: Type: " + currJob.getType() + ", ID: " + currJob.getID());
                        synchronized (slaveALoad_LOCK) { sharedMemory.addSlaveALoad(2); }
                    } else {
                        //send to Slave B array
                        synchronized (jobsForSlaveB_LOCK) {sharedMemory.getJobsForSlaveB().add(currJob);}
                        System.out.println("Sending to slave B array: Type: " + currJob.getType() + ", ID: " + currJob.getID());
                        synchronized (slaveBLoad_LOCK) { sharedMemory.addSlaveBLoad(10); }
                    }
                } else if (currJob.getType() == 'b') {
                    if ( (sharedMemory.getSlaveBLoad() + 2) <= (sharedMemory.getSlaveALoad() + 10) ) {
                        //sending to Slave B array
                        synchronized (jobsForSlaveB_LOCK) {sharedMemory.getJobsForSlaveB().add(currJob);
                        }
                        System.out.println("Sending to slave B array: Type: " + currJob.getType() + ", ID: " + currJob.getID());
                        synchronized (slaveBLoad_LOCK) { sharedMemory.addSlaveBLoad(2); }
                    } else {
                        //sending to Slave A array
                        synchronized (sharedMemory.getJobsForSlaveA_LOCK()) {sharedMemory.getJobsForSlaveA().add(currJob);}
                        System.out.println("Sending to slave A array: Type: " + currJob.getType() + ", ID: " + currJob.getID());
                        synchronized (slaveALoad_LOCK) { sharedMemory.addSlaveALoad(10); }
                    }
                }
                System.out.println("Decider: updated loads: " + "slaveALoad: " + sharedMemory.getSlaveALoad() + ", slaveBLoad: " + sharedMemory.getSlaveBLoad() + "\n");



            }
        }

    }
}
