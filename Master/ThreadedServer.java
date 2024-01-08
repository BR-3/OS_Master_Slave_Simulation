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

            // these are our reader and writer threads
            ArrayList<Thread> listenFromClientThreads = new ArrayList<>();
            ArrayList<Thread> writeToClientThreads = new ArrayList<>();
            ArrayList<Thread> writeToSlaveThreads = new ArrayList<>();
            ArrayList<Thread> listenFromSlaveThreads = new ArrayList<>();

            // shared memory: array list of jobs to complete:
            // sent to decider thread. Writer threads from master to slave will also need access to these
            ArrayList<Job> jobsToComplete = new ArrayList<>();
            // these two arraylists are sent to the writers for the slaves
            ArrayList<Job> jobsForSlaveA = new ArrayList<>();
            ArrayList<Job> jobsForSlaveB = new ArrayList<>();
            // this is sent to the listener for the slaves
            ArrayList<Job> doneJobs = new ArrayList<>();

            // these will be the locks that we can create synchronized blocks
            Object jobsToComplete_Lock = new Object();
            Object jobsForSlaveA_Lock = new Object();
            Object jobsForSlaveB_Lock = new Object();
            Object doneJobs_Lock = new Object();



            // FOR THE CLIENT LISTENERS-----------------------------------------------------------------------------
            for (int i = 0; i < CLIENT_THREADS; i++)
                listenFromClientThreads.add(new Thread(new ServerThreadClientListener(serverSocketC, i, jobsToComplete, jobsToComplete_Lock)));

            for (Thread t : listenFromClientThreads)
                t.start();

            for (Thread t : listenFromClientThreads)
            {
                try
                {
                    t.join();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            // FOR THE CLIENT WRITERS-----------------------------------------------
            for(int i = 0;i< CLIENT_THREADS; i++)
                writeToClientThreads.add(new Thread(new ServerThreadClientWriter(serverSocketC, i)));

            for (Thread t : writeToClientThreads)
                t.start();

            for(Thread t : writeToClientThreads)
            {
                try
                {
                    t.join();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            // FOR DECIDING WHICH SLAVE TO SEND TO- DECIDER THREAD---------------------------------------
            Thread deciderThread = new Thread(new ServerThreadDecider(jobsToComplete, jobsForSlaveA, jobsForSlaveB, jobsForSlaveA_Lock, jobsForSlaveB_Lock));
            deciderThread.start();

            try
            {
                deciderThread.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            // FOR THE SLAVE WRITERS-----------------------------------------------------------------------------
            for (int i = 0; i < SLAVE_THREADS; i++)
                writeToSlaveThreads.add(new Thread(new ServerThreadSlaveWriter(serverSocketS, i, jobsForSlaveA, jobsForSlaveB, jobsForSlaveA_Lock, jobsForSlaveB_Lock)));

            for (Thread t : writeToSlaveThreads)
                t.start();

            for (Thread t : writeToSlaveThreads)
            {
                try
                {
                    t.join();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            // FOR THE SLAVE LISTENERS-------------------------------------
            for (int i = 0;i<SLAVE_THREADS;i++)
                listenFromSlaveThreads.add(new Thread(new ServerThreadSlaveListener(serverSocketS, i, portNumberS, doneJobs, doneJobs_Lock)));

            for (Thread t : listenFromSlaveThreads)
                t.start();

            for (Thread t : listenFromSlaveThreads)
            {
                try
                {
                    t.join();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("Exception caught while trying to listen on port " + args[0] +
                    " or listening for a connection");
            System.out.println(e.getMessage());
        }


    }
}
