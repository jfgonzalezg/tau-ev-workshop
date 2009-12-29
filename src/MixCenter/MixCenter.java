package MixCenter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
//import java.math.BigInteger;
import java.util.Random;

import tcp.Client;
import tcp.Server;
import zkp.GI.GIProof;
import elgamal.CryptObject;
import zkp.GI.IGI;
import elgamal.Ciphertext;
import global.BigIntegerMod;
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
	private int VOTERS_AMOUNT;
	private BigIntegerMod w,g;
	private BigInteger q,p;
	private static int num_of_centers_involved;
	
	
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
		pi = new int[VOTERS_AMOUNT]; 
		Random generator = new Random();
		for(int i=0;i<VOTERS_AMOUNT;i++) //initialize the permutation array.
		{
			pi[i]=i;
		}
		for(int i=0;i<VOTERS_AMOUNT-1;i++)//for all cells except the last one [0,n-2]
		{
			d=generator.nextInt(VOTERS_AMOUNT-i)+i; //generates a random number [i,n-1]
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
		ElGamal gamal=new ElGamal(w); 
		B=new CryptObject[VOTERS_AMOUNT];	
		for(int i=0;i<VOTERS_AMOUNT;i++) //create permutation according to pi[] and then - re-encrypt
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
		try 
		{
			if (outputFile == null)
			{
				outputFile = new BufferedWriter(new FileWriter(MC_RESULTS_FILE));
			}
			// print ZKP string
			outputFile.write("ZKP:\r\n" + message + "\r\n");
			
			// print A array
			outputFile.write("Recieved votes: \r\n");
			for (int i=0; i<VOTERS_AMOUNT; i++)
			{
				outputFile.write("A["+i+"] = "+A[i].toString()+" \r\n");
			}
			outputFile.write("\n");
			
			// print B array
			outputFile.write("Permutated and re-encrypted votes: \r\n");
			for (int i=0; i<VOTERS_AMOUNT; i++)
			{
				outputFile.write("B["+i+"] = "+B[i].getCiphertext().toString()+" \r\n");
			}
			outputFile.write("\r\n");
			
			outputFile.flush(); // TODO: is it possible the buffer will be full sooner?
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
	
	public String performZKP() 
	{
		// call ZKP function  
	//	GIProof zkp=createGIProof(A,B,pi,VOTERS_AMOUNT,w,g);	
	//	return zkp.toString();	
		return "";
	}
	
	
	//This function used for MixCenter users
	public boolean send_to_next_mix_center (){
		Ciphertext[] votes = new Ciphertext[B.length];
		for (int i=0; i<B.length; i++){
			votes[i] = B[i].getCiphertext();
		}
		return send_to_next_mix_center (votes, g, p, q, w, VOTERS_AMOUNT);
	}
	
	
	
	//The description of these functions is explained in the interface file
	public boolean send_to_next_mix_center (Ciphertext[ ] votes,
											BigIntegerMod G,
											BigInteger    P,
											BigInteger	  Q,
											BigIntegerMod W,
											int			  N)
	{
		System.out.println("DEBUG Entering send_to_next_mix_center");
		SentObject sent_object = new SentObject(votes, G, P, Q, W, N);
		int next_available_center = mix_center_id + 1;
		Client client = null;
		while (next_available_center < 12 ||                         /*Pret a vote is last center*/
			  (mix_center_id == 0 && next_available_center == 11)){  /*Pret a vote is trying to send to itself*/
			//NOTE: we assign modulo 11 for the case next_available_center == 11
			//so it is pret a vote
			client = new Client(	Consts.MIX_CENTERS_IP  [next_available_center % 11],
									Consts.MIX_CENTERS_PORT[next_available_center % 11],
									mix_center_id);
			if (client.canSend()){
				client.send(sent_object);
				Consts.log("Mix Center number "+mix_center_id+" sent data to Mix Center number" +
						next_available_center, Consts.DebugOutput.STDOUT);
				client.close();
				num_of_centers_involved++;
				return true;
			} else {
				Consts.log("ERROR: Mix Center number "+mix_center_id+" : Error while connecting to Mix Center number" +
						next_available_center, Consts.DebugOutput.STDERR);
			}
			next_available_center++;
		}
		client.close();
		return false;
	}

	public Ciphertext[ ] receive_from_prev_mix_center (){
		return receive_from_prev_mix_center(60/*minutes*/);
	}
	
	public Ciphertext[ ] receive_from_prev_mix_center (int timeout){
		
		System.out.println("DEBUG Entering  receive_from_prev_mix_center");
		Server 	server 					= new Server(Consts.MIX_CENTERS_PORT[mix_center_id]);
		Server.Message 	received_votes  = null;
		
		timeout *= 60; //convert to seconds
		while (timeout != 0 && received_votes == null){
			received_votes 	= server.getReceivedObject();
			if (received_votes != null){
				if (received_votes instanceof Server.Message){
					System.out.println("DEBUG "+received_votes.getMessage().getClass());
					SentObject recv_object = (SentObject) received_votes.getMessage();
					//MUST CHECK THAT THESE ARE NOT NULL!!!
					this.A = recv_object.get_votes_array();
					System.out.println("size"+A.length);
					this.g = recv_object.get_G();
					this.p = recv_object.get_P();
					this.q = recv_object.get_Q();
					this.w = recv_object.get_W();
					this.VOTERS_AMOUNT = recv_object.get_N();
					if (A.length != VOTERS_AMOUNT)
						System.out.println("ERROR number of votes in A is "+A.length+
								" while number of expected votes is "+VOTERS_AMOUNT);
					server.close();
					return A;
				} else {
					System.out.println(received_votes.getClass());
					Consts.log("ERROR: Mix Center number "+mix_center_id+" : received object is not of type Ciphertext[ ].",
								Consts.DebugOutput.STDERR);
				}
			}
			timeout -= 2*(Consts.CONNECTION_TIMEOUT);//The server waits 2 seconds every wait in socket
		}
		
		server.close();
		return null;
	}
	//IGELKA - MAKE SEND FUNCTION TO RETURN FALSE
	
	/*
	 * Get mix center id
	 */
	public int getId()
	{
		return this.mix_center_id;
	}
	
	/*
	 * Get the array A - encrypted votes before re-encryption
	 */
	public Ciphertext[] getArrayA()
	{
		return this.A;
	}

	/*
	 * Get the array A - encrypted votes before re-encryption
	 */
	public CryptObject[] getArrayB()
	{
		return this.B;
	}
	
	/*
	 * Get the permutation in specific index
	 */
	public int getPremutation(int index)
	{
		return this.pi[index];	
	}
	
	/*
	 * Get the permutation array
	 */
	public int[] getPremutationArray()
	{
		return this.pi;
	}
	
	/*
	 * Get the VOTERS_AMOUNT
	 */
	public int getVotersAmount()
	{
		return this.VOTERS_AMOUNT;
	}
	
	/*
	 * Set the VOTERS_AMOUNT
	 */
	protected void setVotersAmount(int amount)
	{
		this.VOTERS_AMOUNT = amount;
	}
	
	/*
	 * Set the A array
	 */
	protected void setArrayA(Ciphertext[] arr)
	{
		this.A = arr;
	}
	
	/*
	 * Set the B array
	 */
	protected void setArrayB(CryptObject[] arr)
	{
		this.B = arr;
	}
	
	public int get_num_of_centers_involved(){
		return num_of_centers_involved;
	}
}
