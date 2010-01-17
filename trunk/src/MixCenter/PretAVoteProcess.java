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
			//Here we can print the votes
			System.out.println("num_of_centers_involved" + preta_center.get_num_of_centers_involved());
			
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
			basicTest(0);
			basicTest(1);
			for (int i=0; i<5; i++)
			{
				Random r = new Random();
				int n = r.nextInt(1000);
				basicTest(n);
			}
		}
		
		/* test num 3 */
		//if (args[0] == "test3") - one mc process is missing so skip of one mix is needed
		
		/* test num 4 */
		//if (args[0] == "test4") - more than one mc process is missing so skip of few mixes is needed
		
		/* test num 5 */
		//if (args[0] == "test5") - do not run any mc process, so skipping of all mc's is needed
		
	}

}
