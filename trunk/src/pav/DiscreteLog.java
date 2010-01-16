package pav;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import global.BigIntegerMod;
import global.Consts;

public class DiscreteLog {
	//TODO return to code.
//	private static int nVoters = PAVShared.numberOfCastVotes; 
//	private static int nParties = Consts.PARTIES_AMOUNT;

	private static int nVoters = 40; 
	private static int nParties = 4;
	
	private static int[] indices = new int[nParties -1];
	private static Map<Integer, BigInteger> votes = new HashMap<Integer, BigInteger>(); //Plain text votes
	private static BigInteger x; 
	private static BigInteger mod;
	private static BigInteger base;
	
	private static boolean foundLog;
	
	private static int i;
	private static BigInteger sumOfVotes;
	private static BigInteger result;
	
	/**
	 * Finds the discrete logarithm of xArg in base baseArg, using brute force  
	 * @param xArg - the argument for the log function
	 * @param baseArg - the base of the log
	 * @return - the log of xArg of base baseArg in finite field Z(xArg.getMod())
	 */
	public static BigIntegerMod dLog(BigIntegerMod xArg, BigIntegerMod baseArg){
		initializeVotes();
		
		foundLog = false;
		
		x = xArg.getValue();
		mod = xArg.getMod();
		base = baseArg.getValue();	

		looper(nParties - 1, -1);

		if (foundLog) return new BigIntegerMod(sumOfVotes, mod);
		else return null;
	}
	
	/**
	 * A "helper" function for dLog that only test exponents that represent a valid result of election votes sum 
	 * @param depth - how deep is the nesting of the "for" loops
	 * @param parentCurIndex - the current state of the calling function's index
	 */
	private static void looper(int depth, int parentCurIndex){
		if (!foundLog){
			if (depth != 0){
				for(indices[nParties-depth-1] = parentCurIndex + 1; indices[nParties-depth-1] < nParties+nVoters-1; indices[nParties-depth-1]++ ){
					looper(depth - 1, indices[nParties-depth-1]);
				}
			} else {// In the most inner loop
				sumOfVotes = BigInteger.ZERO;
				
				sumOfVotes = sumOfVotes.add((new BigInteger(indices[0]+"")).multiply(votes.get(new Integer(0)))); // first party votes
				for(i = 1; i < indices.length; i++){
					sumOfVotes = sumOfVotes.add((new BigInteger((indices[i]-indices[i-1]-1)+"")).multiply(votes.get(new Integer(i))));
				}
				sumOfVotes = sumOfVotes.add((new BigInteger((nVoters+nParties-2-indices[indices.length-1])+"")).multiply(votes.get(new Integer(nParties-1)))); // last party votes
				
				// in this stage sumOfVotes is a candidate to be the discrete log of x
				result = base.modPow(sumOfVotes, mod);
				if (result.equals(x)) 
					foundLog = true;
			}
		}
	}
	
    /**
     * Initializes the hash-map to contain the plain text votes
     */
    private static void initializeVotes(){
    	for (int i = 0; i < PAVShared.getPlaintextVotes().size(); i++) 
			votes.put(i, PAVShared.getPlaintextVote(i).getValue());
    }
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	   /**************************************************************/
	  /**  The following functions are for debugging purposes only **/
	 /**************************************************************/
	
	public static void main(String[] args) {
		try{
			PAVShared.initialize(false,false);
		}catch(ElectionsSetUpException e){}
		
		BigInteger exponent = BigInteger.ZERO;
		for(i=0; i<0;i++)
			exponent = exponent.add(PAVShared.getPlaintextVote(0).getValue());
		for(i=0; i<40;i++)
			exponent = exponent.add(PAVShared.getPlaintextVote(1).getValue());
		for(i=0; i<0;i++)
			exponent = exponent.add(PAVShared.getPlaintextVote(2).getValue());
		for(i=0; i<0;i++)
			exponent = exponent.add(PAVShared.getPlaintextVote(3).getValue());

//		PAVShared.announceResults(new BigIntegerMod(exponent,Consts.p));
		
		BigIntegerMod z = PAVShared.getZ();
//		//BigIntegerMod z = new BigIntegerMod(votes.get(new Integer(2)), Consts.getP());
		BigIntegerMod xPar = new BigIntegerMod(z.getValue().modPow(exponent, z.getMod()),z.getMod());
//		
//		long start = System.currentTimeMillis();
		BigIntegerMod output = dLog(xPar,z);
		if (output == null) System.out.println("*** Log Wasn't Found");
		else{	
			System.out.println("The original exponent was: "+ exponent);
			System.out.println("The log that was found is: "+output.getValue());
		}
	}
	
	/**
	 * Prints all the plain texts of the votes
	 */
	private static void printVotes(){
		for(BigInteger b: votes.values())
			System.out.println("* "+b);
	}
	
	/**
	 * Calculates the number of operations required for finding discrete log in worst case
	 * @param pVoters - number of Voters.
	 * @param pParties - number of parties.
	 */
	private static BigInteger calcDlogOper(int pVoters, int pParties){
		BigInteger bVoters = new BigInteger(pVoters+"");
		BigInteger bParties = new BigInteger(pParties+"");
		
		BigInteger a = factorial(bVoters.add(bParties).subtract(BigInteger.ONE));
		BigInteger b = factorial(bVoters);
		BigInteger c = factorial(bParties.subtract(BigInteger.ONE));
		BigInteger res = a.divide(b.add(c));
		//System.out.println("A="+a.toString()+"\nB="+b+"\nC="+c+"\nResult="+res);
		return res;
	}
	
	/**
	 * Factorial function over BigInteger 
	 */
    private static BigInteger factorial(BigInteger n )
    {
        if( n.compareTo(BigInteger.ZERO) == 0 )  // base case
        	return BigInteger.ONE;
        else
            return n.multiply(factorial(n.subtract(BigInteger.ONE)));
    }
    
    /**
     * This function prints an estimation on the worst case running time.
     * The field "timeFor100000Iterations" must be set manually depending on the machine running the code.
     * To calculate the value, set the Voters value to "100000" and choose some number of parties,
     *  give all the votes to, the one before last, party and measure the running time of looper function.
     * @param pVoters
     * @param pParties
     */
    private static void estimateDLogTime(int pVoters, int pParties){
    	long timeFor100000Iterations = 147857; // SET MANUALLY!
    	BigInteger iterations= calcDlogOper(pVoters, pParties);	
    	System.out.println("The worst case estimated time is: "+iterations.divide(new BigInteger(100000+"")).multiply(new BigInteger(timeFor100000Iterations+"")).divide(new BigInteger(60000+""))+" min");
    }

}


