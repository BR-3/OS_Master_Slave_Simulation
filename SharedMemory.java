package yg;

import java.util.ArrayList;

public class SharedMemory {
    // we need to add more to shared memory and create locks for it
    int SlaveALoad = 0;
    int SlaveBLoad = 0;
    boolean SlaveAIsOpen = true;
    boolean SlaveBIsOpen = true;
    ArrayList<Job> doneJobs = new ArrayList<>();
    ArrayList<Job> jobsToComplete = new ArrayList<>();
    ArrayList<Job> jobsForSlaveA = new ArrayList<>();
    ArrayList<Job> jobsForSlaveB = new ArrayList<>();
    // these will be the locks that we can create synchronized blocks
    Object jobsToComplete_LOCK = new Object();
    Object jobsForSlaveA_LOCK = new Object();
    Object jobsForSlaveB_LOCK = new Object();
    Object doneJobs_LOCK = new Object();
    Object slaveALoad_LOCK = new Object();
    Object slaveBLoad_LOCK = new Object();


    // getters and setters:

    public ArrayList<Job> getJobsToComplete() {
        return jobsToComplete;
    }

    public void setJobsToComplete(ArrayList<Job> jobsToComplete) {
        this.jobsToComplete = jobsToComplete;
    }

    public ArrayList<Job> getJobsForSlaveA() {
        return jobsForSlaveA;
    }

    public void setJobsForSlaveA(ArrayList<Job> jobsForSlaveA) {
        this.jobsForSlaveA = jobsForSlaveA;
    }

    public ArrayList<Job> getJobsForSlaveB() {
        return jobsForSlaveB;
    }

    public void setJobsForSlaveB(ArrayList<Job> jobsForSlaveB) {
        this.jobsForSlaveB = jobsForSlaveB;
    }

    public void setSlaveALoad(int load) {
        SlaveALoad = load;
    }

    public void setSlaveBLoad(int load) {
        SlaveBLoad = load;
    }

    public int getSlaveALoad() {
        return SlaveALoad;
    }

    public int getSlaveBLoad() {
        return SlaveBLoad;
    }

    public void setJobsToCompleteArray(ArrayList<Job> jobsToComplete) {
            this.jobsToComplete.clear();
            this.jobsToComplete.addAll(jobsToComplete);
    }
    public ArrayList<Job> getJobsToCompleteArray() {
        return jobsToComplete;
    }
    public ArrayList<Job> getDoneJobs() {return doneJobs;}

    public Object getJobsToComplete_LOCK() {return jobsToComplete_LOCK;}
    public Object getJobsForSlaveA_LOCK() {return jobsForSlaveA_LOCK; }
    public Object getJobsForSlaveB_LOCK() {return jobsForSlaveB_LOCK;}
    public Object getSlaveALoad_LOCK() {return slaveALoad_LOCK;}
    public Object getSlaveBLoad_LOCK() {return slaveBLoad_LOCK;}
    public Object getDoneJobs_LOCK() {return doneJobs_LOCK;}

    public boolean getSlaveAIsOpen() {return SlaveAIsOpen;}
    public boolean getSlaveBIsOpen() {return SlaveBIsOpen;}
    public void setSlaveAIsOpen(boolean value) {SlaveAIsOpen = value;}
    public void setSlaveBIsOpen(boolean value) {SlaveBIsOpen = value;}
}
