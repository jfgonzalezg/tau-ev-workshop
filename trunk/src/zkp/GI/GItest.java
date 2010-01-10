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
	
	public static int n=1;
	public static int[] pi = new int [] {2,3,4,1};
	public static Ciphertext[] A;
	public static CryptObject[] B;
	public static BigIntegerMod w = Consts.getG().pow(Consts.getQ());
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

		GI gi = new GI();
		ElGamal gamal=new ElGamal(Consts.getP(),Consts.getG(),w,null);
		
		for(int i=0;i<pi.length;i++)
		{//initialize A.
			BigIntegerMod a = new BigIntegerMod(Consts.getQ());
			BigIntegerMod b = new BigIntegerMod(Consts.getQ());
			A[i]=new Ciphertext(a,b);
			B[i] = gamal.reencrypt(A[i]);
		}
			
		try {
			gi.createGIProof(A, B, pi, n, w, Consts.getG());
		} catch (ZkpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}