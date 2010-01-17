package pav;

import MixCenter.*;
import threshold.center.ThresholdCryptosystem;
import threshold.parties.PartiesManager;

import java.math.BigInteger;
import java.util.Random;

import pavBallot.Ballot;
import pavUI.BatchUI;
import pavUI.InitialGUI;
import elgamal.*;
import global.*;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		////////////////////////
		// Options handling   //
		////////////////////////
		
		boolean guiFlag = true;
		boolean mixNetsFlag = false;
		boolean randomMode = false;
		
		for (String arg : args) {
			if (arg.equals("-gui")) guiFlag = true;
			else if (arg.equals("-mix-nets")) mixNetsFlag = true;
			else if (arg.equals("-random")) randomMode = true;
			else printUsage();
		}
		
		////////////////////////
		////////////////////////
		////////////////////////
		
		
		ThresholdCryptosystem tc = new ThresholdCryptosystem();
		new PartiesManager();
		
		try{
			System.out.println("KALEV: public key is "+tc.getMutualPublicKey());
			PAVShared.setPublicKey(tc.getMutualPublicKey());
		}catch (ElectionsSetUpException esue){
			System.err.println(esue);
			System.exit(-1);
		}
		
		try{
			PAVShared.initialize(mixNetsFlag,guiFlag);
		} catch (ElectionsSetUpException esue) {
			System.err.println(esue);
			System.exit(-1);
		}
		
		if (randomMode){
			Random rand = new Random();
			for (int i=0; i<Consts.VOTERS_AMOUNT; i++){
				PAVShared.addCastVote((new Ballot()).getVote(rand.nextInt(Consts.PARTIES_AMOUNT)), rand.nextInt(100000)+"");
			}
		}
		else if (PAVShared.inGUIMode()){
			InitialGUI.runGUI();
		} else {
			BatchUI.run();
		}
		
		PAVShared.terminate();
		
		BigIntegerMod voteSum;		
		
		if (PAVShared.inMixNetsMode()){
			IMixCenter mixNets = new MixCenter(0);
			
			Ciphertext[] mnRetVotes = new Ciphertext[PAVShared.getCastVotes().size()];
			
			Ciphertext[] castVotesArr = new Ciphertext[PAVShared.getCastVotes().size()];
			
			for (int i = 0; i < PAVShared.getCastVotes().size(); i++) {
				castVotesArr[i] =  PAVShared.getCastVotes().get(i);
			}
			
			boolean mnSend = mixNets.send_to_next_mix_center(castVotesArr, 
					global.Consts.G,
					global.Consts.p,
					global.Consts.q, 
					PAVShared.getPublicKey(),
					PAVShared.getCastVotes().size());
			
			/*boolean mnSend = mixNets.send_to_next_mix_center(((Ciphertext[]) PAVShared.getCastVotes().toArray()), 
					global.Consts.G,
					global.Consts.p,
					global.Consts.q, 
					PAVShared.getPublicKey(),
					PAVShared.getCastVotes().size());*/
			mnRetVotes = mixNets.receive_from_prev_mix_center();
			
			if (!mnSend){
				// TODO: handle error
			}
			
			voteSum = new BigIntegerMod(BigInteger.ZERO,Consts.p);
			
			for (Ciphertext ciphertext : mnRetVotes) {
				voteSum = voteSum.add(tc.decryptMutually(ciphertext));
			}
		} else { // Threshold mode
			voteSum = DiscreteLog.dLog(tc.decryptMutually(PAVShared.getVoteProduct()),PAVShared.getZ());
			if (voteSum == null){
				System.err.println("Discrete logarithm function failed. Exiting.");
				return;
			}
			
		}
		
		tc.close();
		
		PAVShared.announceResults(voteSum);
		
	}
	
	/**
	 * Prints usage information about this application to System.out
	 */
	public static void printUsage() {
		System.out.println("Usage: pav.Main [-gui] [-mix-nets]");
	}
}
