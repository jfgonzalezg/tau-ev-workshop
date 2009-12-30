package MixCenter;

import java.io.*;
import java.text.*;
import java.util.*;


public class MixCenterProcess {

	int counter = 0;
	protected static String defaultLogFile = "c:\\MClog.txt";
    
    public static void write(String s) throws IOException {
    write(defaultLogFile, s);
    }
    
    public static void write(String f, String s) throws IOException {
    TimeZone tz = TimeZone.getTimeZone("GMT+2"); // or PST, MID, etc ...
    Date now = new Date();
    DateFormat df = new SimpleDateFormat ("yyyy.mm.dd hh:mm:ss ");
    df.setTimeZone(tz);
    String currentTime = df.format(now);
   
    FileWriter aWriter = new FileWriter(f, true);
    aWriter.write(currentTime + ":\r\n" + "-> " + s + "\r\n");
    aWriter.flush();
    aWriter.close();
}

	
	/**
	 * This main function is the Mix Center process and supposed to run on each Mix Center computer
	 * @param args (args[0] is the id of the Mix Center)
	 */
	public static void main(String[] args) {
		
		MixCenter MC;
		int id = Integer.parseInt(args[0]);
		String proof;
				
		try{
			MC = new MixCenter(id);
			write("Mix Center No." + id + " is working...");
			write("Waiting to recieve data from previus MC...");
			MC.receive_from_prev_mix_center();
			write("Finished recieving data from previus MC, generating new permutation...");
			MC.generatePermutation();
			write("Starting reencypting and permutating the data...");
			MC.PermutateAndReecncrypt();
			write("Finished reencypting and permutating the data, performing ZKP...");
			proof = new String(MC.performZKP());
			MC.printToFile(proof);
			write("ZKP is done, proof file was created and has valid data...");
			write("Starting to send data to the next MC...");
			MC.send_to_next_mix_center();
			write("Finished sending the data to the next MC, goodbye :-) ");
			write("Mix Center No." + id + " is down...\r\n\r\n");
		}catch (IOException e) 
		{
			System.err.println(e);
		}

	}

}
