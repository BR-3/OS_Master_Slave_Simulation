//This is what Master.java was doing, but now with threads
//but not 100% finished

package yg.Master;

import yg.Job;
import yg.SharedMemory;

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

            //SHARED MEMORY---------------------------------------------------------------------------------------------
            SharedMemory sharedMemory = new SharedMemory();

            // FOR THE CLIENT LISTENERS-----------------------------------------------------------------------------
            for (int i = 0; i < CLIENT_THREADS; i++)
                allThreads.add(new Thread(new ServerThreadClientListener(serverSocketC, i, sharedMemory)));

            // FOR THE CLIENT WRITERS-----------------------------------------------
            for(int i = 0;i< CLIENT_THREADS; i++)
                allThreads.add(new Thread(new ServerThreadClientWriter(serverSocketC, i)));

            // FOR DECIDING WHICH SLAVE TO SEND TO- DECIDER THREAD---------------------------------------
            Thread deciderThread = new Thread(new ServerThreadDecider(sharedMemory));
            allThreads.add(deciderThread);

            // FOR THE SLAVE WRITERS-----------------------------------------------------------------------------
            for (int i = 0; i < SLAVE_THREADS; i++)
                allThreads.add(new Thread(new ServerThreadSlaveWriter(serverSocketS, i, sharedMemory)));

            // FOR THE SLAVE LISTENERS-------------------------------------
            for (int i = 0;i<SLAVE_THREADS;i++)
                allThreads.add(new Thread(new ServerThreadSlaveListener(serverSocketS, i, portNumberS, sharedMemory)));

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
