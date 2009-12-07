package globals;

import java.math.BigInteger;
import java.util.Random;

public class GlobalConsts {

	public static final int PARTIES_AMOUNT = 40;
	public static final int VOTERS_AMOUNT = 400;
	public static final int BITS_AMOUNT = 1024;
	public static final int CERTAINTY = 100;
	public static final String PARTIES_MANAGER_HOSTNAME = "localhost";
	public static final int PARTIES_MANAGER_PORT = 7777;
	public static final int CONNECTION_TIMEOUT = 1000; // ms = 1sec
	public static final BigInteger q = getRandomLargePrimeQ();
	public static final BigInteger p = getP(q);
	public static final BigInteger G = null; // TODO - fix initialization

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

	public static BigInteger getQ(BigInteger p) {
		// TODO - q = (p-1)/2;
		return null;
	}

	public static BigInteger getP(BigInteger p) {
		// TODO - p = 2*q + 1;
		return null;
	}

	public static BigInteger getQ() {
		return getRandomLargePrimeQ();
	}

	public static BigInteger getP() {
		return getRandomLargePrimeP();
	}

	public static boolean isPrime(BigInteger n) {
		// TODO - implement
		return false;
	}

}
