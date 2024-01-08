
package yg.Master;

import java.net.ServerSocket;

/**
 * Master's WRITER threads to slaves
 * takes the jobs from sendToSlaveA array and sendToSlaveB array and
 * actually writes it over on the slave socket
 */
public class ServerThreadSlaveWriter implements Runnable{

    // a reference to the server socket is passed in, all threads share it
    private ServerSocket serverSocket = null;
    int id;

    public ServerThreadSlaveWriter(ServerSocket serverSocket, int id) {
        this.serverSocket = serverSocket;
        this.id = id;
    }

    @Override
    public void run() {

    }

}
