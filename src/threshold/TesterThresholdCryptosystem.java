package threshold;

import java.math.BigInteger;

import global.BigIntegerMod;
import global.Consts;
import threshold.center.ThresholdCryptosystem;
import threshold.parties.Party;

public class TesterThresholdCryptosystem {

	public static void main(String[] args) {
		BigInteger p = new BigInteger("23");
		//BigInteger q = p.subtract(BigInteger.ONE).divide(Consts.TWO);
		BigIntegerMod g = (new BigIntegerMod(p)).pow(Consts.TWO);
		System.out.println("g = " + g);
		int partiesAmount = 2;
		int threshold = 1;
		ThresholdCryptosystem thresholdCryptosystem = new ThresholdCryptosystem(partiesAmount, threshold, p, g, Consts.THRESHOLD_CENTER_PORT);
		Party parties[] = new Party[partiesAmount];
		for (int i=0; i<partiesAmount; ++i) {
			parties[i] = new Party(i, "localhost", Consts.THRESHOLD_CENTER_PORT);
			try {
				// wait between initialization of parties, to let the server do each handshake without errors
				java.lang.Thread.sleep(2 * Consts.CONNECTION_TIMEOUT);
			} catch (InterruptedException e) {
				System.err.println(e);
			}
		}
		
		System.out.println("waiting the key-exchange protocol to finish");
		thresholdCryptosystem.wait4KeyExchange();
		for (int i=0; i<partiesAmount; ++i) {
			parties[i].wait4KeyExchange();
		}
		
		BigIntegerMod polynom[];
		
		// printing private polynoms
		System.out.println("**********************************************");
		System.out.println("Clients' private polynoms");
		for (int i=0; i<partiesAmount; ++i) {
			polynom = parties[i].getPrivatePolynom();
			System.out.print("party "+i+": ");
			for (int j=0; j<threshold; ++j) {
				System.out.print(polynom[j]); //TODO print only values
			}
			System.out.println("");
		}
		
		// printing public polynoms
		System.out.println("Clients' public polynoms");
		for (int i=0; i<partiesAmount; ++i) {
			polynom = parties[i].getPrivatePolynom();
			System.out.print("party "+i+" public polynom: ");
			for (int j=0; j<threshold; ++j) {
				System.out.print(g.pow(polynom[j])); //TODO print only values
			}
			System.out.println("");
		}
		
		// printing mutual public polynom
		polynom = thresholdCryptosystem.getMutualPolynom();
		System.out.print("Mutual public polynom: ");
		for (int j=0; j<threshold; ++j) {
			System.out.print(polynom[j]); //TODO print only values
		}
		System.out.println("");
		
		// printing the private and public mutual keys
		System.out.print("private mutual keys:");
		for (int i=0; i<partiesAmount; ++i) {
			System.out.print("" + i + ":" + parties[i].getMutualPrivateKey() + " "); //TODO print only values
		}
		System.out.println("");
		System.out.print("public mutual keys :");
		for (int i=0; i<partiesAmount; ++i) {
			System.out.print("" + i + ":" + g.pow(parties[i].getMutualPrivateKey()) + " "); //TODO print only values
		}
		System.out.println("");
		System.out.print("server mutual keys :");
		polynom = thresholdCryptosystem.getMutualPublicKeys();
		for (int i=0; i<partiesAmount; ++i) {
			System.out.print("" + i + ":" + polynom[i] + " "); //TODO print only values
		}
		
		//TODO finish the sessions
	}

}
