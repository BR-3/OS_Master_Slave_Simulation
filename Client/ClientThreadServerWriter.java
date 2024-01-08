package yg.Client;

import java.net.Socket;

public class ClientThreadServerWriter implements Runnable{
    private Socket clientSocket;
    private int id;
    public ClientThreadServerWriter(Socket clientSocket, int id)
    {
        this.clientSocket = clientSocket;
        this.id = id;
    }

    public void run() {







    }
}
