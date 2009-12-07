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

	private static final boolean DEBUG_MODE = true;
	private static final String LOG_FILE = "Elections_Log.txt";
	private static BufferedWriter outputFile = null;

	public static final int PARTIES_AMOUNT = 40;
	public static final int VOTERS_AMOUNT = 400;
	public static final int BITS_AMOUNT = 1024;
	public static final int CERTAINTY = 100;
	public static final String PARTIES_MANAGER_HOSTNAME = "localhost";
	public static final int PARTIES_MANAGER_PORT = 7777;
	public static final int CONNECTION_TIMEOUT = 1000; // ms = 1sec
	public static final BigInteger TWO = BigInteger.valueOf(2);

	public static final BigInteger q = getRandomLargePrimeQ();
	public static final BigInteger p = getP(q);
	public static final BigIntegerMod G = calculateG();

	public static void log(String message, DebugOutput logger) {
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

	private static BigInteger getRandomLargePrimeQ() {
		BigInteger q = getRandomLargePrime();
		BigInteger p = getP(q);
		while (!isPrime(p)) {
			q = q.nextProbablePrime();
			p = getP(q);
		}
		return q;
	}

	private static BigInteger getP(BigInteger q) {
		// p = 2*q + 1;
		return q.multiply(TWO).add(BigInteger.ONE);
	}

	private static BigIntegerMod calculateG() {
		Random random = new Random();
		BigInteger lowerBound = TWO;
		BigInteger upperBound = p.subtract(TWO);
		BigInteger result;
		do {
			result = new BigInteger(p.bitLength(), random);
		} while ((result.compareTo(lowerBound) < 0) || (result.compareTo(upperBound) > 0));
		return new BigIntegerMod(result, p);
	}

	private static BigInteger getRandomLargePrime() {
		return new BigInteger(BITS_AMOUNT, CERTAINTY, new Random());
	}

	public static BigInteger getQ() {
		return q;
	}

	public static BigInteger getP() {
		return p;
	}

	public static BigIntegerMod getG() {
		return G;
	}

	public static boolean isPrime(BigInteger n) {
		return n.isProbablePrime(CERTAINTY);
	}

}
