package threshold.center;

import java.math.BigInteger;

import threshold.IThresholdCryptosystem;


import elgamal.Ciphertext;
import global.BigIntegerMod;
import global.Consts;
import global.Consts.DebugOutput;

public class ThresholdCryptosystem implements IThresholdCryptosystem {

	private int partiesAmount;
	private int threshold;
	private BigInteger p; 
	private BigIntegerMod g;
	private BigIntegerMod mutualPublicKey;

	public ThresholdCryptosystem() {
		applyConstructor(Consts.PARTIES_AMOUNT, Consts.THRESHOLD, Consts.getP(), Consts.getG());
	}

	public ThresholdCryptosystem(int partiesAmount, int threshold, BigInteger p, BigIntegerMod g) {
		applyConstructor(partiesAmount, threshold, p, g);
	}

	private void applyConstructor(int partiesAmount, int threshold, BigInteger p, BigIntegerMod g) {
		this.partiesAmount = partiesAmount;
		this.threshold = threshold;
		this.p = p;
		this.g = g;
		this.mutualPublicKey = generateMutualPublicKey();
	}

	/**
	 * Generates a mutual public key for a mutual encryption
	 * @return the generated public key, where (public key).getMod()==Consts.getQ()
	 */
	private BigIntegerMod generateMutualPublicKey() {
		// TODO - implement
		return null;
	}

	public int getPartiesAmount() {
		return partiesAmount;
	}

	public int getThreshold() {
		return threshold;
	}

	public BigInteger getP() {
		return p;
	}

	public BigIntegerMod getG() {
		return g;
	}

	public BigIntegerMod getMutualPublicKey() {
		return mutualPublicKey;
	}

	public BigIntegerMod decryptMutually(Ciphertext ciphertext) {
		return decryptMutually(ciphertext, Consts.THRESHOLD);
	}

	public BigIntegerMod decryptMutually(Ciphertext ciphertext, int parties_to_use) {
		if (parties_to_use < Consts.THRESHOLD) {
			Consts.log("Trying to mutually decrypt using too few parties (" + parties_to_use + " < " + Consts.THRESHOLD + ")", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			return null;
		}
		if (parties_to_use > Consts.PARTIES_AMOUNT) {
			Consts.log("Trying to mutually decrypt using more parties than exist (" + parties_to_use + " > " + Consts.PARTIES_AMOUNT + ")", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			return null;
		}
		// TODO randomly choose threshold parties
		// TODO decrypt the given ciphertext using the threshold parties' private keys
		return null;
	}

}
