package yg;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Client {

    public static void main(String[] args) {
        // this is going to be a temporary list to hold jobs

        // these args are hardcoded, but we can enter them if we want into the command line
        // possible option to have an arg to create two instances of this?
        args = new String[]{"127.0.0.1", "30121"};

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
        ) {
            Job job1 = new Job(Type.b,1);
            Job job2 = new Job(Type.a,2);
            Job job3 = new Job(Type.b,3);
            Job job4 = new Job(Type.a,4);
            Job job5 = new Job(Type.b,5);
            Job job6 = new Job(Type.a,6);
            Job job7 = new Job(Type.b,7);
            Job job8 = new Job(Type.a,8);
            Job job9 = new Job(Type.b,9);
            Job job10 = new Job(Type.b,10);
            ArrayList<Job> jobs= new ArrayList <> ();
            jobs.add(job1);
            jobs.add(job2);
            jobs.add(job3);
            jobs.add(job4);
            jobs.add(job5);
            jobs.add(job6);
            jobs.add(job7);
            jobs.add(job8);
            jobs.add(job9);
            jobs.add(job10);

            // this is going to be the code where we send things to the master

            String info = infoReader.readLine();
            // this will be reading from the socket
            while(info != null) {
                // this will be checking if master says that the job is finished
                // then it will send another one
                if(info == "done") {
                    getNewJob();
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }

    public static void getNewJob() {

    }
}