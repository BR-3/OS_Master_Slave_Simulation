package yg.Client;

import java.net.Socket;

public class ClientThreadServerListener implements Runnable{
    private Socket clientSocket;
    private int id;

    public ClientThreadServerListener(Socket clientSocket, int id)
    {
        this.clientSocket = clientSocket;
        this.id = id;
    }

    public void run()
    {

    }
}
