package MixCenter;

import elgamal.Ciphertext;

public interface IMixCenter 
{
	public void generatePermutation();
	public void PermutateAndReecncrypt();
	public void printToFile(String message);
	public String performZKP();
	
	/*	This function sends array of votes to mix_center.
   		The function is blocking.
    	Parameters: 
    		votes – array of votes of type vote_class.
   		Returns True if succeeded, else False is returned.*/
		public boolean send_to_next_mix_center (Ciphertext[ ] votes);

	
	/*	This function receives array of votes from mix_center.
   		The function is blocking.
    	Parameters: 
    		votes   – array of votes of type vote_class.
    	Returns True if succeeded, else False is returned. */
		public boolean receive_basics (Ciphertext[ ] votes);

}
