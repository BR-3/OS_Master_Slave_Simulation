package yg;

import java.io.*;
import java.net.*;

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
			System.out.println("The master is now connected to the client");

			//this is for recieving messages from client:
			String inputLine;
			while ((inputLine = inClient.readLine()) != null)
			{
				//print to console that we recived a message from client:
				System.out.println("\nReceived from client: " + inputLine);
				// this is a temporary Job
				Job newJob = null;
				if(inputLine.charAt(0) == 'a') {
					newJob.setType(Type.a);
				} else {
					newJob.setType(Type.b);
				}
				newJob.setID(Integer.parseInt(inputLine.replaceAll("[^0-9]", "")));

				// Calculations to decide which slave to send to:
				// Spins while both are busy
				while (!aIsOpen && !bIsOpen);
				if (newJob.getType == 'a'){
					if (aIsOpen()) {
						// Method to send a job to a slave
						sendCodeToSlaveA();
					} else {
						sendCodeToSlaveB();
					}
				} else {
					if (bIsOpen()) {
						sendCodeToSlaveB();
					} else {
						sendCodeToSlaveA();
					}
				}
			}

			//enter code here for sending job to slave

			//enter code here for reciving messages from slave that job is done

			//enter code here for sending message to client that job is done

			//^^all these steps will probably involve separate threads so that they can
			// all be happening at the same time with sending messages in and out
		}
		catch (IOException exc)
		{
			System.out.println("Exception caught when trying to listen on port "
					+ portNumber + " or listening for a connection");
			System.out.println(exc.getMessage());
		}
	}

   


}
