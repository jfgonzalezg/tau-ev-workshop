package globals;

import java.math.BigInteger;
import java.util.Random;

public class BigIntegerMod {

	// TODO - NOTE - use this - globals.GlobalConsts.CERTAINTY

	private BigInteger value;
	private BigInteger modSize;

	public BigIntegerMod(BigInteger modSize, Random random) {
		// TODO - implement
	}

	public BigIntegerMod(BigInteger modSize, BigInteger value) {
		// TODO - implement
	}

	public BigInteger getValue() {
		return value;
	}

	public BigInteger getModSize() {
		return modSize;
	}

	public String toString() {
		return value.toString() + " (mod " + modSize.toString() + ")";
	}

	private void verifyModSize(BigIntegerMod comparator, String op) {
		verifyModSize(this, comparator, op);
	}

	private static void verifyModSize(BigIntegerMod first, BigIntegerMod second, String op) {
		// verify that comparator and this have the same modSize
		if (first.getModSize().compareTo(second.getModSize()) != 0) {
			throw new ArithmeticException(op + " (BigIntegerMod)s with different (modSize)s");
		}
	}

	public BigIntegerMod multiply(BigIntegerMod multiplier) {
		verifyModSize(multiplier, "multiply");
		return new BigIntegerMod(modSize, value.multiply(multiplier.value));
	}

	public BigIntegerMod add(BigIntegerMod adder) {
		verifyModSize(adder, "add");
		return new BigIntegerMod(modSize, value.add(adder.value));
	}

	public BigIntegerMod substract(BigIntegerMod substracter) {
		verifyModSize(substracter, "substract");
		return new BigIntegerMod(modSize, value.subtract(substracter.value));
	}

	public BigIntegerMod pow(BigInteger exponent) {
		return new BigIntegerMod(modSize, value.modPow(exponent, modSize));
	}

	public BigIntegerMod pow(BigIntegerMod exponent) {
		return new BigIntegerMod(modSize, value.modPow(exponent.value, modSize));
	}

	public BigIntegerMod inverse() {
		return new BigIntegerMod(modSize, value.modInverse(modSize));
	}

	public int compareTo(BigIntegerMod comparator) {
		verifyModSize(comparator, "compare");
		return value.compareTo(comparator.value);
	}

	static public BigIntegerMod getModFieldGenerator(BigInteger modFieldPrime, int smallPrimeDevisor, BigInteger bigPrimeDevisor) {
		// TODO - implement
		return null;
	}

	static public BigInteger BruteForceDLOG(BigIntegerMod dlogBase, BigIntegerMod dlogResult) {
		verifyModSize(dlogBase, dlogResult, "DLOG");
		// TODO - implement
		return null;
	}

}
