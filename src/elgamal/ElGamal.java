package elgamal;

import global.BigIntegerMod;
import global.Consts;
import global.Consts.DebugOutput;

public class ElGamal implements IElGamal {

	private BigIntegerMod privateKey;
	private BigIntegerMod publicKey;

	public ElGamal(BigIntegerMod publicKey) {
		if (publicKey.getMod() != Consts.getQ()) { // TODO - verify it works well, and doesn't require getMod().compareTo(p)!=0
			Consts.log("Creating an ElGamal object where publicKey.getMod()!=Consts.getQ()", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
		}
		this.publicKey = publicKey;
		this.privateKey = null;
	}

	public ElGamal(BigIntegerMod publicKey, BigIntegerMod privateKey) {
		if (publicKey.getMod() != Consts.getQ()) { // TODO - verify it works well, and doesn't require getMod().compareTo(p)!=0
			Consts.log("Creating an ElGamal object where publicKey.getMod()!=Consts.getQ()", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
		}
		if (privateKey.getMod() != Consts.getQ()) { // TODO - verify it works well, and doesn't require getMod().compareTo(p)!=0
			Consts.log("Creating an ElGamal object where privateKey.getMod()!=Consts.getQ()", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
		}
		this.publicKey = publicKey;
		this.privateKey = privateKey;
		boolean match = true;
		// TODO - check if the keys match
		if (!match) {
			Consts.log("The given private key does not match the given public key", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
		}
	}

	public BigIntegerMod decrypt(Ciphertext ciphertext) {
		if (privateKey == null) {
			Consts.log("Trying to decrypt a ciphertext when the private key is null", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			return null;
		}
		// TODO decrypt
		return null;
	}

	public CryptObject encrypt(BigIntegerMod message) {
		return encrypt(message, new BigIntegerMod(Consts.getP()));
	}

	public CryptObject encrypt(BigIntegerMod message, BigIntegerMod r) {
		if (message.getMod() != Consts.getP()) { // TODO - verify it works well, and doesn't require getMod().compareTo(p)!=0
			Consts.log("Trying to encrypt a message where message.getMod()!=Consts.getP()", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			return null;
		}
		if (r.getMod() != Consts.getQ()) { // TODO - verify it works well, and doesn't require getMod().compareTo(p)!=0
			Consts.log("Trying to encrypt a message where r.getMod()!=Consts.getQ()", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			return null;
		}
		// TODO encrypt
		return null;
	}

	public CryptObject reencrypt(Ciphertext ciphertext) {
		return reencrypt(ciphertext, new BigIntegerMod(Consts.getQ()));
	}

	public CryptObject reencrypt(Ciphertext ciphertext, BigIntegerMod r) {
		if (r.getMod() != Consts.getQ()) { // TODO - verify it works well, and doesn't require getMod().compareTo(p)!=0
			Consts.log("Trying to reencrypt a message where r.getMod()!=Consts.getQ()", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			return null;
		}
		// TODO reencrypt
		return null;
	}

	public BigIntegerMod getPrivateKey() {
		return privateKey;
	}

	public BigIntegerMod getPublicKey() {
		return publicKey;
	}

}
