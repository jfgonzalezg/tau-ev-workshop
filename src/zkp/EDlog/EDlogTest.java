package zkp.EDlog;

import global.Consts;
import global.BigIntegerMod;
import java.math.BigInteger;
import zkp.ZkpException;

public class EDlogTest {

	
	
	public static void main(String[] args)
	{
	
		BigIntegerMod x = new BigIntegerMod(BigInteger.TEN, Consts.getQ());
		BigIntegerMod h = Consts.getG().pow(x);
		BigIntegerMod a = Consts.getG().pow(x);
		BigIntegerMod b = h.pow(x);
		
		System.out.println("x: "+x.toString()+"\n");
		System.out.println("a: "+a.toString()+"\n");
		System.out.println("b: "+b.toString()+"\n");
	
		
		EDlog edlog = new EDlog();
		edlog.setG(h);
		
		
		try {
			EDlogProof proof = edlog.createEDlogProof(h, a, b, x);
			System.out.println("proofU: "+proof.getU().toString()+"\n");
			System.out.println("proofV: "+proof.getV().toString()+"\n");
			System.out.println("proofZ: "+proof.getZ().toString()+"\n");
			if (edlog.verifyEDlogProof(proof, h, a, b))
				System.out.println("good");
		} catch (ZkpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
		
		
	}

}