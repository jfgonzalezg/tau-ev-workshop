package tcp;

//The client code Client.java:

import global.Consts;

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
			Consts.log("Client: Error connectiong to server:" + e, Consts.DebugOutput.STDERR);
			return;
		}
		Consts.log("Client: Connection accepted " + socket.getInetAddress() + ":" + socket.getPort(), Consts.DebugOutput.STDOUT);

		/* Creating both Data Stream */
		try {
			Sinput = new ObjectInputStream(socket.getInputStream());
			Soutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			Consts.log("Client: Exception creating new Input/output Streams: " + e, Consts.DebugOutput.STDERR);
			return;
		}
		send(new Integer(connectionNumber)); // predefined handshake
	}

	public boolean send(Object object) {
		// send the string to the server
		Consts.log("Client: Client sending \"" + object + "\" to server", Consts.DebugOutput.STDOUT);
		try {
			Soutput.writeObject(object);
			Soutput.flush();
			return true;
		} catch (IOException e) {
			Consts.log("Client: Error writting to the socket: " + e, Consts.DebugOutput.STDERR);
			return false;
		}
	}

	public boolean isConnected() {
		if (socket != null)
			return (canSend() && canReceive());
		return false;
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
			Consts.log(e.toString(), Consts.DebugOutput.STDERR);
		}
	}

}
