package elgamal;

import global.BigIntegerMod;

public class Ciphertext {

	private BigIntegerMod a; // TODO - verify mod p everywhere
	private BigIntegerMod b; // represents k*m = (public_key^r)*m // TODO - verify mod p everywhere

	public Ciphertext(BigIntegerMod a, BigIntegerMod b) {
		this.a = a;
		this.b = b;
	}

	public BigIntegerMod getA() {
		return a;
	}

	public BigIntegerMod getB() {
		return b;
	}

	protected void setA(BigIntegerMod a) {
		this.a = a;
	}

	protected void setK(BigIntegerMod b) {
		this.b = b;
	}

	public String toString() {
		return ("<" + a.getValue() + "," + b.getValue() + ">");
	}

	public Ciphertext multiply(Ciphertext multiplier) {
		return multiply(this, multiplier);
	}

	public static Ciphertext multiply(Ciphertext multiplier1, Ciphertext multiplier2) {
		return new Ciphertext(multiplier1.a.multiply(multiplier2.a), multiplier1.b.multiply(multiplier2.b));
	}

}
