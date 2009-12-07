package tcp;

//The client code Client.java:

import java.net.*;
import java.io.*;

public class Client {

	ObjectInputStream Sinput; // to read the socket
	ObjectOutputStream Soutput; // to write on the socket
	Socket socket;
	int connectionNumber;

	// Constructor connection receiving a socket number
	public Client(String hostname, int port,int connectionNumber) {
		this.connectionNumber = connectionNumber;
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
		send(new Integer(connectionNumber)); // predefined handshake
	}

	public void send(Object object) {
		// send the string to the server
		System.out.println("Client: Client sending \"" + object + "\" to server");
		try {
			Soutput.writeObject(object);
			Soutput.flush();
		} catch (IOException e) {
			System.out.println("Client: Error writting to the socket: " + e);
			return;
		}
	}

	public boolean isConnected() {
		return (canSend() && canReceive());
	}

	public boolean canSend() {
		return !socket.isOutputShutdown();
	}

	public boolean canReceive() {
		return !socket.isInputShutdown();
	}

	public void close() {
		try {
			Soutput.close();
			Sinput.close();
			socket.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
