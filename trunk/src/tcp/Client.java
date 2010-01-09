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
			Consts.log("Client " + connectionNumber + ": Error connectiong to server:" + e, Consts.DebugOutput.STDERR);
			return;
		}
		Consts.log("Client " + connectionNumber + ": Connection accepted " + socket.getInetAddress() + ":" + socket.getPort(), Consts.DebugOutput.STDOUT);

		/* Creating both Data Stream */
		try {
			Sinput = new ObjectInputStream(socket.getInputStream());
			Soutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			Consts.log("Client " + connectionNumber + ": Exception creating new Input/output Streams: " + e, Consts.DebugOutput.STDERR);
			return;
		}
		send(new Integer(connectionNumber)); // predefined handshake
	}

	public boolean send(Object object) {
		// send the object to the server
		Consts.log("Client " + connectionNumber + ": Client sending \"" + object + "\" to server", Consts.DebugOutput.STDOUT);
		try {
			Soutput.writeObject(object);
			Soutput.flush();
			return true;
		} catch (IOException e) {
			Consts.log("Client " + connectionNumber + ": Error writting to the socket: " + e, Consts.DebugOutput.STDERR);
			return false;
		}
	}

	public Object receive() {
		// receive an object from the server
		Consts.log("Client " + connectionNumber + ": trying to receive an object from the server", Consts.DebugOutput.STDOUT);
		Object receivedObject = null;
		try {
			receivedObject = Sinput.readObject();
			Consts.log("Client " + connectionNumber + ": received an object from the server: " + receivedObject, Consts.DebugOutput.STDOUT);
		} catch (SocketTimeoutException e) {
			Consts.log("Client " + connectionNumber + ": timeout reached in receive function, returning null.", Consts.DebugOutput.STDERR);
			receivedObject = null;
		} catch (Exception e) {
			Consts.log("Client " + connectionNumber + ": exception occured in receive(timeout) function, returning null.", Consts.DebugOutput.STDERR);
			receivedObject = null;
		}
		return receivedObject;
	}

	public Object receive(int timeout_miliseconds) {
		// receive an object from the server with timeout
		try {
			socket.setSoTimeout(timeout_miliseconds);
		} catch (SocketException e) {
			Consts.log("Client " + connectionNumber + ": exception occured in receive() function, returning null.", Consts.DebugOutput.STDERR);
		}
		return receive();
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
