package threshold.center;


import java.math.BigInteger;
import java.util.Random;

import tcp.Server;
import threshold.IThresholdCryptosystem;
import threshold.ThresholdPacket;
import threshold.ThresholdPacket.PacketType;
import zkp.ZkpException;
import zkp.EDlog.EDlog;
import elgamal.Ciphertext;
import global.BigIntegerMod;
import global.Consts;
import global.Consts.DebugOutput;

public class ThresholdCryptosystem implements IThresholdCryptosystem {

	private int partiesAmount;
	private int threshold;
	private BigInteger p; 
	private BigInteger q;
	private BigIntegerMod g;
	private BigIntegerMod mutualPublicKey;
	private Server server;
	private BigInteger clientsPolynoms[][];
	private BigIntegerMod mutualPolynom[];
	private BigIntegerMod clientsPublicKeys[];
	private EDlog ZKP;
	private boolean keyReady;
	private Integer keyReadyLock = new Integer(0);
	private boolean keyExchangeDone;
	private Integer keyExchangeLock = new Integer(0);
	private Integer waitLock = new Integer(0);

	public ThresholdCryptosystem() {
		applyConstructor(null, null, null, null, null);
	}
	
	public ThresholdCryptosystem(int portnum) {
		applyConstructor(null, null, null, null, portnum);
	}

	public ThresholdCryptosystem(Integer partiesAmount, Integer threshold, BigInteger p, BigIntegerMod g, Integer portnum) {
		applyConstructor(partiesAmount, threshold, p, g, portnum);
	}

	private void applyConstructor(Integer partiesAmount, Integer threshold, BigInteger p, BigIntegerMod g, Integer portnum) {
		//getting the right parameters
		partiesAmount = (partiesAmount == null) ? Consts.PARTIES_AMOUNT : partiesAmount;
		threshold = (threshold == null) ? Consts.THRESHOLD : threshold;
		p = (p == null) ? Consts.getP() : p;
		g = (g == null) ? Consts.getG() : g;
		portnum = (portnum == null) ? Consts.THRESHOLD_CENTER_PORT : portnum;
		
		server = new Server(portnum);
		this.partiesAmount = partiesAmount;
		this.threshold = threshold;
		this.p = p;
		q = p.subtract(BigInteger.ONE).divide(Consts.TWO);
		this.g = g;
		clientsPolynoms = new BigInteger[partiesAmount][threshold];
		clientsPublicKeys = new BigIntegerMod[partiesAmount];
		mutualPolynom = new BigIntegerMod[threshold];
		for (int i=0; i<threshold; ++i) {
			mutualPolynom[i] = new BigIntegerMod(BigInteger.ONE, p);
		}
		keyReady = false;
		keyExchangeDone = false;
		ZKP = new EDlog();
		ZKP.setG(g);
		Consts.log("threshold center: finished initializing values. starting key-exchange.", DebugOutput.STDOUT);
		new KeyExchangeThread();
	}

	private ThresholdPacket recieveNextPacket() {
		Server.Message m;
		do {
			m = server.getReceivedObject();;
		} while (m == null);
		return (ThresholdPacket)(m.getMessage());
	}

	public static BigIntegerMod computeExponentPolynomValue(BigIntegerMod polynom[], Integer x, BigInteger p, BigInteger q) {
		BigIntegerMod result = new BigIntegerMod(BigInteger.ONE, p);
		BigIntegerMod base = new BigIntegerMod(new BigInteger(x.toString()), q);
		BigIntegerMod currExponent = new BigIntegerMod(BigInteger.ONE, q);
		for (int i=0; i<polynom.length; ++i) {
			result = result.multiply(polynom[i].pow(currExponent));
			currExponent = currExponent.multiply(base);
		}
		return result;
	}
	
