//This is what Master.java was doing, but now with threads
//but not 100% finished

package yg.Master;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ThreadedServer {
    public static void main(String[] args) {
        // hardcoded port for now...
        //args = new String[] {"30120", "30121", "30122", "30123"};

        if (args.length != 4)
        {
            System.out.println("Usage: java Server <port number>");
            System.exit(1);
        }

        int portNumberC0 = Integer.parseInt(args[0]); // for client connection
        int portNumberC1 = Integer.parseInt(args[1]); // for second client connection
        int portNumberSA = Integer.parseInt(args[2]); // for slave A connections
        int portNumberSB = Integer.parseInt(args[3]); // for slave b connections

        try (
                ServerSocket serverSocketC0 = new ServerSocket(portNumberC0);
                ServerSocket serverSocketC1 = new ServerSocket(portNumberC1);
                ServerSocket serverSocketSA = new ServerSocket(portNumberSA);
                ServerSocket serverSocketSB = new ServerSocket(portNumberSB);
        )
        {
            // socket streams- client:
            Socket clientSocketC0 = serverSocketC0.accept();
            System.out.println("Client0 is connected to Master");
            Socket clientSocketC1 = serverSocketC1.accept();
            System.out.println("Client1 is connected to Master");

           // socket streams- slave A:
            Socket clientSocketSA = serverSocketSA.accept();
            System.out.println("Slave A is connected to Master.");
            // send to slaveAWriter:
            ObjectOutputStream objectOutSA = new ObjectOutputStream(new BufferedOutputStream(clientSocketSA.getOutputStream()));
            // send to slaveAListener:
            ObjectInputStream objectInSA = new ObjectInputStream(new BufferedInputStream(clientSocketSA.getInputStream()));

            // socket streams - slave B
            Socket clientSocketSB = serverSocketSB.accept();
            System.out.println("Slave B is connected to the Master.");
            // sending to SlaveBWriter:
            ObjectOutputStream objectOutSB = new ObjectOutputStream((new BufferedOutputStream(clientSocketSB.getOutputStream())));
            // send to slaveBListener:
            ObjectInputStream objectInSB = new ObjectInputStream(new BufferedInputStream((clientSocketSB.getInputStream())));


            //Array for all threads:
            ArrayList<Thread> allThreads = new ArrayList<>();

            //SHARED MEMORY---------------------------------------------------------------------------------------------
            ServerSharedMemory sharedMemory = new ServerSharedMemory();

            // FOR THE CLIENT LISTENER-----------------------------------------------------------------------------
            allThreads.add(new Thread(new ServerThreadClientListener(clientSocketC0, 0, sharedMemory)));
            allThreads.add(new Thread(new ServerThreadClientListener(clientSocketC1, 1, sharedMemory)));

            // FOR THE CLIENT WRITER-----------------------------------------------
            allThreads.add(new Thread(new ServerThreadClientWriter(clientSocketC0, 0, sharedMemory)));
            allThreads.add(new Thread(new ServerThreadClientWriter(clientSocketC1, 1, sharedMemory)));
            // FOR DECIDING WHICH SLAVE TO SEND TO- DECIDER THREAD---------------------------------------
            Thread deciderThread = new Thread(new ServerThreadDecider(sharedMemory));
            allThreads.add(deciderThread);

            // FOR THE SLAVE WRITERS-----------------------------------------------------------------------------
            allThreads.add(new Thread(new ServerThreadSlaveAWriter(objectOutSA, sharedMemory)));
            allThreads.add(new Thread(new ServerThreadSlaveBWriter(objectOutSB, sharedMemory)));

            // FOR THE SLAVE LISTENERS-------------------------------------
            allThreads.add(new Thread(new ServerThreadSlaveAListener(objectInSA, sharedMemory)));
            allThreads.add(new Thread(new ServerThreadSlaveBListener(objectInSB, sharedMemory)));

            // FOR  DECIDING WHICH CLIENT TO SEND DONE JOBS TO- DONE_DECIDER THREAD-------------------------------------
            allThreads.add(new Thread(new ServerThreadDoneDecider(sharedMemory)));

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
            e.printStackTrace();
            System.out.println(e.getMessage());
        }


    }
}
