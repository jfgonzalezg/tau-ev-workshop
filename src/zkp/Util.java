package zkp;

import java.math.BigInteger;
import global.BigIntegerMod;
import java.util.Random;

public class Util {

	public static BigInteger createRandom(BigInteger modulo) {
		
		return new BigInteger(modulo.bitLength(), new Random()).mod(modulo);
	}

}