package zkp.EDlog;

import global.BigIntegerMod;
import zkp.ZkpException;

public interface IEDlog {

	/**
	 * Returns a zero knowledge proof that a=g^x and b=h^x 
	 * the ZKP will be verifiable without knowing x.
	 *
	 * @param h - a public key (Z*p) (for elgamal this is called C) 
	 * @param a - equals g^x (Z*p) (for elgamal this is called g^s)
	 * @param b - equals h^x (Z*p) (for elgamal this is called C^s)
	 * @param x - discrete log of a and b - secret key (Z*p) (for elgamal this is called s)
	 * @returns EDlogProof object
	 * @throws ZkpException
	 */
	public EDlogProof createEDlogProof(BigIntegerMod h, BigIntegerMod a, BigIntegerMod b, 
			BigIntegerMod x) throws ZkpException;

	/**
	 * Verifies using the ZKP that a=g^x and b=h^x without knowing x 
	 * 
	 * @param proof - ZKP of EDLog
	 * @param h - a public key (Z*p) (for elgamal this is called C)
	 * @param a - equals g^x (Z*p) (for elgamal this is called g^s)
	 * @param b - equals h^x (Z*p) (for elgamal this is called C^s)
	 * @returns true only if proof is correct.
	 * @throws ZkpException
	 */
	public boolean verifyEDlogProof(EDlogProof proof, BigIntegerMod h, BigIntegerMod a, 
			BigIntegerMod b) throws ZkpException;

}
