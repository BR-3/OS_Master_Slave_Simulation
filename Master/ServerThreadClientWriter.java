package yg.Master;

import java.io.*;
import java.net.*;

/**
 * Master's WRITER threads to clients
 * takes the finished jobs from the finished jobs array
 * and writes it back to the client that sent it
 */

public class ServerThreadClientWriter implements Runnable {
    private ServerSocket serverSocket = null;
    int id;

    public ServerThreadClientWriter(ServerSocket serverSocket, int id) {
        this.serverSocket = serverSocket;
        this.id = id;
    }

    @Override
    public void run() {
        try (Socket clientSocket = serverSocket.accept();
             PrintWriter textOut = new PrintWriter(clientSocket.getOutputStream());
             BufferedReader textIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

             // object streams:
             ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
             ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());

        ) {


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
