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
//    private volatile ArrayList<Job> jobsToComplete;
    private ArrayList<Job> jobsToComplete;
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
        this.sharedMemory = sharedMemory;
//        commented out bc each time we need to read/write we'll need to access each one new and lock on the original lock not the private copy
//        to get the current copy
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
        System.out.println("Number of jobs to complete: " + sharedMemory.getJobsToComplete().size());

        ArrayList<Job> currJobsToComplete;
        while (true)
        {
            synchronized (sharedMemory.getJobsToComplete_LOCK())
            {
                currJobsToComplete = new ArrayList<>(sharedMemory.getJobsToComplete());
            }

            // New attempt: YAY IT WORKS:)
            for (Job currJob : currJobsToComplete)
            {
                // remove each one from original array
                synchronized (sharedMemory.getJobsToComplete_LOCK())
                {
                    System.out.println("Array size before removal: " + sharedMemory.getJobsToComplete().size()); // for testing... could remove after
                    sharedMemory.getJobsToComplete().remove(currJob);
                    System.out.println("Array size after removal: " + sharedMemory.getJobsToComplete().size());
                }

                // Perform job type-specific logic
                synchronized (sharedMemory.getSlaveALoad_LOCK()) {
                    synchronized (sharedMemory.getSlaveBLoad_LOCK()) {
                        if (currJob.getType() == 'a') {
                            if ( (sharedMemory.getSlaveALoad() + 2) <= (sharedMemory.getSlaveBLoad() + 10) ) {
                                //send to slave A array
                                synchronized (sharedMemory.getJobsForSlaveA_LOCK()) {
                                    sharedMemory.getJobsForSlaveA().add(currJob);
                                }
                                System.out.println("Sending to slave A array: " + currJob.getType() + currJob.getID());
                                sharedMemory.addSlaveALoad(2);
                            } else {
                                //send to Slave B array
                                synchronized (sharedMemory.getJobsForSlaveB_LOCK()) {
                                    sharedMemory.getJobsForSlaveB().add(currJob);
                                }
                                System.out.println("Sending to slave B array: " + currJob.getType() + currJob.getID());
                                sharedMemory.addSlaveBLoad(10);
                            }
                        } else if (currJob.getType() == 'b') {
                            if ( (sharedMemory.getSlaveBLoad() + 2) <= (sharedMemory.getSlaveALoad() + 10) ) {
                                //sending to Slave B array
                                synchronized (sharedMemory.getJobsForSlaveB_LOCK()) {
                                    sharedMemory.getJobsForSlaveB().add(currJob);
                                }
                                System.out.println("Sending to slave B array: " + currJob.getType() + currJob.getID());
                                sharedMemory.addSlaveBLoad(2);
                            } else {
                                //sending to Slave A array
                                synchronized (sharedMemory.getJobsForSlaveA_LOCK()) {
                                    sharedMemory.getJobsForSlaveA().add(currJob);
                                }
                                System.out.println("Sending to slave A array: " + currJob.getType() + currJob.getID());
                                sharedMemory.addSlaveALoad(10);

                            }
                        }
                    }
                }
            }

            // old attempt:
            /*
            while (!currJobsToComplete.isEmpty())
            // NOT WORKING: for some reason it never realizes that the other thread added jobs to the array
            {
                System.out.println("inside while loop");
                System.out.println("Current array: " + currJobsToComplete);
//                System.out.flush();
                Job currJob;
                synchronized (sharedMemory.getJobsToComplete_LOCK()) {
                    // Ensure exclusive access to jobsToComplete
                    currJob = sharedMemory.getJobsToComplete().get(0);
                }
                //

                // Perform job type-specific logic
                synchronized (sharedMemory.getSlaveALoad_LOCK()) {
                    synchronized (sharedMemory.getSlaveBLoad_LOCK()) {
                        if (currJob.getType() == 'a') {
                            if ( (sharedMemory.getSlaveALoad() + 2) <= (sharedMemory.getSlaveBLoad() + 10) ) {
                                //send to slave A array
                                synchronized (sharedMemory.getJobsForSlaveA_LOCK()) {
                                    sharedMemory.getJobsForSlaveA().add(currJob);
                                }
                                System.out.println("Sending to slave A array: " + currJob.getType() + currJob.getID());
                                sharedMemory.addSlaveALoad(2);
                            } else {
                                //send to Slave B array
                                synchronized (sharedMemory.getJobsForSlaveB_LOCK()) {
                                    sharedMemory.getJobsForSlaveB().add(currJob);
                                }
                                System.out.println("Sending to slave B array: " + currJob.getType() + currJob.getID());
                                sharedMemory.addSlaveBLoad(10);
                            }
                        } else if (currJob.getType() == 'b') {
                            if ( (sharedMemory.getSlaveBLoad() + 2) <= (sharedMemory.getSlaveALoad() + 10) ) {
                                //sending to Slave B array
                                synchronized (sharedMemory.getJobsForSlaveB_LOCK()) {
                                    sharedMemory.getJobsForSlaveB().add(currJob);
                                }
                                System.out.println("Sending to slave B array: " + currJob.getType() + currJob.getID());
                                sharedMemory.addSlaveBLoad(2);
                            } else {
                                //sending to Slave A array
                                synchronized (sharedMemory.getJobsForSlaveA_LOCK()) {
                                    sharedMemory.getJobsForSlaveA().add(currJob);
                                }
                                System.out.println("Sending to slave A array: " + currJob.getType() + currJob.getID());
                                sharedMemory.addSlaveALoad(10);

                            }
                        }
                    }
                }
                synchronized (sharedMemory.getJobsToComplete_LOCK()) {
                    // Ensure exclusive access to jobsToComplete
                    sharedMemory.getJobsToComplete().remove(currJob);
                }


            }*/

        }

    }
}
