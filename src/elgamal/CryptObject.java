package elgamal;

import global.BigIntegerMod;

public class CryptObject {

	private BigIntegerMod text; // TODO - verify mod p everywhere
	private Ciphertext ciphertext;
	private BigIntegerMod r; // TODO - verify mod q everywhere
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

/*
	TODO
	public ZKP getZKP() {
		return zkp;
	}
*/

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
