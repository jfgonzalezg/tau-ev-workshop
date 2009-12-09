package main;

import global.Consts;

import java.math.BigInteger;

import tcp.Server;
import tcp.Client;

public class Elections {

	public static void main(String[] args) {
		System.out.println("Initializing global consts");
		System.out.println("This might take several minutes of 100% CPU usage");
		System.out.println("(~ up to 30 minutes, depending on how strong is your CPU and how lucky you are)");
		System.out.println("Generated P = " + Consts.getP());
		System.out.println("Generated Q = " + Consts.getQ());
		System.out.println("Generated G = " + Consts.getG());
		Server server = new Server(1500);
		Client client = new Client("localhost", 1500, 3);
		client.send("igelka");
		BigInteger bigint = new BigInteger("0");
		client.send(bigint);
		client.close();
		server.close();
	}

}
