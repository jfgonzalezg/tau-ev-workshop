package MixCenter;

import java.math.BigInteger;

import elgamal.Ciphertext;
import global.BigIntegerMod;

public class PretAVoteProcess {

	public static void main(String[] args) {
		
		IMixCenter preta_center = new MixCenter(0);
		
		//Create some votes
		BigIntegerMod g = new BigIntegerMod(new BigInteger("3"),new BigInteger("4"));
		BigInteger p = new BigInteger("5");
		BigInteger q = new BigInteger("6");
		BigIntegerMod w = new BigIntegerMod(new BigInteger("7"),new BigInteger("8"));
		BigIntegerMod a = new BigIntegerMod(new BigInteger("9"),new BigInteger("10"));
		BigIntegerMod b = new BigIntegerMod(new BigInteger("11"),new BigInteger("12"));
		Ciphertext vote = new Ciphertext(a, b);
		Ciphertext[] votes= new Ciphertext[2];
		votes[0] = vote;
		votes[1] = vote;
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
		}
		
		
		
	}

}
