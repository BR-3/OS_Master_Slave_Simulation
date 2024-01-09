package yg.Client;

import yg.Job;

import java.util.ArrayList;

public class ClientSharedMemory {
    ArrayList<Job> jobList;
    Object jobList_LOCK;
    
    public ClientSharedMemory()
    {
        this.jobList = jobList;
    }

    public void addJobs(Job job) {jobList.add(job);  }
    public ArrayList<Job> getJobList() {return jobList;}
    public Object getJobList_LOCK() {return jobList_LOCK;}
}
