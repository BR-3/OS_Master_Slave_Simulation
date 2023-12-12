package yg;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Master {

	public static void main(String[] args) throws IOException {
		args = new String[]{"30121"};
		if (args.length != 1)
		{
			System.out.println("Usage: java Master <port number>");
			System.exit(1);
		}

		//in Intellij, the arg 30121 was added in a configuration
		// for this file so that we can practice with that port number
		// coded into the configurations
		int portNumber = Integer.parseInt(args[0]);

		try (
				// this is the master to connect to client
				ServerSocket masterClientSocket = new ServerSocket(Integer.parseInt(args[0])); // connects to clients
				Socket clientSocket = masterClientSocket.accept();
				ObjectOutputStream masterClientObjectOutput = new ObjectOutputStream ( clientSocket.getOutputStream());
				ObjectInputStream masterObjectInput = new ObjectInputStream ( clientSocket.getInputStream());
				//to send messages out to client:
				PrintWriter outClient = new PrintWriter(clientSocket.getOutputStream(), true);
				//to read incoming messages from client:
				BufferedReader inClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				// master to connect to slave
				ServerSocket masterSlaveSocket = new ServerSocket(Integer.parseInt(args[0]));
				Socket slaveSocket = masterSlaveSocket.accept();
				ObjectOutputStream masterSlaveObjectOutput = new ObjectOutputStream(slaveSocket.getOutputStream());
				ObjectInputStream masterSlaveObjectInput = new ObjectInputStream(slaveSocket.getInputStream());
				PrintWriter outSlave = new PrintWriter(slaveSocket.getOutputStream(),true);
				BufferedReader inSlave = new BufferedReader(new InputStreamReader(slaveSocket.getInputStream()));
				)
		{
			System.out.println("The client is now connected to the master");

			// these will keep track of whether the slaves are available
			boolean aIsOpen =true;
			boolean bIsOpen= true;

			// Arraylist of jobs that are waiting to be assigned to slaves
			ArrayList<Job> readyJobs = new ArrayList<>();

			while ((masterObjectInput.readObject()) != null)
			{
				Job newJob = (Job) masterObjectInput.readObject();
				//print to console that we received a message from client:
				System.out.println("\nReceived from client: " + newJob.getID());
				// Add the new job to the arraylist of jobs
				readyJobs.add(newJob);

				// Calculations to decide which slave to send to:
				int slaveALoad = SlaveA.getCurrentLoad();
				int slaveBLoad = SlaveA.getCurrentLoad(); //switch to slaveB later after edit slaveB

				if (newJob.getType().equals('a'))
				{
					if (slaveALoad - slaveBLoad >= 10) // not sure if calculations are 100% accurate...
					{
						//add code for sending to Slave B
						sendCodeToSlaveB(readyJobs, masterSlaveObjectOutput, inSlave);
					}
					else
					{
						//add code for sending to Slave A
						sendCodeToSlaveA(readyJobs, masterSlaveObjectOutput, inSlave);
					}
				}
				else if (newJob.getType().equals('b'))
				{
					if (slaveBLoad - slaveALoad >= 10) // not sure if calculations are 100% accurate...
					{
						//add code for sending to Slave A
						sendCodeToSlaveA(readyJobs, masterSlaveObjectOutput, inSlave);
					}
					else
					{
						//add code for sending to Slave B
						sendCodeToSlaveB(readyJobs, masterSlaveObjectOutput, inSlave);
					}
				}




				// Spins while both are busy
				while (!aIsOpen || !bIsOpen);
				if (newJob.getType().equals('a')){
					if (aIsOpen) {
						// Method to send a job to a slave
						aIsOpen = sendCodeToSlaveA(readyJobs, masterSlaveObjectOutput, inSlave); // need to create another socket to connect to slaves, then send the object
					} else {
						bIsOpen = sendCodeToSlaveB(readyJobs, masterSlaveObjectOutput, inSlave);
					}
				} else {
					if (bIsOpen) {
						bIsOpen = sendCodeToSlaveB(readyJobs, masterSlaveObjectOutput, inSlave);
					} else {
						aIsOpen = sendCodeToSlaveA(readyJobs, masterSlaveObjectOutput, inSlave);
					}
				}
			}
		}
		catch (IOException exc)
		{
			System.out.println("Exception caught when trying to listen on port "
					+ portNumber + " or listening for a connection");
			System.out.println(exc.getMessage());
		} catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

	private static boolean sendCodeToSlaveB(ArrayList<Job> readyJobs,
											ObjectOutputStream output, BufferedReader inSlave) throws IOException {
		Job currJob = readyJobs.get(0);
		readyJobs.remove(0);
		//print to console to show each step
		System.out.println("Sending to slave B: " + currJob);
		// Logic to send it to the slave with sockets and everything
		output.writeObject(currJob);
		// When the slave is finished it comes back to this method
		// and returns true, meaning it's ready for a new job
		while(inSlave.readLine() == null);
		return true;
	}

	private static boolean sendCodeToSlaveA(ArrayList<Job> readyJobs,
											ObjectOutputStream output, BufferedReader inSlave) throws IOException {
		Job currJob = readyJobs.get(0);
		readyJobs.remove(0);
		//print to console to show each step
		System.out.println("Sending to slave A: " + currJob);
		// this sends the job to the slave
		output.writeObject(currJob);
		// When the slave is finished it comes back to this method
		// and returns true, meaning it's ready for a new job
		// it will spin until the slave input sends something back (which will be true)
		while(inSlave.readLine() == null);
		return true;
	}
}
