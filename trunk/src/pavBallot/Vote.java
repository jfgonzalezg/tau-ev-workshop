package pavBallot;

import org.apache.commons.codec.binary.Base64;
import elgamal.*;
import global.BigIntegerMod;
import global.Consts;
import pav.*;
import zkp.ZkpException;
import zkp.OneOutOfL.OneOutOfLProof;

public class Vote {
	private String candidateName; 	// Naming scheme for candidates - Candidate<# in original list>
	private BigIntegerMod vote;		// single unencrypted vote
	private Ciphertext encVote;  	// single encrypted vote
	private BigIntegerMod seed;		// random seed used to encrypt the vote
	private int index;
	private OneOutOfLProof zkp;
	
	/**
	 * A constructor that initializes a single Vote.
	 * It gets a public key, the index of the vote, and the offset of the ballot.
	 * 
	 * It encrypts the vote, and stores both plaintext and ciphertext.
	 */
	public Vote(BigIntegerMod pKey,int index,int offset){
		this.index = index;
		
		vote = PAVShared.getPlaintextVote(index,offset);
		
		ElGamal elGamal = new ElGamal(pKey);
		CryptObject encObj = elGamal.encrypt(vote);
		
		encVote = encObj.getCiphertext();
		seed = encObj.getR();
		
		candidateName = "Candidate"+((index+offset) % Consts.PARTIES_AMOUNT);
		
		try{
			zkp = PAVShared.zkpMaker.createOneOutOfLProof(encObj, pKey, (index+offset) % Consts.PARTIES_AMOUNT);
		} catch (ZkpException ze){
			System.err.println("Failed To Create ZKP");
			System.err.println(ze);
			ze.printStackTrace();
			//TODO: Handle ZKP Exception.
		}
		
	}
	
	/**
	 * A getter for the index attribute of a single vote. 
	 * @return The index of this candidate in the original list of candidates.
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * @return The Ciphertext object representing the single encrypted vote.
	 */
	public Ciphertext getEncryptedVote(){
		return encVote;
	}

	/** 
	 * @return The name of the candidate
	 */
	public String getCandidateName() {
		return candidateName;
	}

	/**
	 * The method returns the encrypted vote as String in Base64.
	 * The encryption is two numbers separated by "::".
	 * The method requires "commons-codec-1.4.jar"
	 * @return The encryption in Base64
	 */
	public String getEncryptionBase64(){
		Base64 b64 = new Base64();
		String A64 = b64.encodeToString(getEncryptedVote().getA().getValue().toByteArray());
		String B64 = b64.encodeToString(getEncryptedVote().getB().getValue().toByteArray());
		return A64+"::"+B64;
	}
	
	
	
	public BigIntegerMod getSeed(){
		return seed;
	}
	
	/**
	 * The method returns the seed that used to encrypt the vote in Base64 String
	 */
	public String getSeedInBase64(){
		Base64 b64 = new Base64();
		return b64.encodeToString(seed.getValue().toByteArray());
	}
	
	public OneOutOfLProof getZKP(){
		return zkp;
	}
	
	
}