	public static BigIntegerMod computePolynomValue(BigIntegerMod polynom[], Integer x, BigInteger q) {
		BigIntegerMod result = new BigIntegerMod(BigInteger.ZERO, q);
		BigIntegerMod base = new BigIntegerMod(new BigInteger(x.toString()), q);
		BigIntegerMod currMultiplier = new BigIntegerMod(BigInteger.ONE, q);
		for (int i=0; i<polynom.length; ++i) {
			result = result.add(polynom[i].multiply(currMultiplier));
			currMultiplier = currMultiplier.multiply(base);
		}
		return result;
	}

	public static Integer[] genRandomTOutOfN(int n, int t) {
		int startingArray[] = new int[n];
		for (int i=0; i<n; ++i) {
			startingArray[i] = i;
		}
		Random r = new Random();
		int index2Remove;
		for (int i=n; i>t; --i) {
			index2Remove = r.nextInt(n);
			startingArray[index2Remove] = startingArray[i-1];
		}
		Integer[] result = new Integer[t];
		for (int i=0; i<t; ++i) {
			result[i] = startingArray[i];
		}
		return result;
	}

	public void close() {
		wait4KeyExchange();
		ThresholdPacket packet = new ThresholdPacket();
		packet.type = PacketType.END;
		server.broadcast(packet);
		
		synchronized(waitLock) {
			try {
				waitLock.wait(5 * Consts.CONNECTION_TIMEOUT);
			} catch (InterruptedException e) {
				Consts.log(e.toString(), Consts.DebugOutput.STDERR);
			}
		}
		server.close();
		synchronized(waitLock) {
			try {
				waitLock.wait(5 * Consts.CONNECTION_TIMEOUT);
			} catch (InterruptedException e) {
				Consts.log(e.toString(), Consts.DebugOutput.STDERR);
			}
		}
	}

	public int getPartiesAmount() {
		return partiesAmount;
	}

	public int getThreshold() {
		return threshold;
	}

	public BigInteger getP() {
		return p;
	}

	public BigIntegerMod getG() {
		return g;
	}

	public BigIntegerMod getMutualPublicKey() {
		wait4Key();
		return mutualPublicKey;
	}

	/*
	public BigIntegerMod[] getMutualPolynom() {
		wait4Key();
		return mutualPolynom;
	}
	*/

	/*
	public BigIntegerMod[] getMutualPublicKeys() {
		wait4Key();
		return clientsPublicKeys;
	}
	*/

	public void wait4Key() {
		synchronized(keyReadyLock) {
			if (!keyReady) {
				try {
					keyReadyLock.wait();
				} catch (InterruptedException e) {
					Consts.log(e.toString(), Consts.DebugOutput.STDERR);
				}
			}
		}
	}

	public BigIntegerMod decryptMutually(Ciphertext ciphertext) {
		return decryptMutually(ciphertext, threshold);
	}

