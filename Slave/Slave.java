package yg.Slave;

import yg.Job;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Slave {
    static ArrayList<Job> doneAJobs = new ArrayList<Job>();
    static ArrayList<Job> doneBJobs = new ArrayList<Job>();
    static Object doneJobs_Lock = new Object();

    // getters and setters
    public static ArrayList<Job> getDoneAJobs() {
        return doneAJobs;
    }
    public static ArrayList<Job> getDoneBJobs() {
        return doneBJobs;
    }

    public static void main(String[] args)  {
      // args = new String[]{"127.0.0.1", "30122", "30123"};

        if (args.length != 3) {
            System.err.println("Usage: java client <host name> <port number> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumberSA = Integer.parseInt(args[1]);
        int portNumberSB = Integer.parseInt(args[2]);

        try (
                Socket slaveASocket = new Socket(hostName, portNumberSA);
                Socket slaveBSocket = new Socket(hostName, portNumberSB);
                                ) {

            ArrayList<Thread> slaveThreads = new ArrayList<>();

            slaveThreads.add(new Thread(new SlaveServerListener(slaveASocket, 'a', doneJobs_Lock)));
            slaveThreads.add(new Thread(new SlaveServerWriter(slaveASocket, doneJobs_Lock, 'A')));
            slaveThreads.add(new Thread(new SlaveServerListener(slaveBSocket, 'b', doneJobs_Lock)));
            slaveThreads.add(new Thread(new SlaveServerWriter(slaveBSocket, doneJobs_Lock, 'B')));

            for (Thread t: slaveThreads)
            {
                t.start();
            }

            for (Thread t: slaveThreads)
            {
                try
                {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}