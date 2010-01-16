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
	
	public static int n=5;
	public static int[] pi = new int [] {4,1,0,2,3};
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
			//B[pi[j]] = new CryptObject(a, A[j], c);
			B[pi[j]]=gamal.reencrypt(A[j]);
		}
		
			
		try {
			giproof=gi.createGIProof(A, B, pi, n, w, Consts.getG());
			flag = gi.verifyGIProof(giproof, A, B, w, Consts.getG());
			if (flag)
				System.out.println("YES");
			else
				System.out.println("AVADOBI");
		} catch (ZkpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}