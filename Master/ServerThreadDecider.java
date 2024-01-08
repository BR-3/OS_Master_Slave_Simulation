package yg.Master;

import yg.Job;
import yg.Slave.SlaveA;

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
    private ArrayList<Job> jobsToComplete;
    private ArrayList<Job> jobsForSlaveA;
    private ArrayList<Job> jobsForSlaveB;

    public ServerThreadDecider(ArrayList<Job> jobsToComplete, ArrayList<Job> jobsForSlaveA, ArrayList<Job> jobsForSlaveB) {
        this.jobsToComplete = jobsToComplete;
        this.jobsForSlaveA = jobsForSlaveA;
        this.jobsForSlaveB = jobsForSlaveB;
    }

    @Override
    public void run() {
        int slaveALoad = SlaveA.getCurrentLoad();
        int slaveBLoad = SlaveA.getCurrentLoad(); //switch to slaveB later after edit slaveB

        while (!jobsToComplete.isEmpty())
        {
            Job currJob = jobsToComplete.remove(0);
            if (currJob.getType() == 'a')
            {
                if (slaveALoad + 2 <= slaveBLoad + 10) // not sure if calculations are 100% accurate...
                {
                    //send to slave A array
                    System.out.println("Sending to slave A array: " + currJob.getType() + currJob.getID());
                    jobsForSlaveA.add(currJob);
                }
                else
                {
                    //send to Slave B array
                    System.out.println("Sending to slave B array: " + currJob.getType() + currJob.getID());
                    jobsForSlaveB.add(currJob);
                }
            }
            else if (currJob.getType() == 'b')
            {
                if (slaveBLoad + 2 <= slaveALoad + 10)
                {
                    //sending to Slave A array
                    System.out.println("Sending to slave A array: " + currJob.getType() + currJob.getID());
                    jobsForSlaveA.add(currJob);
                }
                else
                {
                    //sending to Slave B array
                    System.out.println("Sending to slave B array: " + currJob.getType() + currJob.getID());
                    jobsForSlaveB.add(currJob);
                }
            }



        }

    }
}
