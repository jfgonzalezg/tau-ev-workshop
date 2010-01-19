package MixCenter;


public class MixCenterProcess {


	/**
	 * This main function is the Mix Center process and supposed to run on each Mix Center computer
	 * @param args (args[0] is the id of the Mix Center)
	 */
	public static void main(String[] args) {
		
		MixCenter MC;
		int id = Integer.parseInt(args[0]);
		String proof = null;
		
		MC = new MixCenter(id);
		if (MC == null){
			MixCenter.write("Mix Center No." + id + " couldn't start up!!!- goodbye :-( \r\n\r\n", id, true);
			return;
		}
		MixCenter.write("Mix Center No." + id + " is working...", id, true);
		MixCenter.write("Waiting to recieve data from previous MC...", id, false);
		if (MC.receive_from_prev_mix_center() == null){
			MixCenter.write("Mix Center No." + id + " didn't succeed to receive data from previous MC, communication ends \r\n\r\n", id, false);
			return;
		}
		MixCenter.write("Finished recieving data from previous MC, generating new permutation...", id, false);
		MC.generatePermutation();
		MixCenter.write("Starting reencypting and permutating the data...", id, false);
		boolean isReencryptValid = MC.PermutateAndReecncrypt();
		boolean isValid = isReencryptValid; //this flag indicates several possible problems in the procedure
		if (isReencryptValid){ //in case reencryption went well
			MixCenter.write("Finished reencypting and permutating the data, performing ZKP...", id, false);
			
			proof = MC.performZKP();
			
			if (proof == null){
				isValid = false;
				MixCenter.write("Mix Center No." + id + " failed to get valid ZKP, this MC will not take part of the elections... goodbye :-( \r\n\r\n", id, false);
			}
			//in case ZKP proof was false
			if (proof != null && proof.equals("falseProof")){
				isValid = false;
				MixCenter.write("Mix Center No." + id + " ZKP proof wasn't correct, this MC will not take part of the elections... goodbye :-( \r\n\r\n", id, false);
			}
			else{
				MixCenter.write("ZKP is done and the data is valid...", id, false);
			} 
		}
		MixCenter.write("Starting to send data to the next MC...", id, false);
		if (!MC.send_to_next_mix_center(isValid))
			MixCenter.write("Mix Center No." + id + " didn't succeed to send data to one of the next MCs, the elections process failed!!! - goodbye :-( \r\n\r\n", id, false);
		else{
			MixCenter.write("Finished sending the data to the next MC, goodbye :-) ", id, false);
			if (isValid){ //in case zkp went well
				MixCenter.write("Mix Center No. " + id + " start printing ZKP", id, false);
				MC.printToFile(MC.getProof(), isValid);
			}
		}
		MixCenter.write("Mix Center No. " + id + " is down...\r\n\r\n", id, false);
		
		
			

	}
	
	
}
