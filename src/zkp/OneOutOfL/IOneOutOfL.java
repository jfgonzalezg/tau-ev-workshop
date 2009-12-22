package zkp.OneOutOfL;


import global.BigIntegerMod;
import zkp.ZkpException;
import elgamal.Ciphertext;

/**TODO - will use the list of pairs from pret-a-vote. generated once by them 
 * and transferred to us during the ZKP module initialization
 */


public interface IOneOutOfL {

	/**
	 * Returns a zero knowledge proof that the t'th pair in the list of pairs  
	 * is a re-encryption of the ciphertext pair using r 	  
	 * @param ciphertext - a re-encryption the t'th pair in the list, using random (re-encryption is symmetric)
	 * @param h - a public key (Z*p) (for elgamal this is called C)
	 * @param r - a random number from Z*q.
	 * @param t - index for the pair in the list that is a re-encryption.
	 * @return OneOutOfLProof object
	 * @throws ZkpException
	 */
	public OneOutOfLProof createOneOutOfLProof(Ciphertext ciphertext, BigIntegerMod h, BigIntegerMod r, int t)
			throws ZkpException;

	/**
	 * Verifies using the ZKP that the ciphertext pair is a re-encryption of 
	 * one of the pairs in the list without knowing which pair it is or the random number used
	 * @param proof - ZKP ProofOneOutOfL object proving the above claim
	 * @param ciphertext - a re-encryption the t'th pair in the list, using random (re-encryption is symmetric)
	 * @param h - a public key (Z*p) (for elgamal this is called C)
	 * @return true if the proof holds, false otherwise.
	 * @throws ZkpException 
	 */
	public boolean verifyOneOutOfLProof(OneOutOfLProof proof, Ciphertext ciphertext, BigIntegerMod h) 
			/*throws ZkpException*/;
	
}
