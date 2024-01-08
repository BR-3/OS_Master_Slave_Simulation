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
    jobsToCompleteClass jobsToComplete2;
    private volatile ArrayList<Job> jobsToComplete;
    private ArrayList<Job> jobsForSlaveA;
    private ArrayList<Job> jobsForSlaveB;
    private SlaveALoad slaveALoad;
    private SlaveBLoad slaveBLoad;
    private Object jobsToComplete_Lock;
    private Object jobsForSlaveA_Lock;
    private Object jobsForSlaveB_Lock;
    private Object slaveALoad_Lock;
    private Object slaveBLoad_Lock;

    public ServerThreadDecider(jobsToCompleteClass jobsToComplete2,
                               ArrayList<Job> jobsToComplete, ArrayList<Job> jobsForSlaveA, ArrayList<Job> jobsForSlaveB,
                               SlaveALoad slaveALoad, SlaveBLoad slaveBLoad,
                               Object jobsToComplete_Lock, Object jobsForSlaveA_Lock, Object jobsForSlaveB_Lock,
                               Object slaveALoad_Lock, Object slaveBLoad_Lock) {
        this.jobsToComplete2 = jobsToComplete2;
        this.jobsToComplete = jobsToComplete;
        this.jobsForSlaveA = jobsForSlaveA;
        this.jobsForSlaveB = jobsForSlaveB;
        this.slaveALoad = slaveALoad;
        this.slaveBLoad = slaveBLoad;
        // Objects for locking
        this.jobsToComplete_Lock = jobsToComplete_Lock;
        this.jobsForSlaveA_Lock = jobsForSlaveA_Lock;
        this.jobsForSlaveB_Lock = jobsForSlaveB_Lock;
        this.slaveALoad_Lock = slaveALoad_Lock;
        this.slaveBLoad_Lock = slaveBLoad_Lock;
    }

    @Override
    public void run() {
        System.out.println("Hi from ServerThreadDecider!");
        System.out.println("Number of jobs to complete: " + jobsToComplete.size());

//        int slaveALoad = SlaveA.getCurrentLoad();
//        int slaveBLoad = SlaveA.getCurrentLoad(); //switch to slaveB later after edit slaveB

        while (!jobsToComplete2.getJobsToCompleteArray().isEmpty()) // NOT WORKING: for some reason it never realizes that the other thread added jobs to the array
        {
            System.out.println("inside while loop");
            System.out.flush();
            Job currJob;
            synchronized (jobsToComplete_Lock) {
                // Ensure exclusive access to jobsToComplete
                currJob = jobsToComplete.remove(0);
            }

            // Perform job type-specific logic

            synchronized (slaveALoad_Lock) {
                synchronized (slaveBLoad_Lock) {
                    if (currJob.getType() == 'a') {
                        if ( (slaveALoad.getSlaveALoad() + 2) <= (slaveBLoad.getSlaveBLoad() + 10) ) {
                            //send to slave A array
                            synchronized (jobsForSlaveA_Lock) {
                                jobsForSlaveA.add(currJob);
                            }
                            System.out.println("Sending to slave A array: " + currJob.getType() + currJob.getID());
                            slaveALoad.add(2);
                        } else {
                            //send to Slave B array
                            synchronized (jobsForSlaveB_Lock) {
                                jobsForSlaveB.add(currJob);
                            }
                            System.out.println("Sending to slave B array: " + currJob.getType() + currJob.getID());
                            slaveBLoad.add(10);
                        }
                    } else if (currJob.getType() == 'b') {
                        if ( (slaveBLoad.getSlaveBLoad() + 2) <= (slaveALoad.getSlaveALoad() + 10) ) {
                            //sending to Slave B array
                            synchronized (jobsForSlaveB_Lock) {
                                jobsForSlaveB.add(currJob);
                            }
                            System.out.println("Sending to slave B array: " + currJob.getType() + currJob.getID());
                            slaveBLoad.add(2);
                        } else {
                            //sending to Slave A array
                            synchronized (jobsForSlaveA_Lock) {
                                jobsForSlaveA.add(currJob);
                            }
                            System.out.println("Sending to slave A array: " + currJob.getType() + currJob.getID());
                            slaveALoad.add(10);
                        }
                    }
                }
            }


        }
    }
}
