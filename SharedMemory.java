package yg;

import java.util.ArrayList;

public class SharedMemory {
    // we need to add more to shared memory and create locks for it
    int SlaveALoad;
    int SlaveBLoad;
    ArrayList<Job> jobsToComplete = new ArrayList<>();
    ArrayList<Job> jobsForSlaveA = new ArrayList<>();
    ArrayList<Job> jobsForSlaveB = new ArrayList<>();

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
}
