package zkp.GI;

import elgamal.CryptObject;
import elgamal.Ciphertext;
import elgamal.ElGamal;
import global.Consts;
import global.BigIntegerMod;
import java.math.BigInteger;

import zkp.ZkpException;

import MixCenter.Ran1_test;

public class  GItest{
	
	public static int n=4;
	public static int[] pi = new int [] {2,3,0,1};
	//public static Ciphertext[] A;
	//public static CryptObject[] B;
	public static BigIntegerMod w = Consts.getG().pow(Consts.getQ());
	public static GIProof giproof;
	public static boolean flag;
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

		GI gi = new GI();
		ElGamal gamal=new ElGamal(Consts.getP(),Consts.getG(),w,null);
		
		BigIntegerMod a = new BigIntegerMod(Consts.getP());
		BigIntegerMod b = new BigIntegerMod(Consts.getP());
		BigIntegerMod c = new BigIntegerMod(Consts.getQ());
		Ciphertext[] A = new Ciphertext[n];
		CryptObject[] B = new CryptObject[n];	
		
		for(int i=0;i<pi.length;i++)
		{//initialize A.
			
			A[i]=new Ciphertext(a,b);
		}
		
		for (int j=0; j<n; j++)
		{
			B[j] = new CryptObject(a, A[pi[j]], c);
		}
		
			
		try {
			giproof=gi.createGIProof(A, B, pi, n, w, Consts.getG());
			flag = gi.verifyGIProof(giproof, A, B, w, Consts.getG());
		} catch (ZkpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}