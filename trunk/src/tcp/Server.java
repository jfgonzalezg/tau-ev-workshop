package tcp;

//The server code Server.java:

import global.Consts;

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
			serverSocket.setSoTimeout(3 * global.Consts.CONNECTION_TIMEOUT);
			// DEBUG Consts.log("Server: Server waiting for clients on port " + serverSocket.getLocalPort(), Consts.DebugOutput.STDOUT);
			serverThread = new ServerThread();
		} catch (IOException e) {
			// DEBUG Consts.log(e.toString(), Consts.DebugOutput.STDERR);
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
				// DEBUG Consts.log(e.toString(), Consts.DebugOutput.STDERR);
			}
		}
		return true;
	}

	public boolean send(int connectionNumber, Object toSend) {
		return send(knownClients.get(connectionNumber), toSend);
	}

	public void broadcast(Object toSend) {
		synchronized (connectionThreads) {
			Iterator<ConnectionHandlerThread> iter = connectionThreads.iterator();
			while (iter.hasNext()) {
				send(iter.next(), toSend);
			}
		}
	}

	private void closeConnectionThread(ConnectionHandlerThread thread) {
		if (thread == null) {
			return;
		}
		thread.closeConnection();
	}

	private void addConnection(ConnectionHandlerThread thread) {
		synchronized (connectionThreads) {
			connectionThreads.add(thread);
			closeConnectionThread(knownClients.put(thread.connectionNumber, thread));
		}
	}

	/**
	 * @return the oldest object received of this type, or null if no such object
	 */
	public Message getReceivedObject() {
		synchronized(receivedObjects) {
			if (receivedObjects.isEmpty()) {
				try {
					receivedObjects.wait(2 * global.Consts.CONNECTION_TIMEOUT);
				} catch (InterruptedException e) {
					// DEBUG Consts.log(e.toString(), Consts.DebugOutput.STDERR);
				}
			}
			if (receivedObjects.isEmpty()) {
				return null;
			} else {
				return receivedObjects.remove(0);
			}
		}
	}

	public int[] getConnectionNumbers() {
		synchronized (connectionThreads) {
			int[] result = new int[connectionThreads.size()];
			int i = 0;
			Iterator<ConnectionHandlerThread> iter = connectionThreads.iterator();
			while (iter.hasNext()) {
				result[i++] = iter.next().connectionNumber;
			}
			return result;
		}
	}

	public void close() {
		synchronized (connectionThreads) {
			serverThread.finish();
			Iterator<ConnectionHandlerThread> iter = connectionThreads.iterator();
			while (iter.hasNext()) {
				closeConnectionThread(iter.next());
			}
		}
	}

	private class ConnectionHandlerThread extends Thread {
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
			if (socket.isClosed()) {
				return;
			}
			synchronized(this) {
				try {
					socket.shutdownInput();
					socket.shutdownOutput();
					Soutput.close();
					Sinput.close();
					socket.close();
				} catch (Exception e) {
					// DEBUG Consts.log(e.toString(), Consts.DebugOutput.STDERR);
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
				socket.setSoTimeout(10*global.Consts.CONNECTION_TIMEOUT);
			} catch (Exception e) {
				// DEBUG Consts.log(e.toString(), Consts.DebugOutput.STDERR);
				// DEBUG Consts.log("Server: connection from " + socket.getInetAddress() + ":" + socket.getPort() + " error - failed to get connection number. exiting.", Consts.DebugOutput.STDERR);
				closeConnection();
				return;
			}
			// DEBUG Consts.log("Server: new connection id is " + connectionNumber, Consts.DebugOutput.STDOUT);
			addConnection(this);
			while (canReceive()) {
				try {
					Object receivedObject = Sinput.readObject();
					// DEBUG Consts.log("Server: connection " + connectionNumber + " received object's classname: " + receivedObject.getClass().getName(), Consts.DebugOutput.STDOUT);
					Message message = new Message(connectionNumber, socket.getInetAddress(), socket.getPort(), receivedObject);
					synchronized(receivedObjects) {
						receivedObjects.add(message);
						receivedObjects.notifyAll();
					}
				} catch (EOFException e) {
					try {
						try {
							sleep(3 * socket.getSoTimeout());
						} catch (SocketException e1) {
							// DEBUG Consts.log(e1.toString(), Consts.DebugOutput.STDERR);
						}
					} catch (InterruptedException e1) {
						// DEBUG Consts.log(e1.toString(), Consts.DebugOutput.STDERR);
					}
					// DEBUG Consts.log("Server: Retrying to read object from client " + connectionNumber, Consts.DebugOutput.STDERR);
					continue;
				} catch (SocketTimeoutException e) {
					// DEBUG Consts.log("Server: connection " + connectionNumber + "'s socket has reached timeout. retrying.", Consts.DebugOutput.STDERR);
					continue;
				} catch (IOException e) {
					// DEBUG Consts.log("Server: Exception reading/writing Streams: " + e, Consts.DebugOutput.STDERR);
					// DEBUG Consts.log("Server: connection " + connectionNumber + " is existing upon exception", Consts.DebugOutput.STDERR);
					break;
				} catch (ClassNotFoundException e) {
					// DEBUG Consts.log(e.toString(), Consts.DebugOutput.STDERR);
					// DEBUG Consts.log("Server: connection " + connectionNumber + " is existing upon exception", Consts.DebugOutput.STDERR);
					break;
				}
			}
			// DEBUG Consts.log("Server: connection " + connectionNumber + " closing successfully", Consts.DebugOutput.STDOUT);
			closeConnection();
		}
	}

	private class ServerThread extends Thread {
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
					try {
						sleep(serverSocket.getSoTimeout());
					} catch (IOException e) {
						// DEBUG Consts.log(e.toString(), Consts.DebugOutput.STDERR);
					}
				} catch (InterruptedException e) {
					// DEBUG Consts.log(e.toString(), Consts.DebugOutput.STDERR);
				}
			}
			done = true;
		}

		public void run() {
			while (!done) {
				try {
					socket = serverSocket.accept();
					// DEBUG Consts.log("Server: New client asked for a connection", Consts.DebugOutput.STDOUT);
				} catch (SocketTimeoutException e) {
					continue;
				} catch (IOException e) {
					// DEBUG Consts.log(e.toString(), Consts.DebugOutput.STDERR);
					continue;
				}
				try {
					// create I/O data streams
					// DEBUG Consts.log("Server: Thread trying to create Object Input/Output Streams", Consts.DebugOutput.STDOUT);
					Soutput = new ObjectOutputStream(socket.getOutputStream());
					Soutput.flush();
					Sinput = new ObjectInputStream(socket.getInputStream());
				} catch (IOException e) {
					// DEBUG Consts.log("Server: Exception creating new Input/output Streams: " + e, Consts.DebugOutput.STDERR);
					return;
				}
				new ConnectionHandlerThread(socket, Sinput, Soutput);
			}
			try {
				serverSocket.close();
			} catch (IOException e) {
				// DEBUG Consts.log(e.toString(), Consts.DebugOutput.STDERR);
			}
			done = false;
		}
	}
}
