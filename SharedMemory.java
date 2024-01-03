package yg;

import java.util.ArrayList;

public class SharedMemory {
    // we need to add more to shared memory and create locks for it
    int SlaveALoad;
    int SlaveBLoad;
    Job currJob;
    ArrayList<Job> readyJobs = new ArrayList<>();

    public SharedMemory(Job currJob) {
        this.currJob = currJob;
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
