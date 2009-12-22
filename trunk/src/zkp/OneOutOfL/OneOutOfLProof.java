package zkp.OneOutOfL;

import java.util.List; 
import java.io.Serializable; 
import global.BigIntegerMod;


public class OneOutOfLProof implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3103238216603359488L;
	private BigIntegerMod c;
	private List<BigIntegerMod> d_List;
	private List<BigIntegerMod> r_List;
		
	public OneOutOfLProof(BigIntegerMod c, List<BigIntegerMod> d_List,
			List<BigIntegerMod> r_List) {
		
		this.c = c;
		this.d_List = d_List;
		this.r_List = r_List;
	}

	public BigIntegerMod getC()
	{
		return c;
	}

	public List<BigIntegerMod> getD_List() {
		return d_List;
	}

	public List<BigIntegerMod> getR_List()
	{
		return r_List;
	}
	
	//TODO toString()

}
