package zkp.GI;

import global.BigIntegerMod;
import elgamal.Ciphertext;
import java.io.Serializable; 

/**
	 * @return GIProof - representing the ZKP proof - containes:
	 * 		Ci - Challenge - ciphertext[n][k] - 2*BigIntegerMod*n*k=1024*2*400*40=32768000
	 *		lambda = lambda1...lambdak - Integer[k][n] - n*k=400*40=16000
	 *		T = t1...tk  - BigIntegerMod[k] - 1024bit*K=1024*40=40960bits
	 *			ti is out of Zq - BigIntegerMod - 1024bit
	 *		hash - reprsents the coin toss of the proof - we need only K bits out of 1024 - K=40bits
 */

public class GIProof implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4379903319068606945L;
	private Ciphertext[][] C; // [k][n]
	private int[] lambda; // [k]
	private BigIntegerMod[][] T; //[k][n]
	private BigIntegerMod hash;

	public GIProof(Ciphertext[][] Ci, int[] lambda, BigIntegerMod[] T, BigIntegerMod hash)
	{
		//TO do
	}
/*
	public Ciphertext[][] getC()
	{
		//TO do
	}
	
	public Ciphertext[] getCi(int i)
	{
		//TO do
	}

	public int[] getlambda()
	{
		//TO do
	}

	public BigIntegerMod[] getT()
	{
		//TO do
	}

	public BigIntegerMod getTi(int i)
	{
		//TO do
	}
	
	public BigIntegerMod gethash()
	{
		//TO do
	}
	*/
}
