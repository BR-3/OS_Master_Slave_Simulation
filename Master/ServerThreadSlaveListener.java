package yg.Master;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Will need to edit this, but this file could be used
 * for threads that will listen to incoming done jobs from slaves
 */
public class ServerThreadSlaveListener {
    int args;
    public ServerThreadSlaveListener(int args) {this.args = args;}
    public void run() {
        try (
                ServerSocket MasterSlaveSocket = new ServerSocket(args); // connects to clients
                Socket slaveSocket = MasterSlaveSocket.accept();
                ObjectOutputStream masterSlaveObjectOutput = new ObjectOutputStream ( slaveSocket.getOutputStream());
                ObjectInputStream masterObjectInput = new ObjectInputStream (slaveSocket.getInputStream());
                //to send messages out to client:
                PrintWriter outSlave = new PrintWriter(slaveSocket.getOutputStream(), true);
                //to read incoming messages from client:
                BufferedReader inSlave = new BufferedReader(new InputStreamReader(slaveSocket.getInputStream()));
        ) {


        }   catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
