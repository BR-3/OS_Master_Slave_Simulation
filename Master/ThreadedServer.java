//This is what Master.java was doing, but now with threads
//but not 100% finished

package yg.Master;

import yg.Job;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ThreadedServer {
    public static void main(String[] args) {
        // hardcoded port for now...
        args = new String[] {"30121", "30122"};

        if (args.length != 2)
        {
            System.out.println("Usage: java Server <port number>");
            System.exit(1);
        }

        int portNumberC = Integer.parseInt(args[0]); // for client connection
        int portNumberS = Integer.parseInt(args[1]); // for slave connections
        final int CLIENT_THREADS = 2;
        final int SLAVE_THREADS = 2;

        try (
                ServerSocket serverSocketC = new ServerSocket(portNumberC);
                ServerSocket serverSocketS = new ServerSocket(portNumberS);
        )
        {
            //Array for all threads:
            ArrayList<Thread> allThreads = new ArrayList<>();
            // these are arrays for our reader and writer threads
            ArrayList<Thread> listenFromClientThreads = new ArrayList<>();
            ArrayList<Thread> writeToClientThreads = new ArrayList<>();
            ArrayList<Thread> writeToSlaveThreads = new ArrayList<>();
            ArrayList<Thread> listenFromSlaveThreads = new ArrayList<>();

            //SHARED MEMORY
            // array list of jobs to complete:
            // sent to decider thread. Writer threads from master to slave will also need access to these
            ArrayList<Job> jobsToComplete = new ArrayList<>();

            // these two arraylists are sent to the writers for the slaves
            ArrayList<Job> jobsForSlaveA = new ArrayList<>();
            ArrayList<Job> jobsForSlaveB = new ArrayList<>();
            // current loads of slave A and slave B
            int slaveALoad = 0;
            int slaveBLoad = 0;
            // this is sent to the listener for the slaves
            ArrayList<Job> doneJobs = new ArrayList<>();

            // new attampt with Loads class:
            SlaveALoad slaveALoad_new = new SlaveALoad(0);
            SlaveBLoad slaveBLoad_new = new SlaveBLoad(0);
            jobsToCompleteClass jobsToComplete2 = new jobsToCompleteClass(jobsToComplete);

            // these will be the locks that we can create synchronized blocks
            Object jobsToComplete_LOCK = new Object();
            Object jobsForSlaveA_LOCK = new Object();
            Object jobsForSlaveB_LOCK = new Object();
            Object doneJobs_LOCK = new Object();
            Object slaveALoad_LOCK = new Object();
            Object slaveBLoad_LOCK = new Object();



            // FOR THE CLIENT LISTENERS-----------------------------------------------------------------------------
            for (int i = 0; i < CLIENT_THREADS; i++)
                allThreads.add(new Thread(new ServerThreadClientListener(serverSocketC, i, jobsToComplete2, jobsToComplete_LOCK)));

            // FOR THE CLIENT WRITERS-----------------------------------------------
            for(int i = 0;i< CLIENT_THREADS; i++)
                allThreads.add(new Thread(new ServerThreadClientWriter(serverSocketC, i)));

            // FOR DECIDING WHICH SLAVE TO SEND TO- DECIDER THREAD---------------------------------------
            Thread deciderThread = new Thread(new ServerThreadDecider( jobsToComplete2,
                    jobsToComplete2.getJobsToCompleteArray(), jobsForSlaveA, jobsForSlaveB, slaveALoad_new, slaveBLoad_new,
                    jobsToComplete_LOCK, jobsForSlaveA_LOCK, jobsForSlaveB_LOCK, slaveALoad_LOCK, slaveBLoad_LOCK));
            allThreads.add(deciderThread);

            // FOR THE SLAVE WRITERS-----------------------------------------------------------------------------
            for (int i = 0; i < SLAVE_THREADS; i++)
                allThreads.add(new Thread(new ServerThreadSlaveWriter(serverSocketS, i, jobsForSlaveA, jobsForSlaveB, jobsForSlaveA_LOCK, jobsForSlaveB_LOCK)));

            // FOR THE SLAVE LISTENERS-------------------------------------
            for (int i = 0;i<SLAVE_THREADS;i++)
                allThreads.add(new Thread(new ServerThreadSlaveListener(serverSocketS, i, portNumberS, doneJobs, doneJobs_LOCK)));

            // start all threads
            for (Thread t : allThreads)
            {
                t.start();
            }

            // Wait for all threads to finish
            for (Thread t : allThreads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("All threads have finished.");

        }
        catch (IOException e)
        {
            System.out.println("Exception caught while trying to listen on port " + args[0] +
                    " or listening for a connection");
            System.out.println(e.getMessage());
        }


    }
}
