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
		int d,temp;
		int n=Consts.VOTERS_AMOUNT;
		int[] result = new int[n]; //pi
		Random generator = new Random();
		for(int i=0;i<n-1;i++)
		{
			result[i]=i;
		}
		for(int i=0;i<n-1;i++)//for all cells except the last one [0,n-2]
		{
			d=generator.nextInt(n-i)+i; //generates a random number [i,n-1]
			temp=result[i];
			result[i]=result[d];
			result[d]=temp;			
		}
		return result;
	}
	/*
	 * permutate and re-encrypt A according to pi and using ElGamal module.
	 */
	public CryptObject[] PermutateAndReecncrypt(CryptObject[] A, int[] pi)
	{
		ElGamal gamal=new ElGamal(publicKey);
		int n=Consts.VOTERS_AMOUNT;
		CryptObject[] B=new CryptObject[n];	
		for(int i=0;i<n;i++) //create permutation according to pi[] and then - re-encrypt
		{
			B[i]=gamal.reencrypt(A[pi[i]]);			
		}
		return B;
	}

}
