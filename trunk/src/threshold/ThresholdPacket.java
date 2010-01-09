package threshold;

import java.io.Serializable;
import java.math.BigInteger;

public class ThresholdPacket implements Serializable{

	private static final long serialVersionUID = 3142608504206907437L;
	public enum PacketType {
		BASIC_INFO,
		POLYNOM,
		ALL_POLYNOMS,
		CIPHERTEXT,
		NUMBER,
		END
	}

	public PacketType type; 
	public Integer source;
	public Integer dest;
	public int Parameters[];
	public BigInteger Data[][];
}
