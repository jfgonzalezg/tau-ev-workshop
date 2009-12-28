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
}
