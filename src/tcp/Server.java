package tcp;

//The server code Server.java:

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Server {

	public class Message {
		private int connectionNumber;
		private InetAddress address;
		private int port;
		private Object message;

		private Message(int connectionNumber, InetAddress address, int port, Object message) {
			this.connectionNumber = connectionNumber;
			this.address = address;
			this.port = port;
			this.message = message;
		}

		public int getConnectionNumber() {
			return connectionNumber;
		}

		public InetAddress getAddress() {
			return address;
		}

		public int getPort() {
			return port;
		}

		public Object getMessage() {
			return message;
		}
	}

	// the socket used by the server
	private List<ConnectionHandlerThread> connectionThreads;
	private HashMap<Integer, ConnectionHandlerThread> knownClients;
	private List<Message> receivedObjects;
	private ServerSocket serverSocket;
	private ServerThread serverThread;

	// server constructor
	public Server(int port) {
		connectionThreads = new ArrayList<ConnectionHandlerThread>();
		knownClients = new HashMap<Integer, ConnectionHandlerThread>();
		receivedObjects = new ArrayList<Message>();
		// create the server's socket
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(globals.GlobalConsts.CONNECTION_TIMEOUT);
			System.out.println("Server: Server waiting for clients on port " + serverSocket.getLocalPort());
			serverThread = new ServerThread();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void finalize() throws Throwable {
		super.finalize();
		// we only close all threads and connections, the garbage collector will take care of the rest
		close();
	}

	private boolean send(ConnectionHandlerThread thread, Object toSend) {
		if (thread == null) {
			return false;
		}
		synchronized(thread) {
			if (!thread.canSend()) {
				return false;
			}
			try {
				thread.Soutput.writeObject(toSend);
				thread.Soutput.flush();
			} catch (IOException e) {
				System.out.println(e);
			}
		}
		return true;
	}

	public boolean send(int connectionNumber, Object toSend) {
		return send(knownClients.get(connectionNumber), toSend);
	}

	public void broadcast(Object toSend) {
		Iterator<ConnectionHandlerThread> iter = connectionThreads.iterator();
		while (iter.hasNext()) {
			send(iter.next(), toSend);
		}
	}

	@SuppressWarnings("deprecation")
	private void closeConnectionThread(ConnectionHandlerThread thread) {
		if (thread == null) {
			return;
		}
		thread.stop();
		thread.closeConnection();
	}

	private void addConnection(ConnectionHandlerThread thread) {
		connectionThreads.add(thread);
		closeConnectionThread(knownClients.put(thread.connectionNumber, thread));
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
				receivedObjects.wait(2 * globals.GlobalConsts.CONNECTION_TIMEOUT);
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

	public List<Integer> getConnectionNumbers() {
		List<Integer> result = new ArrayList<Integer>();
		Iterator<ConnectionHandlerThread> iter = connectionThreads.iterator();
		while (iter.hasNext()) {
			result.add(iter.next().connectionNumber);
		}
		return result;
	}

	public void close() {
		serverThread.finish();
		Iterator<ConnectionHandlerThread> iter = connectionThreads.iterator();
		while (iter.hasNext()) {
			closeConnectionThread(iter.next());
		}
	}

	private class ConnectionHandlerThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream Sinput;
		ObjectOutputStream Soutput;
		int connectionNumber = -1;

		ConnectionHandlerThread(Socket socket, ObjectInputStream Sinput, ObjectOutputStream Soutput) {
			this.socket = socket;
			this.Sinput = Sinput;
			this.Soutput = Soutput;
			start();
		}

		public void closeConnection() {
			synchronized(this) {
				try {
					Soutput.close();
					Sinput.close();
					socket.close();
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}

		public boolean canSend() {
			return !socket.isOutputShutdown();
		}

		public boolean canReceive() {
			return !socket.isInputShutdown();
		}

		public void run() {
			if (!canReceive()) {
				return;
			}
			try {
				// predefined handshake - get connection number
				connectionNumber = ((Integer) Sinput.readObject()).intValue();
				// now set the socket's timeout for infinite read
				socket.setSoTimeout(globals.GlobalConsts.CONNECTION_TIMEOUT);
			} catch (Exception e) {
				System.out.println(e);
				System.out.println("Server: connection from " + socket.getInetAddress() + ":" + socket.getPort() + " error - failed to get connection number. exiting.");
				closeConnection();
				return;
			}
			System.out.println("Server: new connection id is " + connectionNumber);
			while (true) {
				if (!canReceive()) {
					break;
				}
				try {
					System.out.println("Server: Retrying to read object from client " + connectionNumber);
					Object receivedObject = Sinput.readObject();
					System.out.println("Server: connection " + connectionNumber + " received object's classname: " + receivedObject.getClass().getName());
					Message message = new Message(connectionNumber, socket.getInetAddress(), socket.getPort(), receivedObject);
					synchronized(receivedObjects) {
						receivedObjects.add(message);
						receivedObjects.notifyAll();
					}
				} catch (EOFException e) {
					try {
						sleep(3 * globals.GlobalConsts.CONNECTION_TIMEOUT);
					} catch (InterruptedException e1) {
						System.out.println(e1);
					}
					continue;
				} catch (IOException e) {
					System.out.println("Server: Exception reading/writing Streams: " + e);
					System.out.println("Server: connection " + connectionNumber + " is existing upon exception");
					break;
				} catch (ClassNotFoundException e) {
					System.out.println(e);
					System.out.println("Server: connection " + connectionNumber + " is existing upon exception");
					break;
				}
			}
			System.out.println("Server: connection " + connectionNumber + " closing successfully");
			closeConnection();
		}
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
			while(done) {
				try {
					sleep(globals.GlobalConsts.CONNECTION_TIMEOUT);
				} catch (InterruptedException e) {
					System.out.println(e);
				}
			}
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
				addConnection(new ConnectionHandlerThread(socket, Sinput, Soutput));
			}
			try {
				serverSocket.close();
			} catch (IOException e) {
				System.out.println(e);
			}
			done = false;
		}
	}
}
