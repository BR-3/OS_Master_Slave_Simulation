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

        // everything the client does will be on a thread
        ClientThread clientThread1 = new ClientThread(hostName, portNumber);
        ClientThread clientThread2 = new ClientThread(hostName, portNumber);
        clientThread1.start();
        clientThread2.start();

    }

    public static void getNewJob() {

    }
}