package global;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class Consts {

	public enum DebugOutput {
		STDOUT,
		STDERR,
		FILE
	}

	public static final boolean DEBUG_MODE = true;
	public static final String LOG_FILE = "Elections_Log.txt";
	private static BufferedWriter outputFile = null;

	public static final int PARTIES_AMOUNT = 40;
	public static final int VOTERS_AMOUNT = 400;
	public static final int BITS_AMOUNT = 1024;
	public static final int CERTAINTY = 100;
	public static final String PARTIES_MANAGER_HOSTNAME = "localhost";
	public static final int PARTIES_MANAGER_PORT = 7777;
	public static final int CONNECTION_TIMEOUT = 1000; // ms = 1sec
	public static final BigInteger p = null; // TODO - fix initialization
	public static final BigInteger q = getQ(p); // TODO - fix initialization
	public static final BigInteger G = null; // TODO - fix initialization

	public static void log(String message, DebugOutput logger) {
		logger = DebugOutput.FILE;
		switch (logger) {
			case STDOUT : {
				System.out.println(message);
				break;
			}
			case STDERR : {
				if (DEBUG_MODE) {
					System.err.println(message);
					break;
				}
			}
			case FILE : {
				try {
					if (outputFile == null) {
						outputFile = new BufferedWriter(new FileWriter(LOG_FILE));
					}
					outputFile.write(message + "\r\n");
					outputFile.flush();
				} catch (IOException e) {
					System.err.println(e);
				}
				break;
			}
		}
	}

	public static BigInteger getRandomLargePrimeQ() {
		BigInteger q;
		BigInteger p;
		do {
			q = new BigInteger(BITS_AMOUNT, CERTAINTY, new Random());
			p = getP(q);
		} while (!isPrime(p));
		return q;
	}

	public static BigInteger getRandomLargePrimeP() {
		return getP(getRandomLargePrimeQ());
	}

	private static BigInteger getQ(BigInteger p) {
		// TODO - q = (p-1)/2;
		return null;
	}

	private static BigInteger getP(BigInteger p) {
		// TODO - p = 2*q + 1;
		return null;
	}

	public static BigInteger getQ() {
		return q;
	}

	public static BigInteger getP() {
		return p;
	}

	public static boolean isPrime(BigInteger n) {
		// TODO - implement
		return false;
	}

}
