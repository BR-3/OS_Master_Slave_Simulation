package yg.Slave;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SlaveAServerWriter implements Runnable{
    private Socket clientSocket;
    private Object doneJobs_Lock;

    boolean runThread = true;

    public SlaveAServerWriter(Socket clientSocket, Object doneJobs_Lock) {
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
                while(!Slave.doneJobs.isEmpty())
                {
                    synchronized (doneJobs_Lock)
                    {
                        objectOut.writeObject(Slave.doneJobs.remove(0));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
