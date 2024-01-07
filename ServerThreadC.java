package yg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThreadC implements Runnable{

    // a reference to the server socket is passed in, all threads share it
    private ServerSocket serverSocket = null;
    int id;

    public ServerThreadC(ServerSocket serverSocket, int id) {
        this.serverSocket = serverSocket;
        this.id = id;
    }

    @Override
    public void run() {
        // This thread accepts its own client socket from the shared server socket
        try (Socket clientSocket = serverSocket.accept();
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

             )
        {
            String jobString;
            while ((jobString = in.readLine()) != null)
            {
                System.out.println(jobString + " received by client #" + id);
                char type = jobString.charAt(0);

                int slaveALoad = SlaveA.getCurrentLoad();
                int slaveBLoad = SlaveA.getCurrentLoad(); //switch to slaveB later after edit slaveB

                if (type == 'a')
                {
                    if (slaveALoad + 2 <= slaveBLoad + 10) // not sure if calculations are 100% accurate...
                    {
                        //send to slave A
                        System.out.println("Sending to slave A: " + jobString);
//                        sendCodeToSlaveA(readyJobs, masterSlaveObjectOutput, inSlave);
                    }
                    else
                    {
                        //send to Slave B
                        System.out.println("Sending to slave B: " + jobString);
//                        sendCodeToSlaveB(readyJobs, masterSlaveObjectOutput, inSlave);
                    }
                }
                else if (type == 'b')
                {
                    if (slaveBLoad + 2 <= slaveALoad + 10) // not sure if calculations are 100% accurate...
                    {
                        //add code for sending to Slave A
                        System.out.println("Sending to slave A: " + jobString);
//                        sendCodeToSlaveB(readyJobs, masterSlaveObjectOutput, inSlave);
                    }
                    else
                    {
                        //add code for sending to Slave B
                        System.out.println("Sending to slave B: " + jobString);
//                        sendCodeToSlaveA(readyJobs, masterSlaveObjectOutput, inSlave);
                    }
                }


            }

        }
        catch (IOException e)
        {
            System.out.println("Exception caught while trying to listen on port " +
                    serverSocket.getLocalPort() + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

}
