package pav;

import pavBallot.*;
import pavUI.UIModeEnum;
import global.*;
import elgamal.*;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import zkp.OneOutOfL.*;

public class PAVShared {
	
	/***********************************************************
	 * ******************************************************* *
	 * 				Static Constants						   *
	 * ******************************************************* *
	 ***********************************************************/

	private static int offset = 0;
	private static Map<Integer, BigIntegerMod> plaintextVotes = new HashMap<Integer, BigIntegerMod>();
	private static Map<Integer, BigIntegerMod> expPlaintextVotes = new HashMap<Integer, BigIntegerMod>();
	private static BigIntegerMod publicKey;
	
	private static boolean inMixNetsMode; // A flag for the Mix Nets mode. Default is threshold mode.  
	private static UIModeEnum uIMode; // A flag for gui mode. Default is batch mode.
	
	private static Ciphertext voteProduct = new Ciphertext(new BigIntegerMod(BigInteger.ONE, Consts.p),
			new BigIntegerMod(BigInteger.ONE, Consts.p)); // The product of all votes - used only in threshold mode.
	private static final BigIntegerMod z = Consts.G.pow(new BigIntegerMod(Consts.q)); // The base for the vote exponent (vote will be z^(vote)) - used only in threshold mode.

	private static List <Ciphertext> castVotes = new ArrayList<Ciphertext>(); // A list of all cast votes - used only in MixNets mode. 
	public static int numberOfCastVotes = 0;
	
	public static IOneOutOfL zkpMaker;
	
	private static String WBBPath = "WBB.xml"; // WBB file path
	private static FileWriter wbbWriter = null;
	
	/***********************************************************
	 * ******************************************************* *
	 * 				Initialization and Termination			   *
	 * ******************************************************* *
	 ***********************************************************/
	
	/**
	 * a method that initializes the pret-a-voter consts.
	 * it creates a mapping between an integer identifier and a valid vote.
	 * @throws UnsportedElectionSizeException in case the number of 
	 * voters and parties make the ballot larger than the elections can support
	 */
	public static void initialize(boolean inMixNetsMode, UIModeEnum uIMode) throws ElectionsSetUpException{
		PAVShared.inMixNetsMode = inMixNetsMode;
		PAVShared.uIMode = uIMode;
		
		int bitsPerParty = (int) Math.ceil(Math.log(Consts.VOTERS_AMOUNT)/Math.log(2));
		
		// Check that a vote can be represented using given bits amount 
		if (bitsPerParty*Consts.PARTIES_AMOUNT > Consts.BITS_AMOUNT) 
			throw new ElectionsSetUpException("Unsupported elections size:\n\tParties:"+
					Consts.PARTIES_AMOUNT+
					"\n\tVoters:"+
					Consts.VOTERS_AMOUNT);
		
		// Checks that largest sum of votes is in Zq or Zp (w/o modulo) according to the work mode.
		if (inMixNetsMode){
			if ((new BigInteger("2")).pow(Consts.PARTIES_AMOUNT).multiply(new BigInteger(Consts.VOTERS_AMOUNT+"")).compareTo(Consts.p) != -1) 
				throw new ElectionsSetUpException("Unsupported elections size:\n\tParties:"+
						Consts.PARTIES_AMOUNT+
						"\n\tVoters:"+
						Consts.VOTERS_AMOUNT);
		} else { // Threshold mode
			if ((new BigInteger("2")).pow(Consts.PARTIES_AMOUNT).multiply(new BigInteger(Consts.VOTERS_AMOUNT+"")).compareTo(Consts.q) != -1) 
				throw new ElectionsSetUpException("Unsupported elections size:\n\tParties:"+
						Consts.PARTIES_AMOUNT+
						"\n\tVoters:"+
						Consts.VOTERS_AMOUNT);			
		}
		
		for (int i = 0; i<Consts.PARTIES_AMOUNT;i++){
			BigInteger b =  BigInteger.ZERO;
			
			if (inMixNetsMode){
				plaintextVotes.put(new Integer(i),
						new BigIntegerMod(b.flipBit(i*bitsPerParty), Consts.getP())); //TODO: make sure mod is correct
			} else { // Threshold mode
				plaintextVotes.put(new Integer(i),
						new BigIntegerMod(b.flipBit(i*bitsPerParty), Consts.getQ())); //TODO: make sure mod is correct
				expPlaintextVotes.put(new Integer(i), z.pow(plaintextVotes.get(new Integer(i))));
			}
		}
		
		// Start the WBB file
		try{
			wbbWriter = new FileWriter(WBBPath,false);
			wbbWriter.write("<?xml version=\"1.0\"?>\n");
			wbbWriter.write("<WBB>\n");
			wbbWriter.write("\t<P>"+toBase64(global.Consts.p)+"</P>\n");
			wbbWriter.write("\t<Q>"+toBase64(global.Consts.q)+"</Q>\n");
			wbbWriter.write("\t<G>"+toBase64(global.Consts.G)+"</G>\n");
			wbbWriter.close();
		} catch(IOException ioe){
			System.err.println("Failed when writing to WBB file");
			ioe.printStackTrace();
		}
		
		
		// instantiate the ZKP Maker
		ArrayList<Ciphertext> pairsListForZKP = new ArrayList<Ciphertext>();
		ElGamal elGamal = new ElGamal(publicKey);
		for (int i = 0; i < global.Consts.PARTIES_AMOUNT; i++) {
			CryptObject encObj = elGamal.encrypt(getPlaintextVote(i), new BigIntegerMod(BigInteger.ZERO, Consts.q));
			pairsListForZKP.add(encObj.getCiphertext());
		}
		zkpMaker = new OneOutOfL(pairsListForZKP);
	}
	
