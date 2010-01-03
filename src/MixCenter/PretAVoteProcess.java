package MixCenter;

import java.math.BigInteger;

import elgamal.Ciphertext;
import global.BigIntegerMod;
import global.Consts;

public class PretAVoteProcess {

	public static void main(String[] args) {
		
		IMixCenter preta_center = new MixCenter(0);
		
		
		//Create some votes
		Ciphertext[] votes= new Ciphertext[Consts.VOTERS_AMOUNT];
		BigIntegerMod g = new BigIntegerMod(new BigInteger("3"),new BigInteger("4"));
		BigInteger p = new BigInteger("5");
		BigInteger q = new BigInteger("6");
		BigIntegerMod w = new BigIntegerMod(new BigInteger("7"),new BigInteger("8"));
		for (int i = 0; i<Consts.VOTERS_AMOUNT; i++){
			BigIntegerMod a = new BigIntegerMod(new BigInteger("9"+10*i),new BigInteger("10"+10*i));
			BigIntegerMod b = new BigIntegerMod(new BigInteger("11"+10*i),new BigInteger("12"+10*i));
			Ciphertext vote = new Ciphertext(a, b);
			votes[i] = vote;
		}
		System.out.println("Start pret a vote");
		
		if (preta_center.send_to_next_mix_center(votes, g, p, q, w, votes.length)){
			System.out.println("Sent to next available mix center");
		} else {
			System.out.println("Failed o sent");
			return;
		}
		
		Ciphertext[] A = preta_center.receive_from_prev_mix_center(3/*minutes timeout*/);
		if (A == null){
			System.out.println("Failed to receive data from last mix");
			return;
		} else {
			System.out.println("Got "+A.length+" votes");
			//Here we can print the votes
			System.out.println("num_of_centers_involved" + preta_center.get_num_of_centers_involved());
			
			//Check that got same values
			for (int i = 0; i<Consts.VOTERS_AMOUNT; i++){
				if (A[i].getA().getValue().compareTo(votes[i].getA().getValue()) == 0 && 
					A[i].getB().getValue().compareTo(votes[i].getB().getValue()) == 0){
					System.out.println("received data same as from sent i = "+i);
				} else {
					System.out.println("ERROR received data different from sent i = "+i);
				}
					
			}
		}
		
		
		
	}

}
