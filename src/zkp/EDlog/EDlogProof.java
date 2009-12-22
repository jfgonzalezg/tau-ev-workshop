package zkp.EDlog;

import java.io.Serializable; 
import global.BigIntegerMod;

public class EDlogProof implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9039925029066720425L;
	private BigIntegerMod u;
	private BigIntegerMod v;
	private BigIntegerMod z;
	
	public EDlogProof(BigIntegerMod u, BigIntegerMod v, BigIntegerMod z) {
		this.u = u;
		this.v = v;
		this.z = z;
	}

	public BigIntegerMod getU() {
		return u;
	}

	public BigIntegerMod getV() {
		return v;
	}

	public BigIntegerMod getZ() {
		return z;
	}

}
