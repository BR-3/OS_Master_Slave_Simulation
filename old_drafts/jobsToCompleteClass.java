package yg.old_drafts;

import yg.Job;

import java.util.ArrayList;

public class jobsToCompleteClass {
    private ArrayList<yg.Job> jobsToCompleteArray;

    public jobsToCompleteClass(ArrayList<Job> jobsToComplete) {
        this.jobsToCompleteArray = jobsToComplete;
    }

    public ArrayList<Job> getJobsToCompleteArray() {
        return jobsToCompleteArray;
    }

    public void setJobsToCompleteArray(ArrayList<Job> jobsToComplete) {
        synchronized (this)
        {
            this.jobsToCompleteArray.clear();
            this.jobsToCompleteArray.addAll(jobsToComplete);

        }
    }
}
