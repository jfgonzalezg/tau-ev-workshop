package MixCenter;

import java.io.Serializable;
import java.math.BigInteger;

import elgamal.Ciphertext;

import global.BigIntegerMod;

public class SentObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -98660363992845654L;
	private Ciphertext[] 	votes_array;
	private BigIntegerMod 	G;
	private BigInteger 		P;
	private BigInteger 		Q;
	private BigIntegerMod 	W;
	private	int				N;
	
	public SentObject(	Ciphertext[] 	votes_array,
						BigIntegerMod 	G,
						BigInteger 		P,
						BigInteger 		Q,
						BigIntegerMod 	W,
						int				N) {
		this.votes_array = votes_array;
		this.G = G;
		this.P = P;
		this.Q = Q;
		this.W = W;
		this.N = N;
	}
	
	public Ciphertext[] get_votes_array(){
		return votes_array;
	}
	
	public BigIntegerMod get_G(){
		return G;
	}
	
	public BigInteger get_P(){
		return P;
	}
	
	public BigInteger get_Q(){
		return Q;
	}
	
	public BigIntegerMod get_W(){
		return W;
	}
	
	public int get_N(){
		return N;
	}
}
