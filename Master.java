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
				ServerSocket masterSocket = new ServerSocket(Integer.parseInt(args[0]));
				Socket clientSocket = masterSocket.accept();
				ObjectOutputStream masterObjectOutput = new ObjectOutputStream ( clientSocket.getOutputStream());
				ObjectInputStream masterObjectInput = new ObjectInputStream ( clientSocket.getInputStream());
				//to send messages out to client:
				PrintWriter outClient = new PrintWriter(clientSocket.getOutputStream(), true);
				//to read incoming messages from client:
				BufferedReader inClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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

				// add some logic to read from slave if slave is busy

				// Calculations to decide which slave to send to:
				// Spins while both are busy
				while (!aIsOpen || !bIsOpen);
				if (newJob.getType().equals('a')){
					if (aIsOpen) {
						// Method to send a job to a slave
						aIsOpen = sendCodeToSlaveA(readyJobs); // need to create another socket to connect to slaves, then send the object
					} else {
						bIsOpen = sendCodeToSlaveB(readyJobs);
					}
				} else {
					if (bIsOpen) {
						bIsOpen = sendCodeToSlaveB(readyJobs);
					} else {
						aIsOpen = sendCodeToSlaveA(readyJobs);
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

	private static boolean sendCodeToSlaveB(ArrayList<Job> readyJobs) {
		Job currJob = readyJobs.get(0);
		readyJobs.remove(0);
		//print to console to show each step
		System.out.println("Sending to slave B: " + currJob);
		// Logic to send it to the slave with sockets and everything

		// When the slave is finished it comes back to this method
		// and returns true, meaning it's ready for a new job
		return true;
	}

	private static boolean sendCodeToSlaveA(ArrayList<Job> readyJobs) {
		Job currJob = readyJobs.get(0);
		readyJobs.remove(0);
		//print to console to show each step
		System.out.println("Sending to slave A: " + currJob);
		// Logic to send it to the slave with sockets and everything

		// When the slave is finished it comes back to this method
		// and returns true, meaning it's ready for a new job
		return true;
	}


}
