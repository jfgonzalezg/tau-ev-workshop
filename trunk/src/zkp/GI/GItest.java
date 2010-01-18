package zkp.GI;

import elgamal.CryptObject;
import elgamal.Ciphertext;
import elgamal.ElGamal;
import global.Consts;
import global.BigIntegerMod;
import java.math.BigInteger;
import java.util.Random;

import zkp.ZkpException;

import MixCenter.Ran1_test;

public class  GItest{
	
	public static int n=400;
	//public static int[] pi = new int [] {4,1,0,2,3};
	public static int[] pi;
	//public static Ciphertext[] A;
	//public static CryptObject[] B;
	//public static BigIntegerMod w = Consts.getG().pow(Consts.getQ());
	public static GIProof giproof;
	public static boolean flag;
	
	
	public static void generatePermutation()
	{
		int d,temp;
		pi = new int[n]; 
		Random generator = new Random();
		for(int i=0;i<n;i++) //initialize the permutation array.
		{
			pi[i]=i;
		}
		for(int i=0;i<n-1;i++)//for all cells except the last one [0,n-2]
		{
			d=generator.nextInt(n-i)+i; //generates a random number [i,n-1]
			temp=pi[i];
			pi[i]=pi[d];
			pi[d]=temp;			
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		BigInteger p = new BigInteger("23");
		BigIntegerMod g = new BigIntegerMod(new BigInteger("2"),p);
		BigIntegerMod w = new BigIntegerMod(new BigInteger("7"),p);
		
		
		GI gi = new GI();
		//ElGamal gamal=new ElGamal(Consts.getP(),Consts.getG(),w,null);
		ElGamal gamal=new ElGamal(p,g,w,null);
		
		generatePermutation();
		
		//BigIntegerMod a = new BigIntegerMod(Consts.getP());
		//BigIntegerMod b = new BigIntegerMod(Consts.getP());
		BigIntegerMod a = new BigIntegerMod(p);
		BigIntegerMod b = new BigIntegerMod(p);
		
		Ciphertext[] A = new Ciphertext[n];
		CryptObject[] B = new CryptObject[n];	
		
		for(int i=0;i<pi.length;i++)
		{//initialize A.
			
			A[i]=new Ciphertext(a,b);
		}
		
		for (int j=0; j<n; j++)
		{
			B[pi[j]]=gamal.reencrypt(A[j]);
			if ((!(B[pi[j]].getCiphertext().getA().equals(A[j].getA().multiply(g.pow(B[pi[j]].getR())))))
					&& (!(B[pi[j]].getCiphertext().getB().equals(A[j].getB().multiply(w.pow(B[pi[j]].getR()))))))
					{
					System.out.println("wrong reencryption for i=" + j);
					}
		}
			
	
			giproof=gi.createGIProof(A, B, pi, n, w, g);
			System.out.println(giproof.toString());
			flag = gi.verifyGIProof(giproof, A, B, w, g);
			if (flag)
				System.out.println("YES");
			else
				System.out.println("NO");
		
		
	}

}