	/**
	 * This methods adds closing tag to WBB XML file.
	 */
	public static void terminate(){
		try{
			wbbWriter = new FileWriter(WBBPath,true);
			wbbWriter.write("</WBB>");
			wbbWriter.close();
		} catch(IOException ioe){
			System.err.println("Failed when writing to WBB file");
			ioe.printStackTrace();
		}
	}
	
	
	
	/***********************************************************
	 * ******************************************************* *
	 * 				Getters	and Setters						   *
	 * ******************************************************* *
	 ***********************************************************/
	
	/**
	 * a method that returns a valid vote to the candidate who is the in the index place in the original list of candidates (disregarding the ballot offset). 
	 * @param index - the number of the candidate in the list of plaintextVotes.
	 * @return the vote for the candidate.
	 */
	public static BigIntegerMod getPlaintextVote(int index){
		return inMixNetsMode ?  plaintextVotes.get(index) : expPlaintextVotes.get(index);	
	}
	
	/**
	 * a method that returns a valid vote for the candidate in the place index in the ballot (taking into account the ballot offset).
	 * @param index - the index of the candidate in the ballot.
	 * @param offset - the offset of the ballot.
	 * @return the vote for the candidate.
	 */
	public static BigIntegerMod getPlaintextVote (int index, int offset){
		return getPlaintextVote((index+offset) % Consts.PARTIES_AMOUNT);
	}
	
	public static Map<Integer, BigIntegerMod> getPlaintextVotes() {
		return plaintextVotes;
	}

	public static Map<Integer, BigIntegerMod> getExpPlaintextVotes() {
		return expPlaintextVotes;
	}

	
	/**
	 * a method that advances the ballot offset (modulo number of parties) and returns that offset
	 * @return current ballot offset
	 */
	public static int getAndAdvanceOffset(){
		offset = (offset + 1) % Consts.PARTIES_AMOUNT;
		
		return offset;
	}
	
	/**
	 * A getter for the public key used in the elections.
	 * @return The public key used in the elections, a BigIntegerMod.
	 */
	public static BigIntegerMod getPublicKey() {
		return publicKey;
	}

	/**
	 * A setter for the public key to be used in the elections.
	 * Will be set only once, trying to set it will result in an error.
	 * @param publicKey
	 * @throws ElectionsSetUpExceptions when trying to set the public key more than once. 
	 */
	public static void setPublicKey(BigIntegerMod publicKey) throws ElectionsSetUpException {
		if (PAVShared.publicKey == null){
			PAVShared.publicKey = publicKey;
		} else {
			throw new ElectionsSetUpException("Tried setting the public key more than once");
		}
	}
	
	public static boolean inMixNetsMode(){
		return inMixNetsMode;
	}
	
	public static UIModeEnum getUIMode(){
		return uIMode;
	}
	
	/**
	 * A method that adds a single cast vote to the system.
	 * Works in two distinct modes for both MixNets and Threshold modes of operation, depending on which system was chosen at initialization.
	 * Also writes the vote and voter ID to the WBB. 
	 * @param vote The vote for the candidate that the voter chose.
	 * @param voterID The ID of the voter that cast this vote.
	 */
	public static void addCastVote(Vote vote, String voterID){
		numberOfCastVotes++;
		
		appendVoteToWBB(vote, voterID);
		
		if (inMixNetsMode) {
			castVotes.add(vote.getEncryptedVote());
		}else {
			voteProduct = voteProduct.multiply(vote.getEncryptedVote());
		}
	}

	public static List<Ciphertext> getCastVotes() {
		return castVotes;
	}

	public static Ciphertext getVoteProduct() {
		return voteProduct;
	}

	public static BigIntegerMod getZ() {
		return z;
	}
	
	
	
