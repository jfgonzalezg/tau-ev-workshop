package elgamal;

import global.BigIntegerMod;

public class Ciphertext {

	private BigIntegerMod ak; // TODO - verify mod p everywhere
	private BigIntegerMod k; // TODO - verify mod p everywhere

	public Ciphertext(BigIntegerMod ak, BigIntegerMod k) {
		this.ak = ak;
		this.k = k;
	}

	public BigIntegerMod getAk() {
		return ak;
	}

	public BigIntegerMod getK() {
		return k;
	}

	protected void setAk(BigIntegerMod ak) {
		this.ak = ak;
	}

	protected void setK(BigIntegerMod k) {
		this.k = k;
	}

}
