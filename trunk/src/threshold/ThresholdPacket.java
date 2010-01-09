package threshold;

import java.io.Serializable;
import java.math.BigInteger;

public class ThresholdPacket implements Serializable{

	private static final long serialVersionUID = -2030870442404871724L;
	public enum PacketType {
		BASIC_INFO,
		POLYNOM,
		ALL_POLYNOMS,
		CIPHERTEXT,
		NUMBER,
		END
	}

	public PacketType type; 
	public int source;
	public int dest;
	public int Parameters[];
	public BigInteger Data[][];
}
