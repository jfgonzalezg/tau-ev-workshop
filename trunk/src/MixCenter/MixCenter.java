package MixCenter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
//import java.math.BigInteger;
import java.util.Random;

import tcp.Client;
import tcp.Server;

import elgamal.CryptObject;
import elgamal.Ciphertext;
import global.Consts;
import elgamal.ElGamal;

public class MixCenter implements IMixCenter
{
	private static BufferedWriter outputFile = null;
	private static final String MC_RESULTS_FILE = "Mix_Center_Log.txt";
	private int mix_center_id;
	private Ciphertext[] A;
	private CryptObject[] B;
	private int[] pi;
	
	
	/* Constructor */
	public MixCenter (int mix_center_id)
	{
		this.mix_center_id = mix_center_id;
	}
	private MixCenter(){}//no default CTOR
	
	/*
	 * generates the array (pi) that represents the permutation that will be made.
	 */
	public void generatePermutation()
	{
		int d,temp;
		int n=Consts.VOTERS_AMOUNT;
		pi = new int[n]; 
		Random generator = new Random();
		for(int i=0;i<n;i++) //initialize the permutation array.
		{
			pi[i]=i;
		}
		for(int i=0;i<n-1;i++)//for all cells except the last one [0,n-2]
		{
			d=generator.nextInt(n-i)+i; //generates a random number [i,n-1]
			temp=pi[i];
			pi[i]=pi[d];
			pi[d]=temp;			
		}
	}
	/*
	 * permutate and re-encrypt A according to pi and using ElGamal module.
	 */
	public void PermutateAndReecncrypt()
	{
		ElGamal gamal=new ElGamal(Consts.publicKey); //TBD
		int n=Consts.VOTERS_AMOUNT;
		B=new CryptObject[n];	
		for(int i=0;i<n;i++) //create permutation according to pi[] and then - re-encrypt
		{
			B[i]=gamal.reencrypt(A[pi[i]]);			
		}
	}
	
	/*
	 * Print results to file
	 * Params: message - string to print
	 *         A - encrypted votes array before re-encryption and mixing
	 *         B - re-encrypted and mixed votes array
	 */
	public void printToFile(String message)
	{
		int n=Consts.VOTERS_AMOUNT;	//TODO: make it a field so we wont read it all the time?	
		
		try 
		{
			if (outputFile == null)
			{
				outputFile = new BufferedWriter(new FileWriter(MC_RESULTS_FILE));
			}
			// print ZKP string
			outputFile.write("ZKP:\n"+message + "\r\n");
			
			// print A array
			outputFile.write("Recieved votes:\n");
			for (int i=0; i<n; i++)
			{
				outputFile.write("A["+i+"] = "+A[i].toString()+"\n");
			}
			outputFile.write("\n");
			
			// print B array
			outputFile.write("Permutated and re-encrypted votes:\n");
			for (int i=0; i<n; i++)
			{
				outputFile.write("B["+i+"] = "+B[i].getCiphertext().toString()+"\n");
			}
			outputFile.write("\n");
			
			outputFile.flush(); // TODO: is it possible buffer will be full  sooner?
		}
		catch (IOException e) 
		{
			System.err.println(e);
		}	
	}
	
	/*
	 * Performs Zero Knowledge Proof
	 * Params: A - encrypted votes array before re-encryption and mixing
	 *         B - re-encrypted and mixed votes array
	 *         pi - new permutation array
	 * @return- a Ciphertext array made out of B.
	 */
	/*
	public Ciphertext[] performZKP(Ciphertext[] A, CryptObject[] B, int[] pi) 
	{
		String sZKP = " ";
		int n=Consts.VOTERS_AMOUNT;	//TODO: make it a field so we wont read it all the time?	
	//	BigIntegerMod[] R = new BigIntegerMod[n];
		// get W (the publicKey)
		
		// prepare R
	
		
		// call ZKP function  
//		sZKP = verifyGIProof(A, B, pi); TODO - bring this line back and fix.
		Ciphertext[] result=new Ciphertext[n];
		for (int i=0; i<n; i++)
		{
			result[i]=B[i].getCiphertext();
		}
		
		printToFile(sZKP, A, result); 
		
		return result;				
	}*/
	
	//The description of these functions is explained in the interface file
	public boolean send_to_next_mix_center (Ciphertext[ ] votes){
		
		Client client = new Client("localhost", 7000, mix_center_id);
		if (client.isConnected()){
			client.send(votes);
		} else {
			Consts.log("Mix Center number "+mix_center_id+" : Error while connecting to the socket.",
						Consts.DebugOutput.STDERR);
			return false;
		}
		client.close();
		return true;
	}

	public boolean receive_basics (Ciphertext[ ] votes){
		
		Server server 			= new Server(7000);
		Object received_votes 	= server.getReceivedObject();
		if (received_votes instanceof Ciphertext[]){
			votes	= (Ciphertext[ ]) received_votes;
		} else {
			Consts.log("Mix Center number "+mix_center_id+" : received object is not of type Ciphertext[ ].",
						Consts.DebugOutput.STDERR);
			return false;
		}
		
		
		
		server.close();
		return true;
	}
	

}
