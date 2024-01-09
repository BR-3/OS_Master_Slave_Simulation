package yg.Master;

import yg.Job;

import java.util.ArrayList;

/**
 * Master's DECIDER thread to decide which slave to send job to
 * Takes each job from jobsToCompleteArray and puts it either on
 * the jobsForSlaveA array or jobsForSlaveB array
 * This thread DOES NOT deal with sockets.
 * (writer threads will take from those arrays and actually write
 * to slaves over socket)
 */
public class ServerThreadDecider implements Runnable{
    private ServerSharedMemory sharedMemory = new ServerSharedMemory();
   // jobsToCompleteClass jobsToComplete2;
    private volatile ArrayList<Job> jobsToComplete;
    private ArrayList<Job> jobsForSlaveA;
    private ArrayList<Job> jobsForSlaveB;
    private int slaveALoad;
    private int slaveBLoad;
    private Object jobsToComplete_LOCK;
    private Object jobsForSlaveA_LOCK;
    private Object jobsForSlaveB_LOCK;
    private Object slaveALoad_LOCK;
    private Object slaveBLoad_LOCK;

    public ServerThreadDecider(ServerSharedMemory sharedMemory) {
        this.jobsToComplete = sharedMemory.getJobsToComplete();
        this.jobsForSlaveA = sharedMemory.getJobsForSlaveA();
        this.jobsForSlaveB = sharedMemory.getJobsForSlaveB();
        this.slaveALoad = sharedMemory.getSlaveALoad();
        this.slaveBLoad = sharedMemory.getSlaveBLoad();
        // Objects for locking
        this.jobsToComplete_LOCK = sharedMemory.getJobsToComplete_LOCK();
        this.jobsForSlaveA_LOCK = sharedMemory.getJobsForSlaveA_LOCK();
        this.jobsForSlaveB_LOCK = sharedMemory.getJobsForSlaveB_LOCK();
        this.slaveALoad_LOCK = sharedMemory.getSlaveALoad_LOCK();
        this.slaveBLoad_LOCK = sharedMemory.getSlaveBLoad_LOCK();
    }

    @Override
    public void run() {
        System.out.println("Hi from ServerThreadDecider!");
        System.out.println("Number of jobs to complete: " + jobsToComplete.size());

        while (!jobsToComplete.isEmpty()) // NOT WORKING: for some reason it never realizes that the other thread added jobs to the array
        {
            System.out.println("inside while loop");
            System.out.flush();
            Job currJob;
            synchronized (jobsToComplete_LOCK) {
                // Ensure exclusive access to jobsToComplete
                currJob = jobsToComplete.remove(0);
            }

            // Perform job type-specific logic
            synchronized (slaveALoad_LOCK) {
                synchronized (slaveBLoad_LOCK) {
                    if (currJob.getType() == 'a') {
                        if ( (sharedMemory.getSlaveALoad() + 2) <= (sharedMemory.getSlaveBLoad() + 10) ) {
                            //send to slave A array
                            synchronized (jobsForSlaveA_LOCK) {
                                jobsForSlaveA.add(currJob);
                            }
                            System.out.println("Sending to slave A array: " + currJob.getType() + currJob.getID());
                            slaveALoad+=2;
                        } else {
                            //send to Slave B array
                            synchronized (jobsForSlaveB_LOCK) {
                                jobsForSlaveB.add(currJob);
                            }
                            System.out.println("Sending to slave B array: " + currJob.getType() + currJob.getID());
                            slaveBLoad+=10;
                        }
                    } else if (currJob.getType() == 'b') {
                        if ( (sharedMemory.getSlaveBLoad() + 2) <= (sharedMemory.getSlaveALoad() + 10) ) {
                            //sending to Slave B array
                            synchronized (jobsForSlaveB_LOCK) {
                                jobsForSlaveB.add(currJob);
                            }
                            System.out.println("Sending to slave B array: " + currJob.getType() + currJob.getID());
                            slaveBLoad+=2;
                        } else {
                            //sending to Slave A array
                            synchronized (jobsForSlaveA_LOCK) {
                                jobsForSlaveA.add(currJob);
                            }
                            System.out.println("Sending to slave A array: " + currJob.getType() + currJob.getID());
                            slaveALoad+=10;
                        }
                    }
                }
            }


        }
    }
}
