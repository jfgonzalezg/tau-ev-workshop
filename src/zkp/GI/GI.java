package zkp.GI;

import elgamal.Ciphertext;
import elgamal.CryptObject;
import elgamal.ElGamal;
import global.BigIntegerMod;
import global.Consts;
import zkp.ZkpException;
import zkp.GI.GIProof;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class GI implements IGI 
{
	private static int Repetition=2;
	
	public GIProof createGIProof(Ciphertext[] A, CryptObject[] B,int[] pi,int n,BigIntegerMod w,BigIntegerMod g)
	{	
		CryptObject[][] C = new CryptObject[Repetition][n];
		int[][] lambda = new int[Repetition][n];
		String hash="";

		BigIntegerMod tempR;
		int[] piInv = new int[n];
		int[] lambdaTagInv = new int[n];
 
		for (int i = 0; i < Repetition; i++)
		{
			lambda[i] = createLambda(n);
			//lambda[i] = new int[]{1,0,2,4,3};
			C[i] = createCi(A,lambda[i],n,w,g);
		}

		try {
			hash = createHash(A,B,C); //returns a string with length of Repetition
		} catch (ZkpException e) {
			return null;
		} 
		piInv = createPiInv(pi);

		for (int i = 0; i < Repetition; i++)
		{
			if (hash.charAt(i) != '0')
			{
				lambda[i] = makelambdaTAG(lambda[i],piInv);
				lambdaTagInv = createPiInv(lambda[i]);
				for (int j = 0;j <n; j++)//	makeTiTAG()
				{
					tempR = C[i][j].getR().substract(B[lambdaTagInv[j]].getR());//ti-ri
					C[i][j].setR(tempR)	;
				}
			}
		}
		
		return new GIProof(C,lambda,hash);
	}
	

	
	
	public boolean verifyGIProof (GIProof proof, Ciphertext[] A, CryptObject[] B,BigIntegerMod w,BigIntegerMod g)
	{
		boolean flag = true;

		for (int i = 0; i < Repetition; i++)
		{
			if (proof.getHash().charAt(i) == '0')
			{
							
				flag = compareMatrix(A, proof.getCi(i), proof.getLambda()[i],w,g);
				if (flag==false)
				{
					return false;
				}
			}
			else
			{
				flag = compareMatrix(B, proof.getCi(i), proof.getLambda()[i], w, g);
				if (flag==false)
				{
					return false;
				}

			}
		}

		return true;
	}
	


	private static int[] createLambda(int n)
	{
		int d,temp;
		int[] permutation = new int[n]; 
		Random generator = new Random();
		for(int i=0;i<n;i++) //initialize the permutation array.
		{
			permutation[i]=i;
		}
		for(int i=0;i<n-1;i++)//for all cells except the last one [0,n-2]
		{
			d=generator.nextInt(n-i)+i; //generates a random number [i,n-1]
			temp=permutation[i];
			permutation[i]=permutation[d];
			permutation[d]=temp;			
		}
		return permutation;

	}



	private static CryptObject[] createCi(Ciphertext[] A,int[] lambda, int n,BigIntegerMod w,BigIntegerMod g)
	{
		ElGamal gamal=new ElGamal(Consts.getP(),g,w,null);
		CryptObject[] tempC=new CryptObject[n];	
		for(int i=0;i<n;i++) //create permutation according to pi[] and then - re-encrypt
		{
			tempC[lambda[i]]=gamal.reencrypt(A[i]);			
		}
		return tempC;
	}


	
	//create hash for GI
	private static String createHash(Ciphertext[] A, CryptObject[] B, CryptObject[][] C) throws ZkpException
	{

		BigInteger temp = null;
		String challenge = "";
		//use MD5 hash function for challenge
		MessageDigest m;
		try
		{
			m = MessageDigest.getInstance("MD5");
			for (int i=0; i<A.length; i++)
			{
				m.update(A[i].getA().getValue().toByteArray());
				m.update(A[i].getB().getValue().toByteArray());
			}
			for (int i=0; i<B.length; i++)
			{
				m.update(B[i].getCiphertext().getA().getValue().toByteArray());
				m.update(B[i].getCiphertext().getB().getValue().toByteArray());
			}
			for (int i = 0; i < C.length; i++)
			{
				for (int j = 0; j < C[i].length; j++)
				{
					m.update(C[i][j].getCiphertext().getA().getValue().toByteArray());
					m.update(C[i][j].getCiphertext().getB().getValue().toByteArray());
				}
			}
			temp = new BigInteger(m.digest()).abs(); //create biginteger hash
			String tempstr = temp.toString(2); //change biginteger to binary string
			challenge = tempstr.substring(0,Repetition); //cut first "repetition" chars from string 
		}
		catch (NoSuchAlgorithmException exception)
		{
			throw new ZkpException(exception.getMessage());
		}
		//
		//challenge = "00";
		//
		return challenge;
	}
		

			
	private static int[] createPiInv(int[] pi) 
	{
		int[] piInv = new int[pi.length];
		for (int i = 0;i<pi.length;i++)
		{
			piInv[pi[i]] = i;
		}
		return piInv;
	}


	private static int[] makelambdaTAG(int[] lambda, int[] piInv)
	{
		int[] lambdaTAG = new int[piInv.length];
		int temp;
		for (int i = 0; i < piInv.length; i++)
		{
			temp = lambda[piInv[i]];
			lambdaTAG[i] = temp;
		}
		return lambdaTAG;
	}




	private static boolean compareMatrix(Ciphertext[] matrix1, CryptObject[] matrix2, int[] premutation, BigIntegerMod w, BigIntegerMod g)
	{
		ElGamal gamal=new ElGamal(Consts.getP(),g,w,null);
		CryptObject[] tempCipher = new CryptObject[matrix1.length];

		for (int i = 0; i < matrix1.length; i++)
		{

			tempCipher[premutation[i]] = gamal.reencrypt(matrix1[i], matrix2[premutation[i]].getR());
		}
		for (int i = 0; i < matrix1.length; i++)
		{
			if ((tempCipher[i].getCiphertext().getA().equals(matrix2[i].getCiphertext().getA()) != true) || (tempCipher[i].getCiphertext().getB().equals(matrix2[i].getCiphertext().getB()) != true))
			{
				return false;
			}
		}
	
		return true;
	}
	
	
	private static boolean compareMatrix(CryptObject[] matrix1, CryptObject[] matrix2, int[] premutation, BigIntegerMod w, BigIntegerMod g)
	{
		ElGamal gamal=new ElGamal(Consts.getP(),g,w,null);
		CryptObject[] tempCipher = new CryptObject[matrix1.length];

		for (int i = 0; i < matrix1.length; i++)
		{

			tempCipher[premutation[i]] = gamal.reencrypt(matrix1[i].getCiphertext(), matrix2[premutation[i]].getR());
		}
		for (int i = 0; i < matrix1.length; i++)
		{
			if ((tempCipher[i].getCiphertext().getA().equals(matrix2[i].getCiphertext().getA()) != true) || (tempCipher[i].getCiphertext().getB().equals(matrix2[i].getCiphertext().getB()) != true))
			{
				return false;
			}
		}
	
		return true;
	}
	
	
}


