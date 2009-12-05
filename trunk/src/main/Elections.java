package main;

import java.io.IOException;

import tcp.Server;
import tcp.Client;

public class Elections {

	/**
	 * Runs another process according to the given command
	 * for example - command="java Server.jar"
	 * @param command - the command which runs the wanted process
	 * @return the created process, of null upon exception
	 */
	public static Process runProcess(String command) {
		try {
			return Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			// what you want
		}
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Server server = new Server(1500);
		Client client = new Client("localhost", 1500);
		client.send("igelka");
/*		try {
			Class [] classParm = null;
			Object [] objectParm = null;
			String name = "Client";
			Class cl = Class.forName(name);
			java.lang.reflect.Constructor co = cl.getConstructor(classParm);
			co.newInstance(objectParm);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		server.close();
	}

}
