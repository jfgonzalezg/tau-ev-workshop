package MixCenter;

import elgamal.CryptObject;
import elgamal.Ciphertext;
import global.Consts;
import global.BigIntegerMod;
import java.math.BigInteger;

public class MixCenterTester {
	
	public static void testPermutationGeneration(MixCenter mc)
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
	
	public static void testPermutation(MixCenter mc)
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
		mc.printToFile(msg, true);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

		int numOfVotes = 400;
		int id = 1;
		
		MixCenter MC = new MixCenter(id);
		MixCenter.write("Mix Center No." + id + " is working...", id, true);
		
		
		//Create some votes
		Ciphertext[] votes= new Ciphertext[numOfVotes];
		BigInteger p = new BigInteger("23");
		BigInteger q = new BigInteger("11");
		BigIntegerMod g = new BigIntegerMod(new BigInteger("2"),p);
		BigIntegerMod w = new BigIntegerMod(new BigInteger("7"),p);
		for (int i = 0; i<numOfVotes; i++){
			BigIntegerMod a = new BigIntegerMod(new BigInteger("6"), p);
			BigIntegerMod b = new BigIntegerMod(new BigInteger("17"), p);
			//BigIntegerMod a = new BigIntegerMod(new BigInteger("9"+10*i),p);
			//BigIntegerMod b = new BigIntegerMod(new BigInteger("11"+10*i),p);
			Ciphertext vote = new Ciphertext(a, b);
			votes[i] = vote;
		}
		MC.setVotersAmount(numOfVotes);
		MC.setQ(q);
		MC.setP(p);
		MC.setG(g);
		MC.setW(w);
		MC.setArrayA(votes);
		
		
		MC.generatePermutation();
		MixCenter.write("Starting reencypting and permutating the data...", id, false);
		MC.PermutateAndReecncrypt();
		MixCenter.write("Finished reencypting and permutating the data, performing ZKP...", id, false);
		String proof = MC.performZKP();
		boolean isZKPValid = true;
		if (proof == null){
			isZKPValid = false;
			MixCenter.write("Mix Center No." + id + " failed to get valid ZKP, this MC will not take part of the elections... goodbye :-( \r\n\r\n", id, false);
		}
		//in case ZKP proof was false
		if (proof == "falseProof"){
			isZKPValid = false;
			MixCenter.write("Mix Center No." + id + " ZKP proof wasn't correct :-( \r\n\r\n", id, false);
		}
		else{
			MC.printToFile(proof, isZKPValid);
			MixCenter.write("ZKP is done, proof file was created and has valid data...", id, false);
		} 
		/*MixCenter mc = new MixCenter(1);
		mc.setVotersAmount(400);
		testPermutation(mc);
		testPermutationAndReencryption(mc);
		testPrint("test", mc);*/
	}

}
