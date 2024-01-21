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
        //args = new String[] {"30121", "30122", "30123"};

        if (args.length != 3)
        {
            System.out.println("Usage: java Server <port number>");
            System.exit(1);
        }

        int portNumberC = Integer.parseInt(args[0]); // for client connection
        int portNumberSA = Integer.parseInt(args[1]); // for slave A connections
        int portNumberSB = Integer.parseInt(args[2]); // for slave b connections

        try (
                ServerSocket serverSocketC = new ServerSocket(portNumberC);
                ServerSocket serverSocketSA = new ServerSocket(portNumberSA);
                ServerSocket serverSocketSB = new ServerSocket(portNumberSB);
        )
        {
            // socket streams- client:
            Socket clientSocketC = serverSocketC.accept();
            System.out.println("Client is connected to Master");
            // send to client listener:
            ObjectInputStream objectInC = new ObjectInputStream(new BufferedInputStream(clientSocketC.getInputStream()));

            System.out.println("Before serverSocketSA.accept() called...");

            // socket streams- slave A:
            Socket clientSocketSA = serverSocketSA.accept();
            System.out.println("Slave A is connected to Master.");
            // send to slaveAWriter:
            ObjectOutputStream objectOutSA = new ObjectOutputStream(new BufferedOutputStream(clientSocketSA.getOutputStream()));
            System.out.println("slaveA output connected");
            // send to slaveAListener:
            ObjectInputStream objectInSA = new ObjectInputStream(new BufferedInputStream(clientSocketSA.getInputStream()));
            System.out.println("slaveA input created");
            // socket streams - slave B


            //Array for all threads:
            ArrayList<Thread> allThreads = new ArrayList<>();

            //SHARED MEMORY---------------------------------------------------------------------------------------------
            ServerSharedMemory sharedMemory = new ServerSharedMemory();

            // FOR THE CLIENT LISTENER-----------------------------------------------------------------------------
            allThreads.add(new Thread(new ServerThreadClientListener(clientSocketC, 0, sharedMemory)));
System.out.println("client listener created");
            // FOR THE CLIENT WRITER-----------------------------------------------
            allThreads.add(new Thread(new ServerThreadClient0Writer(clientSocketC, 0, sharedMemory)));
System.out.println("client writer created");
            // FOR DECIDING WHICH SLAVE TO SEND TO- DECIDER THREAD---------------------------------------
            Thread deciderThread = new Thread(new ServerThreadDecider(sharedMemory));
            allThreads.add(deciderThread);
System.out.println("decider thread created");
            // FOR THE SLAVE WRITERS-----------------------------------------------------------------------------
            allThreads.add(new Thread(new ServerThreadSlaveAWriter(objectOutSA, sharedMemory)));
//            allThreads.add(new Thread(new ServerThreadSlaveBWriter(serverSocketSB, sharedMemory)));
System.out.println("slave A writer created");
            // FOR THE SLAVE LISTENERS-------------------------------------
            allThreads.add(new Thread(new ServerThreadSlaveAListener(objectInSA, sharedMemory)));
//            allThreads.add(new Thread(new ServerThreadSlaveBListener(serverSocketSB, sharedMemory)));
System.out.println("slaveA listener created");
            // FOR  DECIDING WHICH CLIENT TO SEND DONE JOBS TO- DONE_DECIDER THREAD-------------------------------------
            allThreads.add(new Thread(new ServerThreadDoneDecider(sharedMemory)));
System.out.println("donedecider thread created");
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
