package zkp.EDlog;

import global.BigIntegerMod;
import java.math.BigInteger;
import global.Consts;
import zkp.ZkpException;
import zkp.Util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EDlog implements IEDlog {

	public EDlogProof createEDlogProof(BigIntegerMod h, BigIntegerMod a, BigIntegerMod b, 
			BigIntegerMod x) throws ZkpException {

		BigIntegerMod g = Consts.G;
		BigInteger q = Consts.q;	
				
		//check whether a=g^x and b=h^x
		if ((a.compareTo(g.pow(x)) != 0) && (b.compareTo(h.pow(x)) != 0))
			throw new ZkpException("Bad parameters for creating EDlog proof");
	

		// select r from Zq uniformly at random
		BigIntegerMod r = new BigIntegerMod(Util.createRandom(q), q);
		
		// Compute u=g^r and v=h^r
		BigIntegerMod u = g.pow(r);
		BigIntegerMod v = h.pow(r);

		// Compute the challenge using md5 hash function with a, b, u, v
		BigIntegerMod c = new BigIntegerMod(createEDlogHashChallenge(a, b, u, v, q), q);

		// Compute z=r+cx
		BigIntegerMod z = c.multiply(x);
		z = z.add(r);

		return new EDlogProof(u, v, z);
	}


	public boolean verifyEDlogProof(EDlogProof proof, BigIntegerMod h, BigIntegerMod a, 
			BigIntegerMod b) throws ZkpException
	{
		BigIntegerMod g = Consts.G;
		BigInteger q = Consts.q;

		BigIntegerMod u = proof.getU();
		BigIntegerMod v = proof.getV();
		BigIntegerMod z = proof.getZ();

		// Compute the challenge using md5 hash function with a, b, u, v
		BigIntegerMod c = new BigIntegerMod(createEDlogHashChallenge(a, b, u, v, q), q);
		
		// Check whether ua^c=g^z and vb^c=h^z
		if ((g.pow(z)).compareTo(u.multiply(a.pow(c))) != 0)
			return false;
		if ((h.pow(z)).compareTo(v.multiply(b.pow(c))) != 0)
			return false;
		// if true, accepts
		return true;
	}


	private static BigInteger createEDlogHashChallenge(BigIntegerMod a, BigIntegerMod b,
			BigIntegerMod u, BigIntegerMod v, BigInteger modulo) throws ZkpException
	{

		BigInteger challenge = null;
		//use MD5 hash function for challenge
		MessageDigest m;
		try
		{
			m = MessageDigest.getInstance("MD5");
			m.update(a.getValue().toByteArray());
			m.update(b.getValue().toByteArray());
			m.update(u.getValue().toByteArray());
			m.update(v.getValue().toByteArray());
			challenge = new BigInteger(m.digest());
		}
		catch (NoSuchAlgorithmException exception)
		{
			throw new ZkpException(exception.getMessage());
		}

		return challenge.mod(modulo);
	}

}
