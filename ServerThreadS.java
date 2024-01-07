
package yg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThreadS implements Runnable{

    // a reference to the server socket is passed in, all threads share it
    private ServerSocket serverSocket = null;
    int id;

    public ServerThreadS(ServerSocket serverSocket, int id) {
        this.serverSocket = serverSocket;
        this.id = id;
    }

    @Override
    public void run() {

    }

}
