package threshold;

import java.math.BigInteger;

import elgamal.Ciphertext;
import global.BigIntegerMod;
// import global.Consts;

public interface IThresholdCryptosystem {

	/**
	 * The implementing class will be threshold.center.ThresholdCryptosystem
	 * The following 2 will be the constructors of threshold.center.ThresholdCryptosystem

	public ThresholdCryptosystem(int portnum);
	public ThresholdCryptosystem(int partiesAmount, int threshold, BigInteger p, BigIntegerMod g, int portnum);

	 * Note that the first one will use the global consts
	 */

	/**
	 * Decrypts the given ciphertext by using THRESHOLD parties
	 * @param ciphertext - the ciphertext to decrypt
	 * @return the decrypted message
	 */
	public BigIntegerMod decryptMutually(Ciphertext ciphertext);

	/**
	 * Decrypts the given ciphertext by using the threshold parties
	 * @param ciphertext - the ciphertext to decrypt
	 * @param parties_to_use - the amount of parties to use for the mutual decryption
	 * 		where Consts.THRESHOLD < parties_to_use < Consts.PARTIES_AMOUNT
	 * @return the decrypted message
	 */
	public BigIntegerMod decryptMutually(Ciphertext ciphertext, int parties_to_use);

	/**
	 * @return the parties amount of this object
	 */
	public int getPartiesAmount();

	/**
	 * @return the threshold of this object
	 */
	public int getThreshold();

	/**
	 * @return the prime p of this object
	 */
	public BigInteger getP();

	/**
	 * @return the generator g of this object, where g.getMod()==Consts.getP()
	 */
	public BigIntegerMod getG();

	/**
	 * @return the mutual public key of this object, where (private key).getMod()==Consts.getQ()
	 */
	public BigIntegerMod getMutualPublicKey();

}
