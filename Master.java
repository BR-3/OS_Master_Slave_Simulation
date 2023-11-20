package yg;

import java.io.*;
import java.net.*;

public class Master {

	public static void main(String[] args) throws IOException {
		//args = new String[]{"30121"};
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
				//to send messages out to client:
				PrintWriter outClient = new PrintWriter(clientSocket.getOutputStream(), true);
				//to read incoming messages from client:
				BufferedReader inClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				)
		{

			//this is for recieving messages from client:
			String inputLine;
			while ((inputLine = inClient.readLine()) != null)
			{
				//print to console that we recived a message from client:
				System.out.println("\nReceived from client: " + inputLine);
			}

			//enter code here for doing calculations to decide which slave to send to:
			
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



	/*Client client;
	Slave slavea;
	Slave slaveb;*/

   

}
