package MixCenter;

import java.io.*;
import java.text.*;
import java.util.*;


public class MixCenterProcess {

	protected static String defaultLogFilePath = "c:\\";
    
	public static void write(String s) {
    	final String defaultLogFile = defaultLogFilePath + "MC" +".txt";
    	write(defaultLogFile, s);
    }
	
    public static void write(String s, int id) {
    	final String defaultLogFile = defaultLogFilePath + "MC" + id +".txt";
    	write(defaultLogFile, s);
    }
    
    public static void write(String f, String s) {
	    TimeZone tz = TimeZone.getTimeZone("GMT+2"); // or PST, MID, etc ...
	    Date now = new Date();
	    DateFormat df = new SimpleDateFormat ("yyyy.mm.dd hh:mm:ss ");
	    df.setTimeZone(tz);
	    String currentTime = df.format(now); 
	    FileWriter aWriter;
	    try {
	    	aWriter = new FileWriter(f, true);
	    	aWriter.write(currentTime + ":\r\n" + "-> " + s + "\r\n");
	    	aWriter.flush();
	    	aWriter.close();
	    } catch (IOException e){ 
	    	System.err.println(e);
	    }
}

	
	/**
	 * This main function is the Mix Center process and supposed to run on each Mix Center computer
	 * @param args (args[0] is the id of the Mix Center)
	 */
	public static void main(String[] args) {
		
		MixCenter MC;
		int id = Integer.parseInt(args[0]);
		String proof;
		
		MC = new MixCenter(id);
		//TODO: is there a need to check that MC isn't null???
		write("Mix Center No." + id + " is working...", id);
		write("Waiting to recieve data from previus MC...", id);
		if (MC.receive_from_prev_mix_center() != null){
			write("Finished recieving data from previus MC, generating new permutation...", id);
			MC.generatePermutation();
			write("Starting reencypting and permutating the data...", id);
			MC.PermutateAndReecncrypt();
			write("Finished reencypting and permutating the data, performing ZKP...", id);
			proof = new String(MC.performZKP());
			if (proof != null){
				MC.printToFile(proof, true);
				write("ZKP is done, proof file was created and has valid data...", id);
				write("Starting to send data to the next MC...", id);
				if (!MC.send_to_next_mix_center())
					write("Mix Center No." + id + " didn't succeed to send data to one of the next MCs, the elections process failed!!! - goodbye :-( \r\n\r\n", id);
				write("Finished sending the data to the next MC, goodbye :-) ", id);
				write("Mix Center No." + id + " is down...\r\n\r\n", id);
			}
			else
				write("Mix Center No." + id + " failed to get valid ZKP, this MC will not take part of the elections... goodbye :-( \r\n\r\n", id);
		}
		else  
			write("Mix Center No." + id + " didn't succeed to receive data from previus MC, this MC will not take part of the elections... goodbye :-( \r\n\r\n", id);

	}

}
