package MixCenter;

import java.math.BigInteger;
import java.util.Random;

import elgamal.Ciphertext;
import global.BigIntegerMod;
import global.Consts;

public class PretAVoteProcess {
	
	public static void basicTest(int numOfVotes)
	{
		IMixCenter preta_center = new MixCenter(0);
		
		//Create some votes
		Ciphertext[] votes= new Ciphertext[numOfVotes];
		BigInteger p = new BigInteger("23");
		BigInteger q = new BigInteger("11");
		BigIntegerMod g = new BigIntegerMod(new BigInteger("2"),p);
		BigIntegerMod w = new BigIntegerMod(new BigInteger("7"),p);
		for (int i = 0; i<numOfVotes; i++){
			BigIntegerMod a = new BigIntegerMod(new BigInteger("6"), p);
			BigIntegerMod b = new BigIntegerMod(new BigInteger("17"), p);
			//BigIntegerMod a = new BigIntegerMod(new BigInteger("9"+10*i),p);
			//BigIntegerMod b = new BigIntegerMod(new BigInteger("11"+10*i),p);
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
			System.out.println("Recieved q: " + preta_center.getQ());
			System.out.println("Recieved p: " + preta_center.getP());
			System.out.println("Recieved w: " + preta_center.getW());
			System.out.println("Recieved g: " + preta_center.getG());
			//Here we can print the votes
			System.out.println("num_of_centers_involved " + preta_center.get_num_of_centers_involved());
			
			//Check that got same values
			for (int i = 0; i<numOfVotes; i++){
				if (A[i].getA().getValue().compareTo(votes[i].getA().getValue()) == 0 && 
					A[i].getB().getValue().compareTo(votes[i].getB().getValue()) == 0){
					System.out.println("received data same as from sent i = "+i);
				} else {
					System.out.println("ERROR received data different from sent i = "+i);
				}
					
			}
		}
		
	}

	public static void main(String[] args) {
		
		/* test num 1 */
		if (args[0].equals("test1"))
		{
			basicTest(Consts.VOTERS_AMOUNT);
		}
		
		/* test num 2 */
		if (args[0].equals("test2"))
		{
			Random r = new Random();
			int n = r.nextInt(1000);
			basicTest(n);
			
		}
		
		/* test num 3 */
		if (args[0].equals("test3"))
		{
			basicTest(0);			
		}
		
		/* test num 4 */
		if (args[0].equals("test4"))
		{
			basicTest(1);			
		}
		
	}

}
