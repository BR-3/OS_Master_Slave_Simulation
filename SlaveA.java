package yg;

import java.io.*;
import java.net.*;
import java.util.*;

public class SlaveA {

    // a count for the current load:
           /*  when it gets a job,
             it will increase the load by 2 if it's optimal type
             or by 10 if it's the non-optimal job.
             (might need to add a lock on this for when master accesses this
             to determine which slave to send a job to)
             */
    static int currentLoad = 0;

    public static int getCurrentLoad() {
        return currentLoad;
    }

    public static void main(String[] args) {
        args = new String[]{"127.0.0.1", "30122"};

        if (args.length != 2) {
            System.err.println("Usage: java client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                //sockets for connections between client and master (server)
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter requestWriter = //stream to write text requests to server
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader infoReader = //stream to read response from server
                        new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream()));
                ObjectInputStream jobInputStream = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            // this is what it does better
            String optimalJob = String.valueOf('a');





            // this will hold done jobs
            ArrayList<Job> doneJobs = new ArrayList<Job>();

            Job currentJob;
            while(jobInputStream.readObject() != null) {
                currentJob = (Job) jobInputStream.readObject();
                if(currentJob.getType().equals(optimalJob)) {
                    System.out.println("Job is optimal, takes 2 seconds to complete.");
                    // increase load by 2 and sleep for 2 seconds
                    currentLoad += 2;
                    Thread.sleep(2000);
                    // when finish job, decrease load by 2
                    currentLoad -= 2;
                } else {
                    System.out.println("Job is not optimal, takes 10 seconds to complete.");
                    // increase load by 10 and sleep for 10 seconds
                    currentLoad += 10;
                    Thread.sleep(10000);
                    // when finish job, decrease load by 10
                    currentLoad -= 10;
                }
                doneJobs.add(currentJob);
                System.out.println("Completed job, type: " + currentJob.getType() + " ID: " + currentJob.getID());

                // *** don't think we need this bc the master just needs
                // to know the current load (added above) ***
                // this tells the master that it is now available
                requestWriter.println(true);

                //loop through the doneJobs array and send them back to the master:
                while(!doneJobs.isEmpty())
                {
                    //send 1st of array list back to the master to tell it that it's done
                    //hardcoded message for now-later need to add sockets:
                    Job curr = doneJobs.get(0);
                    System.out.println("Sending done job to master, type: " + curr.getType() + " ID: " + curr.getID());
                    doneJobs.remove(curr);
                }
            }



        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



}