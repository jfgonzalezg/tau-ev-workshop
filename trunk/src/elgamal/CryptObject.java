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
		// TODO - ZKP
	}

	public CryptObject(BigIntegerMod text, Ciphertext ciphertext, BigIntegerMod r) {
		this.text = text;
		this.ciphertext = ciphertext;
		this.r = r;
		// TODO - ZKP
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
	
	public String toString() {
		return "text: " + text + " ciphertext: " + ciphertext + " r: " + r;
	}

/*
	TODO
	public ZKP getZKP() {
		return zkp;
	}
*/

	public void setText(BigIntegerMod text) {
		this.text = text;
	}

	public void setCiphertext(Ciphertext ciphertext) {
		this.ciphertext = ciphertext;
	}

	public void setR(BigIntegerMod r) {
		this.r = r;
	}
/*
	TODO
	public void setZKP(ZKP zkp) {
		this.zkp = zkp;
	}
*/
}
