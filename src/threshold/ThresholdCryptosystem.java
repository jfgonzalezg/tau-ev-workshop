package threshold;

import elgamal.Ciphertext;
import global.BigIntegerMod;
import global.Consts;
import global.Consts.DebugOutput;

public class ThresholdCryptosystem implements IThresholdCryptosystem {

	public ThresholdCryptosystem() {
		// TODO - see which global consts can and should be used in this class
		// for example - P,G,THRESHOLD,PARTIES_AMOUNT,PARTIES_MANAGER_PORT,CONNECTION_TIMEOUT etc
	}

	public BigIntegerMod generateMutualPublicKey() {
		// TODO - generate mutual public key
		return null;
	}

	public BigIntegerMod decryptMutually(Ciphertext ciphertext) {
		return decryptMutually(ciphertext, Consts.THRESHOLD);
	}

	public BigIntegerMod decryptMutually(Ciphertext ciphertext, int threshold) {
		if (threshold < Consts.THRESHOLD) {
			Consts.log("Trying to mutually decrypt using too few parties (" + threshold + " < " + Consts.THRESHOLD + ")", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			return null;
		}
		if (threshold > Consts.PARTIES_AMOUNT) {
			Consts.log("Trying to mutually decrypt using more parties than exist (" + threshold + " > " + Consts.PARTIES_AMOUNT + ")", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			return null;
		}
		// TODO randomly choose threshold parties
		// TODO decrypt the given ciphertext using the threshold parties' private keys
		return null;
	}

}
