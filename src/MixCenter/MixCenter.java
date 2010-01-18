package MixCenter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import tcp.Client;
import tcp.Server;
import zkp.GI.GIProof;
import elgamal.CryptObject;
import zkp.GI.IGI;
import zkp.GI.GI;
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
	protected static String defaultLogFilePath = "c:\\";
	
	
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
		//TODO - delete after debug
		System.out.println("p = " + p);
		System.out.println("g = " + g);
		System.out.println("w = " + w);
		ElGamal gamal=new ElGamal(p,g,w,null);
		B=new CryptObject[VOTERS_AMOUNT];	
		
		for(int i=0;i<VOTERS_AMOUNT;i++) //create permutation according to pi[] and then - re-encrypt
		{
			B[pi[i]]=gamal.reencrypt(A[i]);
			//TODO - debug. to delete or add to log
			if ((!(B[pi[i]].getCiphertext().getA().equals(A[i].getA().multiply(g.pow(B[pi[i]].getR())))))
				&& (!(B[pi[i]].getCiphertext().getB().equals(A[i].getB().multiply(w.pow(B[pi[i]].getR()))))))
			{
				System.out.println("wrong reencryption for i=" + i);
			}
		}
		
		
	/*	for(int i=0;i<VOTERS_AMOUNT;i++) //create permutation according to pi[] and then - re-encrypt
		{
			B[i] = new CryptObject();
			B[i].setCiphertext(A[i]);
			B[i].setR(g);
		}*/
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
	 
	 * @return- a zkp in its String notation.
	 */
	
	public String performZKP() 
	{
		// call ZKP function  
		IGI zkpGI = new GI();
		try
		{
		GIProof zkp=zkpGI.createGIProof(A,B,pi,VOTERS_AMOUNT,w,g);
		//check whether ZKP returned a NULL. 
		
		if (zkp==null)
			return null;
		else //in case the proof exists
		{
			if (zkpGI.verifyGIProof(zkp, this.getArrayA(), this.getArrayB(), this.w, this.g))
			{
				return zkp.toString();
			}
			else //in case ZKP returned wrong proof
				return "falseProof";
		}
		}//try
		catch (Exception e)
		{
			return null;
		}
		
	}
	
	/*
	 * This function is for MixCenter users
	 * Print comments to a log file
	 * Params: s - string (= comment) to print
	 *         id - the number of the MC related, relevant to the file name
	 */
    public static void write(String s, int id) 
    {
    	final String defaultLogFile = defaultLogFilePath + "MC" + id +".txt";
    	write(defaultLogFile, s);
    }
    
    /*
     * Print strings to a file
	 * Params: s - string to print
	 *         f - string represents full file name (includes the path)
	 */
    private static void write(String f, String s) 
    {
	    TimeZone tz = TimeZone.getTimeZone("GMT+2"); // or PST, MID, etc ...
	    Date now = new Date();
	    DateFormat df = new SimpleDateFormat ("yyyy.mm.dd hh:mm:ss ");
	    df.setTimeZone(tz);
	    String currentTime = df.format(now); 
	    FileWriter aWriter;
	    try 
	    {
	    	aWriter = new FileWriter(f, true);
	    	aWriter.write(currentTime + ":\r\n" + "-> " + s + "\r\n");
	    	aWriter.flush();
	    	aWriter.close();
	    } 
	    catch (IOException e)
	    { 
	    	System.err.println(e);
	    }
    }
	
	
	//This function used for MixCenter users
	public boolean send_to_next_mix_center (boolean isZKPValid){
		Ciphertext[] votes;
		if (isZKPValid){
			votes = new Ciphertext[B.length];
			for (int i=0; i<B.length; i++){
				votes[i] = B[i].getCiphertext();
			}
			//if (mix_center_id != 0) //mix center 0 doesn't suppose to get here
			num_of_centers_involved++;
		} else {
			votes = A;
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
		write("DEBUG Entering send_to_next_mix_center mix_center_id"+mix_center_id, this.getId());
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
			write("Mix Center number "+mix_center_id+" trying to send data to Mix Center number" +
					next_available_center%11, this.getId());
			if (client.isConnected() && client.canSend()){
				if (client.send(sent_object) == false){
					write("ERROR: Mix Center number "+mix_center_id+" : Error while sending to Mix Center number" +
							next_available_center%11, this.getId());
				}
				else {
					write("Mix Center number "+mix_center_id+" sent data to Mix Center number" +
							next_available_center%11, this.getId());
					client.close();
					return true;
				}
			} else {
				write("ERROR: Mix Center number "+mix_center_id+" : Error while connecting to Mix Center number" +
									next_available_center%11, this.getId());
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
		
		write("DEBUG Entering  receive_from_prev_mix_center mix_center_id"+mix_center_id, this.getId());
		Server 	server 					= new Server(Consts.MIX_CENTERS_PORT[mix_center_id]);
		Server.Message 	received_votes  = null;
		
		timeout *= 60; //convert to seconds
		while (timeout != 0 && received_votes == null){
			received_votes 	= server.getReceivedObject(); //this line blocking for 2*(Consts.CONNECTION_TIMEOUT)
			if (received_votes != null){
				if (received_votes instanceof Server.Message){
					write("DEBUG "+received_votes.getMessage().getClass(), this.getId());
					write("Received message from IP "+received_votes.getAddress().getHostAddress()+
							                 " PORT "+received_votes.getPort(),this.getId());
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
							if (A.length != VOTERS_AMOUNT){
								write("ERROR number of votes in A is "+A.length+
										" while number of expected votes is "+VOTERS_AMOUNT, this.getId());
								server.close();
								return null;
							}
							server.close();
							return A;
						} else { //if (check_corected_recv_params(recv_object) == true)
							this.printToFile("Global parameters were not recieve correctly: communication ends", false);
						} //TODO - don't we need to close communication here as well?
						
					} else {//if (received_votes.getMessage() instanceof SentObject)
						write(	"ERROR: Mix Center number "+mix_center_id+" : received object that is not of type " +
							"SentObject, received type is "+received_votes.getMessage().getClass()+" \nTrying" +
							"receive another message, timeout left = "+timeout+" seconds", this.getId());
					}
				} else { //(received_votes instanceof Server.Message)
					write(	"ERROR: Mix Center number "+mix_center_id+" : received object that is not of type " +
										"Server.Message, received type is "+received_votes.getClass()+" \nTrying" +
										"receive another message, timeout left = "+timeout+" seconds", this.getId());
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
			write("ERROR: Some of received parameters are null:\n" +
					"A "+ ((this.A == null) ? "is" : "is not") + " null\n"+
					"g "+ ((this.g == null) ? "is" : "is not") + " null\n"+
					"p "+ ((this.p == null) ? "is" : "is not") + " null\n"+
					"q "+ ((this.q == null) ? "is" : "is not") + " null\n"+
					"w "+ ((this.w == null) ? "is" : "is not") + " null\n", this.getId());
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
	
	/*
	 * Set q
	 */
	protected void setQ(BigInteger Q)
	{
		this.q = Q;
	}
	
	/*
	 * Set p
	 */
	protected void setP(BigInteger P)
	{
		this.p = P;
	}
	
	/*
	 * Set w
	 */
	protected void setW(BigIntegerMod W)
	{
		this.w = W;
	}
	
	/*
	 * Set g
	 */
	protected void setG(BigIntegerMod G)
	{
		this.g = G;
	}
	public int get_num_of_centers_involved(){
		return num_of_centers_involved;
	}
}