	public BigIntegerMod decryptMutually(Ciphertext ciphertext, int parties2Use) {
		if (parties2Use < threshold) {
			Consts.log("Trying to mutually decrypt using too few parties (" + parties2Use + " < " + Consts.THRESHOLD + ")", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			return null;
		}
		if (parties2Use > partiesAmount) {
			Consts.log("Trying to mutually decrypt using more parties than exist (" + parties2Use + " > " + Consts.PARTIES_AMOUNT + ")", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			return null;
		}
		Integer[] chosenParties = genRandomTOutOfN(partiesAmount, parties2Use);
		wait4KeyExchange();
		Consts.log("threshold center: sending message to decrypt to chosen parties", DebugOutput.STDOUT);
		sendM2ChosenParties(ciphertext.getA().getValue(), chosenParties);
		Consts.log("threshold center: computing lambdas", DebugOutput.STDOUT);
		BigIntegerMod lambdas[] = computeLambdas(chosenParties);
		Consts.log("threshold center: receiving parties' answers", DebugOutput.STDOUT);
		BigIntegerMod w_i[] = receiveFromChosenParties(parties2Use, ciphertext.getA());
		Consts.log("threshold center: computing a^s", DebugOutput.STDOUT);
		BigIntegerMod c_s = compute_c_s(w_i, lambdas);
		Consts.log("threshold center: returning decryption", DebugOutput.STDOUT);
		return ciphertext.getB().multiply(c_s.inverse());
	}

	private BigIntegerMod compute_c_s(BigIntegerMod w_i[], BigIntegerMod lambdas[]) {
		BigIntegerMod result = new BigIntegerMod(BigInteger.ONE, p);
		for (int i=0; i<w_i.length; ++i) {
			result = result.multiply(w_i[i].pow(lambdas[i]));
		}
		return result;
	}

	private BigIntegerMod[] receiveFromChosenParties(int t, BigIntegerMod m) {
		BigIntegerMod w_i[] = new BigIntegerMod[t];
		BigIntegerMod next_w_i;
		ThresholdPacket packet;
		boolean ZKP_OK = false;
		for (int i=0; i<t; ++i) {
			packet = recieveNextPacket();
			next_w_i = new BigIntegerMod(packet.Data[0][0], p);
			try {
				ZKP_OK = ZKP.verifyEDlogProof(packet.ZKP, m, clientsPublicKeys[packet.source], next_w_i);
			} catch (ZkpException e) {
				e.printStackTrace();
			}
			if (!ZKP_OK) {
				System.out.println("ZKP went wrong");
			}
			w_i[packet.dest] = next_w_i;
		}
		return w_i;
	}

	private BigIntegerMod[] computeLambdas(Integer[] partiesInteger) {
		int t = partiesInteger.length;
		BigIntegerMod parties[] = new BigIntegerMod[t];
		BigIntegerMod pi_j_inverse = new BigIntegerMod(BigInteger.ONE, q);
		BigIntegerMod lambdas[] = new BigIntegerMod[t];
		for (int i=0; i<t; ++i) {
			BigInteger J = new BigInteger(partiesInteger[i].toString());
			parties[i] = new BigIntegerMod(J.add(BigInteger.ONE), q);
			pi_j_inverse = pi_j_inverse.multiply(parties[i]);
		}
		pi_j_inverse = pi_j_inverse.inverse();
		for (int i=0; i<t; ++i) {
			lambdas[i] = parties[i].multiply(pi_j_inverse);
			for (int j=0; j<t; ++j) {
				if (j==i) continue;
				lambdas[i]=lambdas[i].multiply(parties[j].substract(parties[i]));
			}
			lambdas[i] = lambdas[i].inverse();
		}
		return lambdas;
	}
	
	private void sendM2ChosenParties(BigInteger m, Integer parties[]) {
		Consts.log("threshold center: sending number to decrypt to "+parties.length+" parties", DebugOutput.STDOUT);
		ThresholdPacket packet;
		for (int i=0; i<parties.length; ++i) {
			packet = new ThresholdPacket();
			packet.type = PacketType.NUMBER;
			packet.dest = parties[i];
			packet.source = i;
			packet.Data = new BigInteger[1][1];
			packet.Data[0][0] = m;
			server.send(parties[i], packet);
			Consts.log("threshold center: sent number to decrypt to party "+parties[i], DebugOutput.STDOUT);
		}
		Consts.log("threshold center: sent number to decrypt to all clients", DebugOutput.STDOUT);
	}

	public void wait4KeyExchange() {
		synchronized(keyExchangeLock) {
			if (!keyExchangeDone) {
				try {
					keyExchangeLock.wait();
				} catch (InterruptedException e) {
					Consts.log(e.toString(), Consts.DebugOutput.STDERR);
				}
			}
		}
	}
	
	private class KeyExchangeThread extends Thread {
		
		KeyExchangeThread() {
			start();
		}

		public void run() {
			synchronized(keyExchangeLock) {
				generateMutualPublicKey();
				keyExchangeDone = true;
				keyExchangeLock.notifyAll();
			}
		}
		
		/**
		 * Generates a mutual public key for a mutual encryption
		 * @return the generated public key, where (public key).getMod()==Consts.getQ()
		 */
		private void generateMutualPublicKey() {
			clientsInit();
			getPolynoms();
			publishPolynoms();
			removeUnnecessaryData();
			handlePrivateKeysExchange();
		}
		
		private void removeUnnecessaryData() {
			clientsPolynoms = null;
			mutualPolynom = null;
		}
		
		private void clientsInit() {
			Consts.log("Initializing clients", Consts.DebugOutput.STDOUT);
			ThresholdPacket packet = new ThresholdPacket();
			packet.type = PacketType.BASIC_INFO;
			packet.Data = new BigInteger[1][2];
			packet.Data[0][0] = p;
			packet.Data[0][1] = g.getValue();
			packet.Parameters = new int[2];
			packet.Parameters[0] = partiesAmount;
			packet.Parameters[1] = threshold;
			wait4AllClients();
			Consts.log("threshold center: all clients connected! sending global parameters to all", DebugOutput.STDOUT);
			server.broadcast(packet);
		}
		
		private void wait4AllClients() {
			while (server.getConnectionNumbers().length < partiesAmount) {
				try {
					sleep(2);
				} catch (InterruptedException e) {
					Consts.log(e.toString(), Consts.DebugOutput.STDERR);
				}
			}
		}
		
		private void getPolynoms() {
			ThresholdPacket packet;
			Consts.log("threshold center: waiting for client's polynoms", DebugOutput.STDOUT);
			for (int i=0; i<partiesAmount; ++i) {
				packet = recieveNextPacket();
				if (packet.type != PacketType.POLYNOM) {
					Consts.log("Recieved wrong packet type - " + packet.type, DebugOutput.STDERR);
					Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
				}
				Consts.log("threshold center: got polynom from party "+packet.source, DebugOutput.STDOUT);
				assignPolynom(packet.source, packet.Data[0]);
			}
			Consts.log("threshold center: got polynom from all parties", DebugOutput.STDOUT);
			mutualPublicKey = mutualPolynom[0];
			computePublicKeys();
			synchronized(keyReadyLock) {
				keyReady = true;
				keyReadyLock.notifyAll();
			}
		}
		
		private void assignPolynom(int partyNum, BigInteger polynom[]) {
			if (polynom.length < threshold) {
				Consts.log("polynom length is less then threshold", DebugOutput.STDERR);
				Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			}
			clientsPolynoms[partyNum] = polynom;
			for (int i=0; i<threshold; ++i) {
				mutualPolynom[i] = mutualPolynom[i].multiply(new BigIntegerMod(polynom[i], p));
			}
		}
		
		private void computePublicKeys() {
			for (int i=0; i<partiesAmount; ++i) {
				clientsPublicKeys[i] = computeExponentPolynomValue(mutualPolynom, i+1, p, q);
			}
		}
		
		private void publishPolynoms() {
			ThresholdPacket packet = new ThresholdPacket();
			packet.type = PacketType.ALL_POLYNOMS;
			packet.Data = new BigInteger[partiesAmount+1][threshold+1];
			for (int i=0; i<partiesAmount; ++i) {
				packet.Data[i][threshold] = clientsPublicKeys[i].getValue();
				for (int j=0; j<threshold; ++j) {
					packet.Data[i][j] = clientsPolynoms[i][j];
				}
			}
			for (int j=0; j<threshold; ++j) {
				packet.Data[partiesAmount][j] = mutualPolynom[j].getValue();
			}
			Consts.log("threshold center: publishing all polynoms", DebugOutput.STDOUT);
			server.broadcast(packet);
		}
		
		private void handlePrivateKeysExchange() {
			ThresholdPacket packet = null;
			Consts.log("threshold center: exchanging keys between parties", DebugOutput.STDOUT);
			for (int i=0; i<partiesAmount*(partiesAmount-1); ++i) {
				packet = recieveNextPacket();
				if (packet.type != PacketType.CIPHERTEXT) {
					Consts.log("Recieved wrong packet type - " + packet.type, DebugOutput.STDERR);
					Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
				}
				server.send(packet.dest, packet);
				Consts.log("moved packet from "+packet.source+" to "+packet.dest, DebugOutput.STDOUT);
			}
			Consts.log("threshold center: finished key exchange", DebugOutput.STDOUT);
		}
	}

}
