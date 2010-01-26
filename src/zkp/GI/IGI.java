package zkp.GI;


import zkp.ZkpException;
import global.Consts;
import elgamal.Ciphertext;
import elgamal.CryptObject;
import global.BigIntegerMod;
import java.math.BigInteger;



public interface IGI {

	/**
	 * Creates the GI Proof using the given A,B,w,pi,g
	 * @param A - the array representing the original permutation ciphertext[n]. ciphertext=2*BigIntegerMod*n=1024*2*400=819200
	 * @param B - the array representing the new permutation (including the random r used for re-encryption for each vote). CryptObject[]
	 * @param g - computed by ElGamal (global)
	 * @param w - computed by ElGamal (global)
	 * @param pi - represents the permutation of the mix net - integer[n]
	 * @return GIProof - representing the ZKP proof - contains:
	 * 		Ci - Challenge - ciphertext[n][k] - 2*BigIntegerMod*n*k=1024*2*400*40=32768000
	 *		lambda = lambda1...lambdak - Integer[k][n] - n*k=400*40=16000
	 *		T = t1...tk  - BigIntegerMod[k] - 1024bit*K=1024*40=40960bits - as part of Ci CryptObject[]
	 *			ti is out of Zq - BigIntegerMod - 1024bit
	 *		hash - represents the coin toss of the proof - we need only K bits out of 1024 - K=40bits
	 *		
	 */
	public GIProof createGIProof(Ciphertext[] A, CryptObject[] B,int[] pi,int n,BigIntegerMod w,BigIntegerMod g)
			throws ZkpException;

	

	/**
	 * Checks the GI Proof using the given A,B,w,g
	 * @param GIProof - representing the ZKP proof
	 * @param Ai - the array representing the original permutation
	 * @param Bi - the array representing the new permutation
	 * @return	true - all iterations returns true
	 *			false - one of the iterations returned false
	 *			prints to file the values of the matrixes for validation
	 * */
	public boolean verifyGIProof (GIProof proof, Ciphertext[] A, CryptObject[] B,BigIntegerMod w,BigIntegerMod g)
			throws ZkpException;

		

}


