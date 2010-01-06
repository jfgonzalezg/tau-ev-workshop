package zkp.OneOutOfL;


import global.BigIntegerMod;
import zkp.ZkpException;
import elgamal.Ciphertext;
import elgamal.CryptObject;

/**TODO - will use the list of pairs from pret-a-vote. generated once by them 
 * and transferred to us during the ZKP module initialization
 */


public interface IOneOutOfL {

	/**
	 * Returns a zero knowledge proof that the t'th pair in the list of pairs  
	 * is a re-encryption of the ciphertext pair in CryptObject, using r 	  
	 * @param CryptObject - a re-encryption the t'th pair in the list, using random (re-encryption is symmetric)
	 * @param h - a public key (Z*p) (for elgamal this is called C)
	 * @param t - index for the pair in the list that is a re-encryption.
	 * @return OneOutOfLProof object
	 * @throws ZkpException
	 */
	public OneOutOfLProof createOneOutOfLProof(CryptObject cryptObj, BigIntegerMod h, int t)
			throws ZkpException;

	/**
	 * Verifies using the ZKP that the ciphertext pair in CryptObject is a re-encryption of 
	 * one of the pairs in the list without knowing which pair it is or the random number used
	 * @param proof - ZKP ProofOneOutOfL object proving the above claim
	 * @param CryptObject - a re-encryption the t'th pair in the list, using random (re-encryption is symmetric)
	 * @param h - a public key (Z*p) (for elgamal this is called C)
	 * @return true if the proof holds, false otherwise.
	 * @throws ZkpException 
	 */
	public boolean verifyOneOutOfLProof(OneOutOfLProof proof, CryptObject cryptObj, BigIntegerMod h) 
			throws ZkpException;
	
}
