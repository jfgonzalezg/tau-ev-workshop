package zkp.OneOutOfL;

import elgamal.Ciphertext;
import elgamal.CryptObject;
import elgamal.ElGamal;
import global.Consts;
import global.BigIntegerMod;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pav.PAVShared;

import zkp.ZkpException;

public class OneOutOfLTest {

	public static BigInteger q = new BigInteger("11");
	public static BigInteger p = new BigInteger("23");
	public static BigIntegerMod g = new BigIntegerMod(new BigInteger("2"),p);
	
	public static void main(String[] args)
	{
		
		ArrayList<Ciphertext> plist = new ArrayList<Ciphertext>();
		
		
		/*
		BigIntegerMod h = new BigIntegerMod(new BigInteger("7"),p);
		for (int i = 0; i<3; i++){
			BigIntegerMod a = new BigIntegerMod(new BigInteger("6"), p);
			BigIntegerMod b = new BigIntegerMod(new BigInteger("17"), p);
			//BigIntegerMod a = new BigIntegerMod(new BigInteger("9"+10*i),p);
			//BigIntegerMod b = new BigIntegerMod(new BigInteger("11"+10*i),p);
			Ciphertext vote = new Ciphertext(a, b);
			plist.add(i,vote);
		}
		ElGamal gamal=new ElGamal(p,g,h,null);
		CryptObject Cobj = new CryptObject();
		BigIntegerMod a = new BigIntegerMod(new BigInteger("6"), p);
		BigIntegerMod b = new BigIntegerMod(new BigInteger("17"), p);
		Ciphertext vote = new Ciphertext(a, b);
		//BigIntegerMod qtest = new BigIntegerMod(new BigInteger("1"), q);
		Cobj = gamal.reencrypt(vote);
		//BigIntegerMod checka = Cobj.getCiphertext().getA().multiply(g.pow(Cobj.getR().inverse()));
		//BigIntegerMod checkb = Cobj.getCiphertext().getB().multiply(h.pow(Cobj.getR().inverse()));
		*/
		
		BigIntegerMod h = new BigIntegerMod(new BigInteger("7"),p);
		ElGamal gamal=new ElGamal(p,g,h,null);
		CryptObject Cobj = new CryptObject();
		BigIntegerMod a = new BigIntegerMod(new BigInteger("6"), p);
		BigIntegerMod b = new BigIntegerMod(new BigInteger("17"), p);
		Ciphertext vote = new Ciphertext(a, b);
		//BigIntegerMod qtest = new BigIntegerMod(new BigInteger("1"), q);
		BigIntegerMod r = new BigIntegerMod(new BigInteger("4"), q);
		Cobj = gamal.reencrypt(vote,r);
		for (int i = 0; i<3; i++){
			//BigIntegerMod a = new BigIntegerMod(new BigInteger("6"), p);
			//BigIntegerMod b = new BigIntegerMod(new BigInteger("17"), p);
			//BigIntegerMod a = new BigIntegerMod(new BigInteger("9"+10*i),p);
			//BigIntegerMod b = new BigIntegerMod(new BigInteger("11"+10*i),p);
			//Ciphertext vote = new Ciphertext(a, b);
			//plist.add(i,vote);
			plist.add(i,Cobj.getCiphertext());
		}
		Cobj.setCiphertext(vote);
		
		//BigIntegerMod x = new BigIntegerMod(BigInteger.TEN, Consts.getQ());
		//BigIntegerMod h = Consts.getG().pow(Consts.getQ());
		/*
		ElGamal gamal=new ElGamal(h);
		ArrayList<Ciphertext> plist = new ArrayList(3);
		BigIntegerMod a = new BigIntegerMod(BigInteger.ONE, Consts.getP());
		//BigIntegerMod b = new BigIntegerMod(BigInteger.ONE, Consts.getP());
		//Ciphertext one = new Ciphertext(a,b);
		CryptObject Cone = new CryptObject();
		Cone = gamal.encrypt(a, new BigIntegerMod(BigInteger.ONE, Consts.q));
		plist.add(0,Cone.getCiphertext());
		BigIntegerMod c = new BigIntegerMod(BigInteger.ONE.add(BigInteger.ONE), Consts.getP());
		//BigIntegerMod d = new BigIntegerMod(BigInteger.ONE.add(BigInteger.ONE), Consts.getP());
		//Ciphertext two = new Ciphertext(c,d);
		CryptObject Ctwo = new CryptObject();
		Ctwo = gamal.encrypt(c, new BigIntegerMod(BigInteger.ONE, Consts.q));
		plist.add(1,Ctwo.getCiphertext());
		BigIntegerMod e = new BigIntegerMod(BigInteger.ONE.add(BigInteger.ONE.add(BigInteger.ONE)), Consts.getP());
		//BigIntegerMod f = new BigIntegerMod(BigInteger.ONE.add(BigInteger.ONE.add(BigInteger.ONE)), Consts.getP());
		//Ciphertext three = new Ciphertext(e,f);
		CryptObject Cthree = new CryptObject();
		Cthree = gamal.encrypt(e, new BigIntegerMod(BigInteger.ONE, Consts.q));
		plist.add(2,Cthree.getCiphertext());
		CryptObject Cobj = new CryptObject();
		Cobj = gamal.reencrypt(Ctwo.getCiphertext());
		*/
		/*
		BigIntegerMod z = Consts.G.pow(new BigIntegerMod(Consts.q));
		int bitsPerParty = (int) Math.ceil(Math.log(Consts.VOTERS_AMOUNT)/Math.log(2));
		Map<Integer, BigIntegerMod> plaintextVotes = new HashMap<Integer, BigIntegerMod>();
		Map<Integer, BigIntegerMod> expPlaintextVotes = new HashMap<Integer, BigIntegerMod>();
		for (int i = 0; i<3;i++){
		plaintextVotes.put(new Integer(i),
				new BigIntegerMod(BigInteger.ZERO.flipBit(i*bitsPerParty), Consts.getQ())); //TODO: make sure mod is correct
		expPlaintextVotes.put(new Integer(i), z.pow(plaintextVotes.get(new Integer(i))));
		}
		ArrayList<Ciphertext> plist = new ArrayList<Ciphertext>();
		
		ElGamal elGamal = new ElGamal(h);
		for (int i = 0; i < 3; i++) {
			CryptObject encObj = elGamal.encrypt(expPlaintextVotes.get(i), new BigIntegerMod(BigInteger.ONE, Consts.q));
			plist.add(encObj.getCiphertext());
		}
		CryptObject Cobj = new CryptObject();
		Cobj = elGamal.encrypt(expPlaintextVotes.get(1));
		
		*/
		
		
		System.out.println("h: "+h.toString()+"\n");
		System.out.println("plist: "+plist.toString()+"\n");
		System.out.println("cobj: "+Cobj.toString()+"\n");
		
		OneOutOfL oneofL = new OneOutOfL(plist);
			
		try {
			OneOutOfLProof proof = oneofL.createOneOutOfLProof(Cobj, h, 1);
			if (oneofL.verifyOneOutOfLProof(proof, Cobj.getCiphertext(), h))
				System.out.println("good");
			else
				System.out.println("bad");
		} catch (ZkpException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	 
		
		
	}

}