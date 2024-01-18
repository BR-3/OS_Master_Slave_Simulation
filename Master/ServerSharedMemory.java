package yg.Master;

import yg.Job;

import java.util.ArrayList;

public class ServerSharedMemory {
    // we need to add more to shared memory and create locks for it
    int SlaveALoad = 0;
    int SlaveBLoad = 0;
    ArrayList<Job> doneJobs = new ArrayList<>();
    ArrayList<Job> jobsToComplete = new ArrayList<>();
    ArrayList<Job> jobsForSlaveA = new ArrayList<>();
    ArrayList<Job> jobsForSlaveB = new ArrayList<>();
    ArrayList<Job> client0DoneJobs = new ArrayList<>();
    ArrayList<Job> client1DoneJobs = new ArrayList<>();

    // these will be the locks that we can create synchronized blocks
    Object jobsToComplete_LOCK = new Object();
    Object jobsForSlaveA_LOCK = new Object();
    Object jobsForSlaveB_LOCK = new Object();
    Object doneJobs_LOCK = new Object();
    Object slaveALoad_LOCK = new Object();
    Object slaveBLoad_LOCK = new Object();
    Object client0DoneJobs_LOCK = new Object();
    Object client1DoneJobs_LOCK = new Object();

// getters and setters:

    public ArrayList<Job> getJobsToComplete() {
        return jobsToComplete;
    }
    public ArrayList<Job> getJobsForSlaveA() {
        return jobsForSlaveA;
    }
    public ArrayList<Job> getJobsForSlaveB() {
        return jobsForSlaveB;
    }

    // add load to slaves:
    public void addSlaveALoad(int add) {
        this.SlaveALoad = SlaveALoad + add;
    }
    public void addSlaveBLoad(int add) {
        this.SlaveBLoad = SlaveBLoad + add;
    }
    public int getSlaveALoad() {
        return SlaveALoad;
    }
    public int getSlaveBLoad() {
        return SlaveBLoad;
    }

    public ArrayList<Job> getDoneJobs() {return doneJobs;}
    public ArrayList<Job> getClient0DoneJobs() {
        return client0DoneJobs;
    }
    public ArrayList<Job> getClient1DoneJobs() {
        return client1DoneJobs;
    }
    public Object getClient0DoneJobs_LOCK() {
        return client0DoneJobs_LOCK;
    }
    public Object getClient1DoneJobs_LOCK() {
        return client1DoneJobs_LOCK;
    }
    public Object getJobsToComplete_LOCK() {return jobsToComplete_LOCK;}
    public Object getJobsForSlaveA_LOCK() {return jobsForSlaveA_LOCK;}
    public Object getJobsForSlaveB_LOCK() {return jobsForSlaveB_LOCK;}
    public Object getSlaveALoad_LOCK() {return slaveALoad_LOCK;}
    public Object getSlaveBLoad_LOCK() {return slaveBLoad_LOCK;}
    public Object getDoneJobs_LOCK() {return doneJobs_LOCK;}
}