	/***********************************************************
	 * ******************************************************* *
	 * 				Results Announcement					   *
	 * ******************************************************* *
	 ***********************************************************/

	/**
	 * The function prints the results of the elections
	 * @param results: the sum of all votes
	 */
	public static void announceResults(BigIntegerMod results){
		int [] resultArray = new int[Consts.PARTIES_AMOUNT];
		BigInteger sumOfVotes = results.getValue();

		//Convert from one BigIntegerMod to an array of votes
		for(int i=0; i<resultArray.length; i++){
			resultArray[i] = sumOfVotes.mod(getPlaintextVotes().get(1).getValue()).intValue();
			sumOfVotes = sumOfVotes.divide(getPlaintextVotes().get(1).getValue());
		}

		//Find the max in resultArray, and if there is a tie
		boolean isTie = false;
		int maxVotes = 0;
		int winnerIndex = 0;
		for(int i=0; i<resultArray.length; i++){
			if (resultArray[i] > maxVotes){
				maxVotes = resultArray[i];
				isTie = false;
				winnerIndex = i;
			}else if (resultArray[i] == maxVotes)
				isTie = true;
		}
		
		//Announce results
		String message = "";
		if (isTie){
			int numberOfTie = 0;
			message += "There is a tie of "+maxVotes+" votes between:\n";
			for(int i=0;i<resultArray.length; i++)
				if (resultArray[i] == maxVotes){
					numberOfTie++;
					message += "Candidate"+i+"\n";
				}
			if (numberOfTie != resultArray.length) message +="Other results are:\n";
			for(int i=0; i<resultArray.length; i++)
				if (resultArray[i]<maxVotes)
					message +="Candidate"+i+": "+resultArray[i]+" votes.\n";
		}else{
			message += "The winner is Candidate"+winnerIndex+" with "+resultArray[winnerIndex]+" votes.\n";
			message += "Other results are:\n";
			for(int i=0; i<resultArray.length; i++)
				if (resultArray[i]<maxVotes)
					message += "Candidate"+i+": "+resultArray[i]+" votes.\n";
		}	
		

		MessageBox emptyName = new MessageBox(new Shell(Display.getDefault()), SWT.ICON_INFORMATION | SWT.OK);
		emptyName.setMessage(message);
		emptyName.setText("Election Results");
		if (emptyName.open() == SWT.OK) return;
		
	}
	
	
	
	/***********************************************************
	 * ******************************************************* *
	 * 				WBB Handling 							   *
	 * ******************************************************* *
	 ***********************************************************/
	
	/**
	 * Converts a BigInteger to a string, encoded with Base64.
	 */
	public static String toBase64(BigInteger b){
		Base64 b64 = new Base64();
		return b64.encodeToString(b.toByteArray());
	}
	
	/**
	 * Converts a BigIntegerMod's value part to a string, encoded with Base64.
	 */
	public static String toBase64(BigIntegerMod b){
		return toBase64(b.getValue());
	}
		
	/**
	 * Adds current vote to WBB.
	 * @param vote - the cast vote.
	 * @param voterID - the ID of the voter who cast the vote.
	 */
	public static void appendVoteToWBB(Vote vote, String voterID){
		
		String voteBuffer = "";
		
		voteBuffer+="\t<vote>\n";
		voteBuffer+="\t\t<voterID>"+voterID+"</voterID>\n";
		voteBuffer+="\t\t<encryptedVote>"+vote.getEncryptionBase64()+"</encryptedVote>\n";
		voteBuffer+="\t\t<oneOutOfLProof>\n";
		voteBuffer+="\t\t\t<C>"+toBase64(vote.getZKP().getC())+"</C>\t";
		voteBuffer+="\t\t\t<DList>\n\t\t\t";
		int i = 0;
		for(BigIntegerMod b : vote.getZKP().getD_List()){
			voteBuffer+="<DElem"+i+">";
			voteBuffer+=toBase64(b);
			voteBuffer+="</DElem"+i+">";			
			i++;
		}
		voteBuffer+="\n\t\t\t</DList>\n";
		voteBuffer+="\t\t\t<RList>\n\t\t\t";
		i=0;
		for(BigIntegerMod b : vote.getZKP().getR_List()){
			voteBuffer+="<RElem"+i+">";
			voteBuffer+=toBase64(b);
			voteBuffer+="</RElem"+i+">";
			i++;
		}
		voteBuffer+="\n\t\t\t</RList>\n";
		voteBuffer+="\t\t</oneOutOfLProof>\n";
		voteBuffer+="\t</vote>\n";
		
		try{
			wbbWriter = new FileWriter(WBBPath,true);
			wbbWriter.write(voteBuffer);
			wbbWriter.close();
		}catch (IOException ioe){
			System.err.println("Failed when writing to WBB file");
			ioe.printStackTrace();
		}		
	}
}
