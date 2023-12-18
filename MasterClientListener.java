package yg;

import java.io.*;
import java.net.*;

public class MasterClientListener extends Thread {
    int args;

    public MasterClientListener(int args) {
        this.args = args;
    }

    public void run() {
           try (
                ServerSocket masterClientSocket = new ServerSocket(args); // connects to clients
                Socket clientSocket = masterClientSocket.accept();
                ObjectOutputStream masterClientObjectOutput = new ObjectOutputStream ( clientSocket.getOutputStream());
                ObjectInputStream masterObjectInput = new ObjectInputStream ( clientSocket.getInputStream());
                //to send messages out to client:
                PrintWriter outClient = new PrintWriter(clientSocket.getOutputStream(), true);
                //to read incoming messages from client:
                BufferedReader inClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                   ) {


           }   catch (IOException e) {
               throw new RuntimeException(e);
           }

        }
    }
}
