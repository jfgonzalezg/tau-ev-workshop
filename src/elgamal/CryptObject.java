package elgamal;

import global.BigIntegerMod;

public class CryptObject {

	private BigIntegerMod text;
	private Ciphertext ciphertext;
	private BigIntegerMod r;
//	private ZKP zkp; TODO

	public CryptObject() {
		this.text = null;
		this.ciphertext = null;
		this.r = null;
	}

	public BigIntegerMod getText() {
		return text;
	}

	public Ciphertext getCiphertext() {
		return ciphertext;
	}

	public BigIntegerMod getR() {
		return r;
	}

	public BigIntegerMod getZKP() {
		// TODO - return zkp;
		return null;
	}

	protected void setText(BigIntegerMod text) {
		this.text = text;
	}

	protected void setCiphertext(Ciphertext ciphertext) {
		this.ciphertext = ciphertext;
	}

	protected void setR(BigIntegerMod r) {
		this.r = r;
	}
/*
	TODO
	public void setZKP(ZKP zkp) {
		this.zkp = zkp;
	}
*/
}
