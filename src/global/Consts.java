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
	public static final int THRESHOLD = PARTIES_AMOUNT*2/3;
	public static final String THRESHOLD_CENTER_HOSTNAME = "localhost";
	public static final int THRESHOLD_CENTER_PORT = 7777;
	public static final int CONNECTION_TIMEOUT = 1000; // ms = 1sec
	public static final BigInteger TWO = BigInteger.valueOf(2);
	public static BigIntegerMod publicKey = null; // TODO - initialization + should be final + consider refactoring the name of this member to something clearer to everybody

	public static final BigInteger q = getRandomLargePrimeQ();
	public static final BigInteger p = getP(q);
	public static final BigIntegerMod G = calculateG();
	
	//Mix Centers IPs and ports. First place in the array is Pret a vote data.
	public static final String[] MIX_CENTERS_IP = {	"127.0.0.1", //Pret a vote
													"127.0.0.1", //Mix Center 1
													"127.0.0.1", //Mix Center 2
													"127.0.0.1", //Mix Center 3
													"127.0.0.1", //Mix Center 4
													"127.0.0.1", //Mix Center 5
													"127.0.0.1", //Mix Center 6
													"127.0.0.1", //Mix Center 7
													"127.0.0.1", //Mix Center 8
													"127.0.0.1", //Mix Center 9
													"127.0.0.1"};//Mix Center 10
	
	public static final int[] MIX_CENTERS_PORT = {	7000, //Pret a vote
													7001, //Mix Center 1
													7002, //Mix Center 2
													7003, //Mix Center 3
													7004, //Mix Center 4
													7005, //Mix Center 5
													7006, //Mix Center 6
													7007, //Mix Center 7
													7008, //Mix Center 8
													7009, //Mix Center 9
													7010};//Mix Center 10
	
	

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
		return new BigInteger("102878210466625684448564152718275770164400015795599081323142484261174204395771542931556573005457548994300390542836390656590342719190546003475313704107511340660051255130886898195350488634781241593291629806029940967099127171111741121800016354471000128327690358673195988965015732141003244933469931582512668226603");
		/* TODO - bring back these lines instead of the line above
		BigInteger q = null;
		BigInteger p = null;
		do {
			System.out.print(".");
			q = new BigInteger(BITS_AMOUNT, CERTAINTY, new Random());
			p = getP(q);
		} while (!Utils.isPrime(p));
		System.out.println("");
		return q;
		*/
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
		return new BigIntegerMod(result.modPow(TWO, p), p);
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

}
