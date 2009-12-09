package global;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

public class BigIntegerMod implements Serializable, Comparable<BigIntegerMod> {

	private static final long serialVersionUID = 5269651515363770906L;
	private BigInteger value;
	private BigInteger mod;

	// TODO - NOTE - use this - global.Consts.CERTAINTY

	// create a random big integer mod
	public BigIntegerMod(BigInteger mod) {
		this.mod = mod;
		if (this.mod.compareTo(BigInteger.ZERO) <= 0) {
			throw new IllegalArgumentException("mod <= 0");
		}
		do {
			value = new BigInteger(this.mod.bitLength(), new Random());
		} while (value.compareTo(this.mod) >= 0);

	}

	// create a big integer mod using the given value
	public BigIntegerMod(BigInteger value, BigInteger mod) {
		this.value = value;
		this.mod = mod;
	}

	public BigInteger getValue() {
		return value;
	}

	public BigInteger getMod() {
		return mod;
	}

	public String toString() {
		return value.toString() + " (mod " + mod.toString() + ")";
	}

	private void verifyMod(BigIntegerMod comparator, String op) {
		verifyMod(this, comparator, op);
	}

	private static void verifyMod(BigIntegerMod first, BigIntegerMod second, String op) {
		// verify that comparator and this have the same mod
		if (first.mod.compareTo(second.mod) != 0) {
			throw new ArithmeticException(op + " (BigIntegerMod)s with different mods");
		}
	}

	public BigIntegerMod multiply(BigIntegerMod multiplier) {
		verifyMod(multiplier, "multiply");
		return new BigIntegerMod(mod, value.multiply(multiplier.value));
	}

	public BigIntegerMod add(BigIntegerMod adder) {
		verifyMod(adder, "add");
		return new BigIntegerMod(mod, value.add(adder.value));
	}

	public BigIntegerMod substract(BigIntegerMod substracter) {
		verifyMod(substracter, "substract");
		return new BigIntegerMod(mod, value.subtract(substracter.value));
	}

	public BigIntegerMod pow(BigInteger exponent) {
		return new BigIntegerMod(mod, value.modPow(exponent, mod));
	}

	public BigIntegerMod pow(BigIntegerMod exponent) {
		return new BigIntegerMod(mod, value.modPow(exponent.value, mod));
	}

	public BigIntegerMod inverse() {
		return new BigIntegerMod(mod, value.modInverse(mod));
	}

	public int compareTo(BigIntegerMod comparator) {
		verifyMod(comparator, "compare");
		return value.compareTo(comparator.value);
	}

	static public BigInteger BruteForceDLOG(BigIntegerMod dlogBase, BigIntegerMod dlogResult) {
		verifyMod(dlogBase, dlogResult, "DLOG");
		// TODO - implement
		return null;
	}

}
