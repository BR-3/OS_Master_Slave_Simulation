package yg;

import java.io.*;
import java.net.*;

public class Client {

    public static void main(String args[]) {
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
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter requestWriter = //stream to write text requests to server
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader infoReader = //stream to read response from server
                        new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream()));
        ) {
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
    /*private Type type;
    private int ID;
    public Client(Type type, int ID){
        this.type = type;
        this.ID = ID;
    }*/
    }

    public static void getNewJob() {

    }
}
