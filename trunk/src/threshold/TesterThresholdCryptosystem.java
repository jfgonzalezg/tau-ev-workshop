package threshold;

import java.math.BigInteger;

import elgamal.Ciphertext;
import elgamal.ElGamal;

import global.BigIntegerMod;
import global.Consts;
import threshold.center.ThresholdCryptosystem;
import threshold.parties.PartiesManager;

public class TesterThresholdCryptosystem {
	
	private static final int NUM_OF_CHECKS = 10;
	public static void main(String[] args) {
		
		BigInteger p = Consts.getP();
		BigIntegerMod g = Consts.getG();
		int partiesAmount = Consts.PARTIES_AMOUNT;
		int threshold = Consts.THRESHOLD;
		int decryptAmount = threshold;
		
		
		//p = new BigInteger("23");
		//g = new BigIntegerMod(Consts.TWO, p);
		//partiesAmount = 10;
		//threshold = 6;
		//decryptAmount = threshold;
		//decryptAmount = partiesAmount;
		
		System.out.println("p = " + p);
		System.out.println("g = " + g);
		
		/*****************************************
		 * initializing the threshold central unit
		 *****************************************/
		ThresholdCryptosystem thresholdCryptosystem = new ThresholdCryptosystem(partiesAmount, threshold, p, g, null);
		
		/**************************
		 * initializing all parties
		 **************************/
		new PartiesManager(partiesAmount, null, null);
		
		
		BigIntegerMod m;
		Ciphertext m_encrypted;
		BigIntegerMod m_decrypted;
		ElGamal eg = new ElGamal(p, g, thresholdCryptosystem.getMutualPublicKey(), null);
		for (int i=0; i<NUM_OF_CHECKS; ++i) {
			 m = new BigIntegerMod(p);
			 System.out.println("Tester: m to encrypt: "+m.getValue());
			 m_encrypted = eg.encrypt(m).getCiphertext();
			 System.out.println("Tester: m encrypted: "+m_encrypted);
			 m_decrypted = thresholdCryptosystem.decryptMutually(m_encrypted, decryptAmount);
			 System.out.println("Tester: m decrypted: "+m_decrypted.getValue());
			 System.out.println("Tester: decryption "+(m.equals(m_decrypted)?"correct":"incorrect"));
			 System.out.println("");
		}
		
		/*****************************************************
		 * closing the central and parties (takes some seconds)
		 *****************************************************/
		thresholdCryptosystem.close();
		
		System.out.println("Tester: finished. Goodbye...");
	}

}
