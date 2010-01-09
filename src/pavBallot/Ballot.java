package pavBallot;

import global.BigIntegerMod;
import pav.*;

public class Ballot {
	private Vote[] votes = new Vote[global.Consts.PARTIES_AMOUNT]; // TODO change to public
	private int offset; // the offset between the original list of candidates, and this ballot
	
	public Ballot(BigIntegerMod pkey) {
		offset = PAVShared.getAndAdvanceOffset();
		for (int i=0; i<global.Consts.PARTIES_AMOUNT;i++){
			votes[i] = new Vote(pkey,i,offset);
		}
	}
	
	public Ballot(){
		this(PAVShared.getPublicKey());
	}

	public Vote getVote(int index) {
		return votes[index];
	}	
	
	public int getOffset() {
		return offset;
	}
}
