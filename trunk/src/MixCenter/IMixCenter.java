package MixCenter;

import java.math.BigInteger;

import elgamal.Ciphertext;

import global.BigIntegerMod;

public interface IMixCenter
{
	public void generatePermutation();
	/*
	 * @PRE generatePermutation()and receive_basics() where called earlier and completed successfully.
	 */
	public void PermutateAndReecncrypt();
	/*
	 * @PRE PermutateAndReecncrypt() was called earlier and completed successfully.
	 */
	public void printToFile(String message);
	/*
	 * @PRE PermutateAndReecncrypt() was called earlier and completed successfully.
	 */
	public String performZKP();
	
	/*	This function sends data to mix_center.
   		The function is blocking.
   		NOTE: If Mix Center(i+1) doesn't listen, the connection will
   			  fail immediately and this Mix Center will try to send
   			  to next available Mix Center(i+2), (i+3)...
    	Parameters: 
    		votes 	– array of votes of type vote_class.
    		G, P, Q - Encryption parameters.
    		W       - Public key.
    		N		- Number of votes.
   		Returns True if succeeded, else False is returned.*/
		public boolean send_to_next_mix_center (Ciphertext[ ] votes,
												BigIntegerMod G,
												BigInteger    P,
												BigInteger	  Q,
												BigIntegerMod W,
												int			  N);

	
	/*	This function receives array of votes from mix_center.
   		The function is blocking.
   		If nothing received within 1 hour the TCP connection will
   			be terminated.
    	Returns array of votes if succeeded, else null is returned. */
		public Ciphertext[ ] receive_from_prev_mix_center ();
		
		
	/*	This function receives array of votes from mix_center.
   		The function is blocking.
    	Parameters: 
    		timeout - timeout in MINUTES after which the TCP connection will
    				  be terminated
    	Returns array of votes if succeeded, else null is returned */
		public Ciphertext[ ] receive_from_prev_mix_center (int timeout);
	
	/* This function returns number of mix centers that were involved
	 * during the voting process.
	 */
		public int get_num_of_centers_involved();
}
