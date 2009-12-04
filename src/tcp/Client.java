package tcp;

//The client code Client.java:

import java.net.*;
import java.io.*;

public class Client {

	ObjectInputStream Sinput; // to read the socker
	ObjectOutputStream Soutput; // towrite on the socket
	Socket socket;

	// Constructor connection receiving a socket number
	public Client(String hostname, int port) {
		// we use "localhost" as host name, the server is on the same machine
		// but you can put the "real" server name or IP address
		try {
			socket = new Socket(hostname, port);
		} catch (Exception e) {
			System.out.println("Client: Error connectiong to server:" + e);
			return;
		}
		System.out.println("Client: Connection accepted " + socket.getInetAddress() + ":" + socket.getPort());

		/* Creating both Data Stream */
		try {
			Sinput = new ObjectInputStream(socket.getInputStream());
			Soutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Client: Exception creating new Input/output Streams: " + e);
			return;
		}
	}

	public void send(String object) {
		// now that I have my connection
		String test = "aBcDeFgHiJkLmNoPqRsTuVwXyZ";
		if (object != null) {
			test = object;
		}
		// send the string to the server
		System.out.println("Client: Client sending \"" + test + "\" to server");
		try {
			Soutput.writeObject(test);
			Soutput.flush();
		} catch (IOException e) {
			System.out.println("Client: Error writting to the socket: " + e);
			return;
		}
		// read back the answer from the server
		String response;
		try {
			response = (String) Sinput.readObject();
			System.out.println("Client: Read back from server: " + response);
		} catch (Exception e) {
			System.out.println("Client: Problem reading back from server: " + e);
		}

		try {
			Sinput.close();
			Soutput.close();
		} catch (Exception e) {
		}
	}

	public static void main(String[] arg) {
		new Client("localhost", 1500);
	}
}
