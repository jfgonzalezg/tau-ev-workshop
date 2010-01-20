package pav;

import MixCenter.*;
import threshold.center.ThresholdCryptosystem;
import threshold.parties.PartiesManager;

import java.math.BigInteger;
import java.util.Random;

import pavBallot.Ballot;
import pavUI.BatchUI;
import pavUI.InitialGUI;
import pavUI.UIModeEnum;
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
		
		//Default modes:
		UIModeEnum uIMode = UIModeEnum.GRAPHIC;
		boolean mixNetsFlag = false;

		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-mix-nets")) mixNetsFlag = true;
			else if (args[i].equals("-gui")) uIMode = UIModeEnum.GRAPHIC;
			else if(args[i].equals("-batch")) uIMode = UIModeEnum.BATCH;
			else if (args[i].equals("-random")) uIMode = UIModeEnum.RANDOM;
			else if (args[i].equals("-voters")) Consts.VOTERS_AMOUNT = Integer.parseInt(args[++i]);
			else if (args[i].equals("-parties")) Consts.PARTIES_AMOUNT = Integer.parseInt(args[++i]);
			else printUsage();
		}
		
		////////////////////////
		////////////////////////
		////////////////////////
		
		
		// Initialize threshold cryptosystem 
		ThresholdCryptosystem tc = new ThresholdCryptosystem();
		new PartiesManager();
		
		try{ // Try to generate public key
			System.out.println("PAV: public key is "+tc.getMutualPublicKey());
			PAVShared.setPublicKey(tc.getMutualPublicKey());
		}catch (ElectionsSetUpException esue){
			System.err.println(esue);
			System.exit(-1);
		}
		
		try{ // Initialized Pret-A-Voter
			PAVShared.initialize(mixNetsFlag,uIMode);
		} catch (ElectionsSetUpException esue) {
			System.err.println(esue);
			System.exit(-1);
		}
		
		// Run chosen user interface
		switch(PAVShared.getUIMode()){
		case RANDOM:
			Random rand = new Random();
			for (int i=0; i<Consts.VOTERS_AMOUNT; i++){
				PAVShared.addCastVote((new Ballot()).getVote(rand.nextInt(Consts.PARTIES_AMOUNT)), rand.nextInt(100000)+"");
			}
			break;
		case GRAPHIC:
			InitialGUI.runGUI();
			break;
		case BATCH:
			BatchUI.run();
			break;
		}
		

		PAVShared.terminate();
		
		BigIntegerMod voteSum;		
		
		if (PAVShared.inMixNetsMode()){
			// Initialize mix nets center 0
			IMixCenter mixNets = new MixCenter(0);
			
			Ciphertext[] mnRetVotes = new Ciphertext[PAVShared.getCastVotes().size()];
			
			Ciphertext[] castVotesArr = new Ciphertext[PAVShared.getCastVotes().size()];
			
			for (int i = 0; i < PAVShared.getCastVotes().size(); i++) {
				castVotesArr[i] =  PAVShared.getCastVotes().get(i);
			}
			
			// Begin mixing process
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
			
			if (!mnSend || (mnRetVotes == null)){
				System.err.println("Critical error during mix-nets phase. Exiting.");
				return;
			}
			
			voteSum = new BigIntegerMod(BigInteger.ZERO,Consts.p);
			
			// Decrypt mixed votes
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
		
		// Close threshold cryptosystem
		tc.close();
		
		// Announce the final elections' results 
		PAVShared.announceResults(voteSum);
		
	}
	
	/**
	 * Prints usage information about this application to System.out
	 */
	public static void printUsage() {
		System.out.println("Usage: pav.Main [-gui] [-batch] [-mix-nets] [-random]");
	}
}
