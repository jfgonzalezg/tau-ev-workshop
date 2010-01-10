package zkp.OneOutOfL;

import elgamal.Ciphertext;
import elgamal.CryptObject;
import elgamal.ElGamal;
import global.BigIntegerMod;
import java.math.BigInteger;
import global.Consts;
import zkp.ZkpException;
import zkp.Util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class OneOutOfL implements IOneOutOfL {

	private ArrayList<Ciphertext> pairslist;

	public OneOutOfL(ArrayList<Ciphertext> pairslist) {
		this.pairslist = pairslist;
	}
	
	public OneOutOfLProof createOneOutOfLProof(CryptObject cryptobj, BigIntegerMod h, int t)
	throws ZkpException 
	{
		int l = pairslist.size(); //or Consts.PARTIES_AMOUNT
		BigIntegerMod g = Consts.G;
		BigInteger q = Consts.q;
		BigIntegerMod x = cryptobj.getCiphertext().getA();
		BigIntegerMod y = cryptobj.getCiphertext().getB();
		BigIntegerMod r = cryptobj.getR();
		
		//check t - index parameter for re-encrypted pair 
		if (t<0 || t>l)
			throw new ZkpException("index input t is <0 or >l");
		
		// check whether cryptobj is indeed a re-encryption using r of the pair in index t
				
		CryptObject test = new CryptObject();
		ElGamal gamal=new ElGamal(h);
		test = gamal.reencrypt(pairslist.get(t), cryptobj.getR());
		if ((test.getCiphertext().getA().compareTo(cryptobj.getCiphertext().getA()) !=0) || 
			(test.getCiphertext().getB().compareTo(cryptobj.getCiphertext().getB()) !=0))
			throw new ZkpException("input CryptObject is not a re-encryption of the CryptObject in index t of the list");
		
			
		
		// randomly select d_List and r_List with Zq numbers
		ArrayList<BigIntegerMod> d_List = new ArrayList<BigIntegerMod>();
		ArrayList<BigIntegerMod> r_List = new ArrayList<BigIntegerMod>();
		for (int i=0; i<l; i++) 
		{
			d_List.add(new BigIntegerMod(Util.createRandom(q), q));
			r_List.add(new BigIntegerMod(Util.createRandom(q), q));
		}
		
		// create a_List and b_list
		ArrayList<BigIntegerMod> a_List = new ArrayList<BigIntegerMod>();
		ArrayList<BigIntegerMod> b_List = new ArrayList<BigIntegerMod>();
		
		// let ai=((xi/x)^di)*g^ri and bi=((yi/y)^di)*h^ri
				
		for (int i=0; i<l; i++) {	
			BigIntegerMod x_i = pairslist.get(i).getA();
			BigIntegerMod y_i = pairslist.get(i).getB();
			BigIntegerMod d_i = d_List.get(i);
			BigIntegerMod r_i = r_List.get(i);
			BigIntegerMod a_i = null;
			BigIntegerMod b_i = null;
				
			try{
				a_i = (((x_i.multiply(x.inverse()))).pow(d_i)).multiply(g.pow(r_i));
				b_i = (((y_i.multiply(y.inverse()))).pow(d_i)).multiply(h.pow(r_i));
			}
			catch (ArithmeticException exception){
				throw new ZkpException(exception.getMessage());
			}			
			a_List.add(a_i);
			b_List.add(b_i);
			
		}
		// compute the challenge using md5 hash function with x,y,a_List,b_list
		BigIntegerMod c = new BigIntegerMod(createOneOutOfLHashChallenge(x,y,a_List,b_List,q), q);
		
		// compute w=(r*d_t)+r_t
		BigIntegerMod w = (r.multiply(d_List.get(t))).add(r_List.get(t));
		
		// change d_List[t]=c-sum(d_List[j!=t])
		BigIntegerMod sumD_List = d_List.get(0);
		for (int j=1; j<l; j++)
			sumD_List = sumD_List.add(d_List.get(j));
		sumD_List = sumD_List.substract(d_List.get(t));
		d_List.set(t,(c.substract(sumD_List)));
		
		// change r_List[t]=w-r*d_List[t]
		r_List.set(t,(w.substract(r.multiply(d_List.get(t)))));
		
		return new OneOutOfLProof(c, d_List, r_List);
	}

	public boolean verifyOneOutOfLProof(OneOutOfLProof proof, CryptObject cryptobj, BigIntegerMod h) 
			throws ZkpException 
	{
		int l = pairslist.size(); //or Consts.PARTIES_AMOUNT????????
		
		BigIntegerMod g = Consts.G;
		BigInteger q = Consts.q;	
		
		BigIntegerMod c = proof.getC();
		ArrayList<BigIntegerMod> d_List = proof.getD_List();
		ArrayList<BigIntegerMod> r_List = proof.getR_List();
		
		BigIntegerMod x = cryptobj.getCiphertext().getA();
		BigIntegerMod y = cryptobj.getCiphertext().getB();
		
		// Check whether c=sum(d_List[j])
		BigIntegerMod sumD_List = d_List.get(0);
		for (int j=1; j<l; j++)
			sumD_List = sumD_List.add(d_List.get(j));
		if (sumD_List.compareTo(c) != 0)
			return false;
		
		// create a_List and b_list
		ArrayList<BigIntegerMod> a_List = new ArrayList<BigIntegerMod>();
		ArrayList<BigIntegerMod> b_List = new ArrayList<BigIntegerMod>();
		
		// let ai=((xi/x)^di)*g^ri and bi=((yi/y)^di)*h^ri
		for (int i=0; i<l; i++) {	
			BigIntegerMod x_i = pairslist.get(i).getA();
			BigIntegerMod y_i = pairslist.get(i).getB();
			BigIntegerMod d_i = d_List.get(i);
			BigIntegerMod r_i = r_List.get(i);
			BigIntegerMod a_i = null;
			BigIntegerMod b_i = null;
				
			try{
				a_i = (((x_i.multiply(x.inverse()))).pow(d_i)).multiply(g.pow(r_i));
				b_i = (((y_i.multiply(y.inverse()))).pow(d_i)).multiply(h.pow(r_i));
			}
			catch (ArithmeticException exception){
				throw new ZkpException(exception.getMessage());
			}			
			a_List.add(a_i);
			b_List.add(b_i);
		}
		// compute the challenge using md5 hash function with x,y,a_List,b_list
		BigIntegerMod new_c = new BigIntegerMod(createOneOutOfLHashChallenge(x,y,a_List,b_List,q), q);
		if (new_c.compareTo(c) == 0)
			return true;
		else
			return false;
	}
	
	
	private static BigInteger createOneOutOfLHashChallenge(BigIntegerMod x, BigIntegerMod y, 
			ArrayList<BigIntegerMod> a_List, ArrayList<BigIntegerMod> b_List, BigInteger modulo) throws ZkpException
	{

		BigInteger challenge = null;
		//use MD5 hash function for challenge
		MessageDigest m;
		try
		{
			m = MessageDigest.getInstance("MD5");
			m.update(x.getValue().toByteArray());
			m.update(y.getValue().toByteArray());
			for (BigIntegerMod a_i : a_List)
				m.update(a_i.getValue().toByteArray());
			for (BigIntegerMod b_i : b_List)
				m.update(b_i.getValue().toByteArray());
			challenge = new BigInteger(m.digest());
		}
		catch (NoSuchAlgorithmException exception)
		{
			throw new ZkpException(exception.getMessage());
		}

		return challenge.mod(modulo);
	}
	
		
}

