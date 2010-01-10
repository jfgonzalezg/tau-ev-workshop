package zkp.GI;

import global.BigIntegerMod;
import global.Consts;
import elgamal.Ciphertext;
import elgamal.CryptObject;

import java.io.Serializable;
import java.math.BigInteger;

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
	private CryptObject[][] C; // [k][n] includes Ti
	private int[][] lambda; // [k][n]
	private String hash;


	public GIProof(CryptObject[][] C, int[][] lambda, String hash)
	{
		this.C = C;
		this.lambda = lambda;
		this.hash = hash;
	}

	public CryptObject[][] getC()
	{
		return C;
	}
	
	public CryptObject[] getCi(int i)
	{
		return C[i];
	}

	public int[][] getLambda()
	{
		return lambda;
	}
	
	public String getHash()
	{
		return hash;
	}

	public String toString()
	{
		String tempres1 = "";
		for (int i = 0; i < C.length; i++)
		{
			for (int j = 0; j < C[i].length; j++)
			{
				tempres1 = tempres1 + C[i][j].toString() + ", ";
			}
		}
		String tempres2 = "";
		for (int i = 0; i < lambda.length; i++)
		{
			for (int j = 0; j < lambda[i].length; j++)
			{
				tempres2 = tempres2 + lambda[i][j] + ", ";
			}
		}

		String str = " C: " + tempres1 + " lambda: " + tempres2 + " hash: " + hash; 
		return str;
	}


	

}
