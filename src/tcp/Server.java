package tcp;

//The server code Server.java:

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import consts.GlobalConsts;

public class Server {

	private class Address {
		InetAddress address;
		int port;

		private Address(InetAddress address, int port) {
			this.address = address;
			this.port = port;
		}

		public InetAddress getAddress() {
			return address;
		}

		public int getPort() {
			return port;
		}
	}

	public class Message {
		Address address;
		Object message;

		private Message(InetAddress address, int port, Object message) {
			this.address = new Address(address, port);
			this.message = message;
		}

		public InetAddress getAddress() {
			return address.getAddress();
		}

		public int getPort() {
			return address.getPort();
		}

		public Object getMessage() {
			return message;
		}
	}

	// the socket used by the server
	private ServerSocket serverSocket;
	public ServerThread serverThread;
	private List<Message> receivedObjects;
	private Vector<Address> knownClients;

	// server constructor
	public Server(int port) {
		// create the server's socket
		try {
			receivedObjects = new ArrayList<Message>();
			knownClients = new Vector<Address>(GlobalConsts.PARTIES_AMOUNT);
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(1000);
			System.out.println("Server: Server waiting for client on port " + serverSocket.getLocalPort());
			serverThread = new ServerThread();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void setPartyNum(InetAddress address, int port, int num) {
		knownClients.add(num, new Address(address, port));
	}

	/**
	 * @return the oldest object received of this type, or null if no such object
	 */
	public Message getReceivedObject() {
		boolean shouldWait = false;
		synchronized(receivedObjects) {
			if (receivedObjects.isEmpty()) {
				shouldWait = true;
			}
		}
		if (shouldWait) {
			try {
				wait(2000);
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
		synchronized(receivedObjects) {
			if (receivedObjects.isEmpty()) {
				return null;
			} else {
				return receivedObjects.get(0);
			}
		}
	}

	public void close() {
		serverThread.finish();
	}

	private class ServerThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream Sinput;
		ObjectOutputStream Soutput;
		boolean done;

		ServerThread() {
			start();
		}

		public void finish() {
			done = true;
		}

		public void run() {
			while (!done) {
				try {
					socket = serverSocket.accept(); // accept connection
					System.out.println("Server: New client asked for a connection");
					/* Creating both Data Stream */
					System.out.println("Server: Thread trying to create Object Input/Output Streams");
				} catch (SocketTimeoutException e) {
					continue;
				} catch (IOException e) {
					System.out.println(e);
					continue;
				}
				try {
					// create output first
					Soutput = new ObjectOutputStream(socket.getOutputStream());
					Soutput.flush();
					Sinput = new ObjectInputStream(socket.getInputStream());
				} catch (IOException e) {
					System.out.println("Server: Exception creating new Input/output Streams: " + e);
					return;
				}
				System.out.println("Server: Thread waiting for a String from the Client");
				// read a String (which is an object)
				try {
					Object receivedObject = Sinput.readObject();
					System.out.println("Server: received object's classname: " + receivedObject.getClass().getName());
					Message message = new Message(socket.getInetAddress(), socket.getPort(), receivedObject);
					synchronized(receivedObjects) {
						receivedObjects.add(message);
						receivedObjects.notifyAll();
					}
					String str = (String) receivedObject;
					str = str.toUpperCase();
					Soutput.writeObject(str);
					Soutput.flush();
				} catch (IOException e) {
					System.out.println("Server: Exception reading/writing Streams: " + e);
					return;
				}
				// will surely not happen with a String
				catch (ClassNotFoundException o) {
				} finally {
					try {
						Soutput.close();
						Sinput.close();
					} catch (Exception e) {
					}
				}
			}
		}
	}
}
