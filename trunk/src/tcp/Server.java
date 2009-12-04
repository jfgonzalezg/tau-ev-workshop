package tcp;

//The server code Server.java:

import java.io.*;
import java.net.*;

/**
 * This is to help people to write Client server application I tried to make it
 * as simple as possible... the client connect to the server the client send a
 * String to the server the server returns it in UPPERCASE thats all
 */
public class Server {

	// the socket used by the server
	private ServerSocket serverSocket;
	public ServerThread serverThread;

	// server constructor
	public Server(int port) {
		// create the server's socket
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(3000);
			System.out.println("Server: Server waiting for client on port " + serverSocket.getLocalPort());
			serverThread = new ServerThread();
		} catch (IOException e) {
			System.out.println(e);
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
					String str = (String) Sinput.readObject();
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
