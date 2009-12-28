package MixCenter;

import elgamal.CryptObject;
import elgamal.Ciphertext;
import global.Consts;
import global.BigIntegerMod;
import java.math.BigInteger;

public class MixCenterTester {
	
	public static void testPermutation(MixCenter mc)
	{
		int n = mc.getVotersAmount();
		
		mc.generatePermutation();
		
		System.out.println("Printing permutation: ");
		for (int i=0; i<n; i++)
		{
			System.out.println("Pi[" + i + "]:" + mc.getPremutation(i) + "\n");
		}
		System.out.println("Done printing permutation\n");
		
	}
	
	public static void testPermutationAndReencryption(MixCenter mc)
	{
		
		BigIntegerMod bi1 = new BigIntegerMod(BigInteger.ONE, BigInteger.TEN);
		BigIntegerMod bi2 = new BigIntegerMod(BigInteger.ONE, BigInteger.TEN);
		
		BigIntegerMod bi3 = new BigIntegerMod(BigInteger.TEN, BigInteger.ONE);
		BigIntegerMod bi4 = new BigIntegerMod(BigInteger.TEN, BigInteger.ONE);
		
		int n = mc.getVotersAmount();
		
		//create A array
		Ciphertext[] A = new Ciphertext[n];
		for (int i=0; i<n; i++)
		{
			if ((i == 7) || (i == 9))
			{
				A[i] = new Ciphertext(bi3,bi4);				
				
			}
			else
				A[i] = new Ciphertext(bi1,bi2);
		}
		mc.setArrayA(A);
		
		//create B array
		CryptObject[] B = new CryptObject[n];	
		for (int i=0; i<n; i++)
		{
			B[i] = new CryptObject(bi1, A[mc.getPremutation(i)], bi2);
			if (mc.getPremutation(i) == 7)
			{
				System.out.println("pi["+i+"]=7"); //prints the new location of 7
			}
			if (mc.getPremutation(i) == 9)
			{
				System.out.println("pi["+i+"]=9"); //prints the new location of 9
			}
		}
		mc.setArrayB(B);
		
	}
	
	public static void testPrint(String msg, MixCenter mc)
	{
		mc.printToFile(msg);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

		MixCenter mc = new MixCenter(1);
		mc.setVotersAmount(400);
		testPermutation(mc);
		testPermutationAndReencryption(mc);
		testPrint("test", mc);
	}

}
