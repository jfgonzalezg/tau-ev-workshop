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
	private int mix_center_id;
	private Ciphertext[] A;
	private CryptObject[] B;
	private int[] pi;
	private int VOTERS_AMOUNT;
	private BigIntegerMod w,g;
	private BigInteger q,p;
	private int num_of_centers_involved;
	
	
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
		
		//ALEX
		//for(int i=0;i<VOTERS_AMOUNT;i++) //create permutation according to pi[] and then - re-encrypt
		//{
		//	B[i]=gamal.reencrypt(A[pi[i]]);			
		//}
		for(int i=0;i<VOTERS_AMOUNT;i++) //create permutation according to pi[] and then - re-encrypt
		{
			B[i] = new CryptObject();
			B[i].setCiphertext(A[i]);
			B[i].setR(g);
		}
	}
	
	/*
	 * Print results to file
	 * Params: message - string to print
	 *         ZKPflag - marks whether to print the arrays or not
	 */
	public void printToFile(String message, boolean ZKPflag)
	{	
		try 
		{
			if (Consts.mcOutputFile == null)
			{
				Consts.mcOutputFile = new BufferedWriter(new FileWriter(Consts.MC_RESULTS_FILE));
			}
			// print ZKP string
			Consts.mcOutputFile.write("ZKP:\r\n" + message + "\r\n");
			
			if (ZKPflag)
			{
				// print A array
				Consts.mcOutputFile.write("Recieved votes: \r\n");
				for (int i=0; i<VOTERS_AMOUNT; i++)
				{
					Consts.mcOutputFile.write("A["+i+"] = "+A[i].toString()+" \r\n");
				}
				Consts.mcOutputFile.write("\n");
			
				// print B array
				Consts.mcOutputFile.write("Permutated and re-encrypted votes: \r\n");
				for (int i=0; i<VOTERS_AMOUNT; i++)
				{
					Consts.mcOutputFile.write("B["+i+"] = "+B[i].getCiphertext().toString()+" \r\n");
				}
				Consts.mcOutputFile.write("\r\n");
			}
			Consts.mcOutputFile.flush(); // TODO: is it possible the buffer will be full sooner?
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
		//check whether ZKP returned a NULL. 
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
		MixCenterProcess.write("DEBUG Entering send_to_next_mix_center mix_center_id"+mix_center_id);
		SentObject sent_object = new SentObject(votes, G, P, Q, W, N, num_of_centers_involved);
		int next_available_center = mix_center_id + 1;
		Client client = null;
		while (next_available_center < 12 ||                         /*Pret a vote is last center*/
			  (mix_center_id == 0 && next_available_center == 11)){  /*Pret a vote is trying to send to itself*/
			//NOTE: we assign modulo 11 for the case next_available_center == 11
			//so it is pret a vote
			client = new Client(	Consts.MIX_CENTERS_IP  [next_available_center % 11],
									Consts.MIX_CENTERS_PORT[next_available_center % 11],
									mix_center_id);
			MixCenterProcess.write("Mix Center number "+mix_center_id+" trying to send data to Mix Center number" +
					next_available_center%11);
			if (client.isConnected() && client.canSend()){
				if (client.send(sent_object) == false){
					MixCenterProcess.write("ERROR: Mix Center number "+mix_center_id+" : Error while sending to Mix Center number" +
							next_available_center%11);
				}
				else {
					MixCenterProcess.write("Mix Center number "+mix_center_id+" sent data to Mix Center number" +
							next_available_center%11);
					client.close();
					return true;
				}
			} else {
				MixCenterProcess.write("ERROR: Mix Center number "+mix_center_id+" : Error while connecting to Mix Center number" +
									next_available_center%11);
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
		
		MixCenterProcess.write("DEBUG Entering  receive_from_prev_mix_center mix_center_id"+mix_center_id);
		Server 	server 					= new Server(Consts.MIX_CENTERS_PORT[mix_center_id]);
		Server.Message 	received_votes  = null;
		
		timeout *= 60; //convert to seconds
		while (timeout != 0 && received_votes == null){
			received_votes 	= server.getReceivedObject(); //this line blocking for 2*(Consts.CONNECTION_TIMEOUT)
			if (received_votes != null){
				if (received_votes instanceof Server.Message){
					MixCenterProcess.write("DEBUG "+received_votes.getMessage().getClass());
					if (received_votes.getMessage() instanceof SentObject){
						SentObject recv_object = (SentObject) received_votes.getMessage();
						if (check_corected_recv_params(recv_object) == true){
							this.A = recv_object.get_votes_array();
							this.g = recv_object.get_G();
							this.p = recv_object.get_P();
							this.q = recv_object.get_Q();
							this.w = recv_object.get_W();
							this.VOTERS_AMOUNT = recv_object.get_N();
							this.num_of_centers_involved = recv_object.get_num_of_centers_involved();
							if (mix_center_id != 0)
								num_of_centers_involved++;
							if (A.length != VOTERS_AMOUNT){
								MixCenterProcess.write("ERROR number of votes in A is "+A.length+
										" while number of expected votes is "+VOTERS_AMOUNT);
								server.close();
								return null;
							}
							server.close();
							return A;
						}
					} else {//if (received_votes.getMessage() instanceof SentObject)
						MixCenterProcess.write(	"ERROR: Mix Center number "+mix_center_id+" : received object that is not of type " +
							"SentObject, received type is "+received_votes.getMessage().getClass()+" \nTrying" +
							"receive another message, timeout left = "+timeout+" seconds");
					}
				} else { //(received_votes instanceof Server.Message)
					MixCenterProcess.write(	"ERROR: Mix Center number "+mix_center_id+" : received object that is not of type " +
										"Server.Message, received type is "+received_votes.getClass()+" \nTrying" +
										"receive another message, timeout left = "+timeout+" seconds");
				}
			}//if (received_votes != null)
			timeout -= 2*(Consts.CONNECTION_TIMEOUT);//The server waits 2 seconds every wait in socket
		}
		
		server.close();
		return null;
	}
	//IGELKA - MAKE SEND FUNCTION TO RETURN FALSE
	
	/*
	 * this function checks that received parameters are not null and
	 * of right type 
	 */
	private boolean check_corected_recv_params(SentObject recv_obj){
		if (recv_obj.get_votes_array() == null ||
			recv_obj.get_G() == null ||
			recv_obj.get_P() == null ||
			recv_obj.get_Q() == null ||
			recv_obj.get_W() == null){
			MixCenterProcess.write("ERROR: Some of received parameters are null:\n" +
					"A "+ ((this.A == null) ? "is" : "is not") + " null\n"+
					"g "+ ((this.g == null) ? "is" : "is not") + " null\n"+
					"p "+ ((this.p == null) ? "is" : "is not") + " null\n"+
					"q "+ ((this.q == null) ? "is" : "is not") + " null\n"+
					"w "+ ((this.w == null) ? "is" : "is not") + " null\n");
			return false;
		}
		return true;
	}
	
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
