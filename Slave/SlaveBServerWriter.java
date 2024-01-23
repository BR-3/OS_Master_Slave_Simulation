package yg.Slave;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SlaveBServerWriter implements Runnable{
    private Socket clientSocket;
    private Object doneJobs_Lock;

    boolean runThread = true;

    public SlaveBServerWriter(Socket clientSocket, Object doneJobs_Lock) {
        this.clientSocket = clientSocket;
        this.doneJobs_Lock = doneJobs_Lock;
    }

    public void run()
    {
        try(
                ObjectOutputStream objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
        ) {
            while(runThread)
            {
                while(!SlaveA.doneJobs.isEmpty())
                {
                    synchronized (doneJobs_Lock)
                    {
                        objectOut.writeObject(SlaveA.doneJobs.remove(0));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

