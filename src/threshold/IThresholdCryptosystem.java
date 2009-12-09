package threshold;

import elgamal.Ciphertext;
import global.BigIntegerMod;

public interface IThresholdCryptosystem {

	/**
	 * The implementing class will be threshold.ThresholdCryptosystem
	 * The following will be the constructor of threshold.ThresholdCryptosystem

	public ThresholdCryptosystem();

	 */


	/**
	 * Generates a mutual public key for a mutual encryption
	 * @return the generated public key, where (public key).getMod()==Consts.getQ()
	 */
	public BigIntegerMod generateMutualPublicKey();

	/**
	 * Decrypts the given ciphertext by using THRESHOLD parties
	 * @param ciphertext - the ciphertext to decrypt
	 * @return the decrypted message
	 */
	public BigIntegerMod decryptMutually(Ciphertext ciphertext);

	/**
	 * Decrypts the given ciphertext by using the threshold parties
	 * @param ciphertext - the ciphertext to decrypt
	 * @param threshold - the amount of parties to use for the mutual decryption
	 * @return the decrypted message
	 */
	public BigIntegerMod decryptMutually(Ciphertext ciphertext, int threshold);

}
