package MixCenter;

import elgamal.Ciphertext;

public interface IMixCenter {
	/*	This function sends array of votes to mix_center.
   		The function is blocking.
    	Parameters: 
    		votes – array of votes of type vote_class.
			dest_IP – IP address of next mix_center.
			dest_port – port of next mix_center.
   		Returns True if succeeded, else False is returned.*/
		public boolean send_to_next_mix_center (Ciphertext[ ] votes,
												String dest_IP, 
												int dest_port);

	
	/*	This function receives array of votes from mix_center.
   		The function is blocking.
    	Parameters: listening_port – port on which the socket will listen.	  
    	Returns True if succeeded, else False is returned. */
		public boolean receive_basics (	Ciphertext[ ] votes, 
										int listening_port);

}
