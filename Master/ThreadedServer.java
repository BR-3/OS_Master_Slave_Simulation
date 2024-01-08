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
            //for now, but later split up into sepearte reader and writer threads
            ArrayList<Thread> listenFromClientThreads = new ArrayList<>();

            // shared memory: array list of jobs to complete:
            // sent to decider thread. Writer threads from master to slave will also need access to these
            ArrayList<Job> jobsToComplete = new ArrayList<>();
            ArrayList<Job> jobsForSlaveA = new ArrayList<>();
            ArrayList<Job> jobsForSlaveB = new ArrayList<>();

            ArrayList<Thread> slaveThreads = new ArrayList<>();

            // FOR THE CLIENT LISTENERS-----------------------------------------------------------------------------
            for (int i = 0; i < CLIENT_THREADS; i++)
                listenFromClientThreads.add(new Thread(new ServerThreadClientListener(serverSocketC, i, jobsToComplete)));

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

            // FOR DECIDING WHICH SLAVE TO SEND TO- DECIDER THREAD---------------------------------------
            Thread deciderThread = new Thread(new ServerThreadDecider(jobsToComplete, jobsForSlaveA, jobsForSlaveB));
            deciderThread.start();

            try
            {
                deciderThread.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            // FOR THE SLAVE -----------------------------------------------------------------------------
            for (int i = 0; i < SLAVE_THREADS; i++)
                slaveThreads.add(new Thread(new ServerThreadSlaveWriter(serverSocketS, i)));

            for (Thread t : slaveThreads)
                t.start();

            for (Thread t : slaveThreads)
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
