//This is what Master.java was doing, but now with threads
//but not 100% finished

package yg;

import java.io.IOException;
import java.lang.reflect.Array;
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
            ArrayList<Thread> clientThreads = new ArrayList<>();
            ArrayList<Thread> readerThreads = new ArrayList<>();
            ArrayList<Thread> writerThreads = new ArrayList<>();

            ArrayList<Thread> slaveThreads = new ArrayList<>();

            // FOR THE CLIENT -----------------------------------------------------------------------------
            for (int i = 0; i < CLIENT_THREADS; i++)
                clientThreads.add(new Thread(new ServerThreadC(serverSocketC, i)));

            for (Thread t : clientThreads)
                t.start();

            for (Thread t : clientThreads)
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

            // FOR THE SLAVE -----------------------------------------------------------------------------
            for (int i = 0; i < SLAVE_THREADS; i++)
                slaveThreads.add(new Thread(new ServerThreadS(serverSocketS, i)));

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
