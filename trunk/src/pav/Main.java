package pav;

import MixCenter.*;
import threshold.center.ThresholdCryptosystem;
import threshold.parties.PartiesManager;

import java.math.BigInteger;
import pavBatchUI.BatchUI;
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
		
		boolean guiFlag = false;
		boolean mixNetsFlag = false;
		
		for (String arg : args) {
			if (arg == "-gui") guiFlag = true;
			else if (arg == "-mix-nets") mixNetsFlag = true;
			else printUsage();
		}
		
		////////////////////////
		////////////////////////
		////////////////////////
		
		
		ThresholdCryptosystem tc = new ThresholdCryptosystem();
		new PartiesManager();
		
		try{
			System.out.println("KALEV: public key is"+tc.getMutualPublicKey());
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
		
		if (PAVShared.inGUIMode()){
			// gui.run(); //TODO
		} else {
			BatchUI.run();
		}
		
		PAVShared.terminate();
		
		BigIntegerMod voteSum;		
		
		if (PAVShared.inMixNetsMode()){
			IMixCenter mixNets = new MixCenter(0);
			
			Ciphertext[] mnRetVotes = new Ciphertext[PAVShared.getCastVotes().size()];
			
			boolean mnSend = mixNets.send_to_next_mix_center(((Ciphertext[]) PAVShared.getCastVotes().toArray()), 
					global.Consts.G,
					global.Consts.p,
					global.Consts.q, 
					PAVShared.getPublicKey(),
					PAVShared.getCastVotes().size());
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
		
	
		
		PAVShared.announceResults(voteSum);
		
	}
	
	/**
	 * Prints usage information about this application to System.out
	 */
	public static void printUsage() {
		System.out.println("Usage: pav.Main [-gui] [-mix-nets]");
	}
}
