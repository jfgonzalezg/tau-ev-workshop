package MixCenter;


public class MixCenterProcess {


	/**
	 * This main function is the Mix Center process and supposed to run on each Mix Center computer
	 * @param args (args[0] is the id of the Mix Center)
	 */
	public static void main(String[] args) {
		
		MixCenter MC;
		int id = Integer.parseInt(args[0]);
		String proof;
		
		MC = new MixCenter(id);
		if (MC == null){
			MixCenter.write("Mix Center No." + id + " couldn't start up!!!- goodbye :-( \r\n\r\n", id);
			return;
		}
		MixCenter.write("Mix Center No." + id + " is working...", id);
		MixCenter.write("Waiting to recieve data from previus MC...", id);
		if (MC.receive_from_prev_mix_center() == null){
			MixCenter.write("Mix Center No." + id + " didn't succeed to receive data from previus MC, this MC will not take part of the elections... goodbye :-( \r\n\r\n", id);
			return;
		}
		MixCenter.write("Finished recieving data from previus MC, generating new permutation...", id);
		MC.generatePermutation();
		MixCenter.write("Starting reencypting and permutating the data...", id);
		MC.PermutateAndReecncrypt();
		MixCenter.write("Finished reencypting and permutating the data, performing ZKP...", id);
		proof = new String(MC.performZKP());
		boolean isZKPValid = true;
		if (proof == null){
			isZKPValid = false;
			MixCenter.write("Mix Center No." + id + " failed to get valid ZKP, this MC will not take part of the elections... goodbye :-( \r\n\r\n", id);
		}
		else{
			MC.printToFile(proof, isZKPValid);
			MixCenter.write("ZKP is done, proof file was created and has valid data...", id);
		} 
		MixCenter.write("Starting to send data to the next MC...", id);
		if (!MC.send_to_next_mix_center(isZKPValid))
			MixCenter.write("Mix Center No." + id + " didn't succeed to send data to one of the next MCs, the elections process failed!!! - goodbye :-( \r\n\r\n", id);
		else
			MixCenter.write("Finished sending the data to the next MC, goodbye :-) ", id);
		MixCenter.write("Mix Center No." + id + " is down...\r\n\r\n", id);
			

	}
	
	
}
