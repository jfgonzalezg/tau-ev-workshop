package zkp.GI;

import elgamal.Ciphertext;
import elgamal.CryptObject;
import elgamal.ElGamal;
import global.BigIntegerMod;
import global.Consts;
import zkp.ZkpException;
import zkp.GI.GIProof;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;




public class GI implements IGI 
{
	private static int Repetition=40;
	private static String File_Path = "C:\\ZkpGIProof.txt";
	
	
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
				writeToFile("");
				writeToFile("");
				writeToFile("########## Starting Run number " + i + " ##########");
				flag = compareMatrix(A, proof.getCi(i), proof.getLambda()[i],w,g);
				if (flag==false)
				{
					return false;
				}
			}
			else
			{
				writeToFile("");
				writeToFile("");
				writeToFile("########## Starting Run number " + i + " ##########");
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
		ElGamal gamal=new ElGamal(g.getMod(),g,w,null);
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
		BigIntegerMod matrix1A,matrix1B,matrix2A,matrix2B;
		
		ElGamal gamal=new ElGamal(g.getMod(),g,w,null);
		CryptObject[] tempCipher = new CryptObject[matrix1.length];

		for (int i = 0; i < matrix1.length; i++)
		{

			tempCipher[premutation[i]] = gamal.reencrypt(matrix1[i], matrix2[premutation[i]].getR());
		}
		for (int i = 0; i < matrix1.length; i++)
		{
			//if ((tempCipher[i].getCiphertext().getA().equals(matrix2[i].getCiphertext().getA()) != true) || (tempCipher[i].getCiphertext().getB().equals(matrix2[i].getCiphertext().getB()) != true))
			/*if ((!(matrix2[premutation[i]].getCiphertext().getA().equals(matrix1[i].getA().multiply(g.pow(matrix2[premutation[i]].getR())))))
					&& (!(matrix2[premutation[i]].getCiphertext().getB().equals(matrix1[i].getB().multiply(w.pow(matrix2[premutation[i]].getR()))))))*/
			matrix1A = matrix1[i].getA().multiply(g.pow(matrix2[premutation[i]].getR()));
			matrix1B = matrix1[i].getB().multiply(w.pow(matrix2[premutation[i]].getR()));
			matrix2A = matrix2[premutation[i]].getCiphertext().getA();
			matrix2B = matrix2[premutation[i]].getCiphertext().getB();
			
			writeToFile("a of matrix A ["+ i +"] =" + matrix1A.toString());
			writeToFile("a of matrix C ["+ i +"] =" + matrix2A.toString());
			writeToFile("b of matrix A ["+ i +"] =" + matrix1B.toString());
			writeToFile("b of matrix C ["+ i +"] =" + matrix2B.toString());
			writeToFile("");
			
			if ((!(matrix2A.equals(matrix1A)))
					|| (!(matrix2B.equals(matrix1B))))
			{
				return false;
			}
		}
	
		return true;
	}
	
	
	private static boolean compareMatrix(CryptObject[] matrix1, CryptObject[] matrix2, int[] premutation, BigIntegerMod w, BigIntegerMod g)
	{
		BigIntegerMod matrix1A,matrix1B,matrix2A,matrix2B;
		
		
		ElGamal gamal=new ElGamal(g.getMod(),g,w,null);
		CryptObject[] tempCipher = new CryptObject[matrix1.length];

		for (int i = 0; i < matrix1.length; i++)
		{

			tempCipher[premutation[i]] = gamal.reencrypt(matrix1[i].getCiphertext(), matrix2[premutation[i]].getR());
			
		}
		for (int i = 0; i < matrix1.length; i++)
		{
		
			matrix1A = matrix1[i].getCiphertext().getA().multiply(g.pow(matrix2[premutation[i]].getR()));
			matrix1B = matrix1[i].getCiphertext().getB().multiply(w.pow(matrix2[premutation[i]].getR()));
			matrix2A = matrix2[premutation[i]].getCiphertext().getA();
			matrix2B = matrix2[premutation[i]].getCiphertext().getB();
			
			writeToFile("a of matrix B ["+ i +"] =" + matrix1A.toString());
			writeToFile("a of matrix C ["+ i +"] =" + matrix2A.toString());
			writeToFile("b of matrix B ["+ i +"] =" + matrix1B.toString());
			writeToFile("b of matrix C ["+ i +"] =" + matrix2B.toString());
			writeToFile("");
			
			//if ((tempCipher[i].getCiphertext().getA().equals(matrix2[i].getCiphertext().getA()) != true) || (tempCipher[i].getCiphertext().getB().equals(matrix2[i].getCiphertext().getB()) != true))
			/*if ((!(matrix2[premutation[i]].getCiphertext().getA().equals(matrix1[i].getCiphertext().getA().multiply(g.pow(matrix2[premutation[i]].getR())))))
					&& (!(matrix2[premutation[i]].getCiphertext().getB().equals(matrix1[i].getCiphertext().getB().multiply(w.pow(matrix2[premutation[i]].getR()))))))*/
			if ((!(matrix2A.equals(matrix1A)))
					|| (!(matrix2B.equals(matrix1B))))
					
			{
				return false;
			}
		}
	
		return true;
	}
	

	


	
	
private static void writeToFile(String s) {
        
        BufferedWriter bufferedWriter = null;
        
        try {
            
            bufferedWriter= new BufferedWriter(new FileWriter(File_Path,true));
            bufferedWriter.append(s);
            bufferedWriter.newLine();
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //Close the BufferedWriter
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

	
	
}


