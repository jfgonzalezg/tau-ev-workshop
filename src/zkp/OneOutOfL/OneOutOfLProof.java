package zkp.OneOutOfL;

import java.util.ArrayList;
import java.io.Serializable; 
import global.BigIntegerMod;


public class OneOutOfLProof implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3103238216603359488L;
	private BigIntegerMod c;
	private ArrayList<BigIntegerMod> d_List;
	private ArrayList<BigIntegerMod> r_List;
		
	public OneOutOfLProof(BigIntegerMod c, ArrayList<BigIntegerMod> d_List,
			ArrayList<BigIntegerMod> r_List) 
	{
		this.c = c;
		this.d_List = d_List;
		this.r_List = r_List;
	}

	public BigIntegerMod getC()
	{
		return c;
	}

	public ArrayList<BigIntegerMod> getD_List() 
	{
		return d_List;
	}

	public ArrayList<BigIntegerMod> getR_List()
	{
		return r_List;
	}
	
	public String toString() 
	{
        String str=" Challenge: "+c.toString()+" d_List: "+d_List.toString()+" r_List: "+r_List.toString();
        return str;
    }

	
}
