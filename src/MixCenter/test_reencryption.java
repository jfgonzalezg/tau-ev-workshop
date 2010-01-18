package MixCenter;

import java.util.Random;

import elgamal.CryptObject;
import elgamal.ElGamal;
import MixCenter.Ran1_test;
import MixCenter.Ran2_test;



public class test_reencryption 
{
	public static int VOTERS_AMOUNT=5;
	public static int pi[];
	public static Ran1_test A[];
	public static Ran2_test B[];
	
	public static void generatePermutation()
	{
		int d,temp;
		pi = new int[VOTERS_AMOUNT]; 
		Random generator = new Random();
		for(int i=0;i<VOTERS_AMOUNT;i++) //initialize the permutation array.
		{
			pi[i]=i;
		}
		for(int i=0;i<VOTERS_AMOUNT-1;i++)//for all cells except the last one [0,n-2]
		{
			d=generator.nextInt(VOTERS_AMOUNT-i)+i; //generates a random number [i,n-1]
			temp=pi[i];
			pi[i]=pi[d];
			pi[d]=temp;			
		}
	}
	public static Ran2_test reencrypt(Ran1_test a)
	{
		Ran2_test b=new Ran2_test(a);
		return b;
	}
	/*
	 * permutate and re-encrypt A according to pi and using ElGamal module.
	 */
	public static void PermutateAndReecncrypt()
	{
		B=new Ran2_test[VOTERS_AMOUNT];
		for(int i=0;i<VOTERS_AMOUNT;i++) //create permutation according to pi[] and then - re-encrypt
		{
			B[pi[i]]=reencrypt(A[i]);			
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		A=new Ran1_test[VOTERS_AMOUNT];
		for(int i=0;i<VOTERS_AMOUNT;i++)
		{//initialize A.
			A[i]=new Ran1_test(i);
		}
		generatePermutation();
	//	pi[0]=4;pi[1]=1;pi[2]=0;pi[3]=2;pi[4]=3;
		PermutateAndReecncrypt();
		System.out.println("A: ");
		for (int i=0;i<VOTERS_AMOUNT;i++)
		{
			System.out.print(A[i].num+" ");
		}
		System.out.println();
		System.out.println("Pi: ");
		for (int i=0;i<VOTERS_AMOUNT;i++)
		{
			System.out.print(pi[i]+" ");
		}
		System.out.println();
		System.out.println("B: ");
		for (int i=0;i<VOTERS_AMOUNT;i++)
		{
			System.out.print(B[i].ran.num+" ");
		}
		System.out.println("\n");
		

	}

}
