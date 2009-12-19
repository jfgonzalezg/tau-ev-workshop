package MixCenter;

import java.util.Random;

import sun.awt.datatransfer.DataTransferer.ReencodingInputStream;
import elgamal.CryptObject;
import global.Consts;
import elgamal.ElGamal;

public class MixCenter 
{
	/*
	 * generates the array (pi) that represents the permutation that will be made.
	 */
	private int[] generatePermutation()
	{
		int d;
		int n=Consts.VOTERS_AMOUNT;
		int[] result = new int[n]; //pi
		Random generator = new Random();
		for(int i=0;i<n-1;i++)//for all cells except the last one [0,n-2]
		{
			d=generator.nextInt(n-i)+i; //generates a random number [i,n-1]
			result[i]=d;
			result[d]=i;			
		}
		return result;
	}
	
	public CryptObject[] PermutateAndReecncrypt(CryptObject[] A, int[] pi)
	{
		ElGamal gamal=new ElGamal(publicKey)
		int n=Consts.VOTERS_AMOUNT;
		CryptObject[] B=new CryptObject[n];	
		for(int i=0;i<n;i++) //create permutation according to pi[] and then - reencrypt
		{
			B[i]=gamal.reencrypt(A[pi[i]]);			
		}
		return B;
	}

}
