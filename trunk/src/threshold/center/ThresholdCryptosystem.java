package threshold.center;


import java.math.BigInteger;
import tcp.Server;
import threshold.IThresholdCryptosystem;
import threshold.ThresholdPacket;
import threshold.ThresholdPacket.PacketType;
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
	private boolean keyReady;
	private Integer keyReadyLock;
	private boolean keyExchangeDone;
	private Integer keyExchangeLock;

	public ThresholdCryptosystem(int portnum) {
		applyConstructor(Consts.PARTIES_AMOUNT, Consts.THRESHOLD, Consts.getP(), Consts.getG(), portnum);
	}

	public ThresholdCryptosystem(int partiesAmount, int threshold, BigInteger p, BigIntegerMod g, int portnum) {
		applyConstructor(partiesAmount, threshold, p, g, portnum);
	}

	private void applyConstructor(int partiesAmount, int threshold, BigInteger p, BigIntegerMod g, int portnum) {
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
		keyReadyLock = new Integer(0);
		keyExchangeDone = false;
		keyExchangeLock = new Integer(0);
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

	public void close() {
		wait4KeyExchange();
		synchronized(keyExchangeLock) {
			ThresholdPacket packet = new ThresholdPacket();
			packet.type = PacketType.END;
			server.broadcast(packet);
			try {
				wait(2 * Consts.CONNECTION_TIMEOUT);
			} catch (InterruptedException e) {
				Consts.log(e.toString(), Consts.DebugOutput.STDERR);
			}
			server.close();
			try {
				wait(2 * Consts.CONNECTION_TIMEOUT);
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

	public BigIntegerMod[] getMutualPolynom() {
		wait4Key();
		return mutualPolynom;
	}

	public BigIntegerMod[] getMutualPublicKeys() {
		wait4Key();
		return clientsPublicKeys;
	}

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

	public BigIntegerMod decryptMutually(Ciphertext ciphertext, int parties_to_use) {
		if (parties_to_use < threshold) {
			Consts.log("Trying to mutually decrypt using too few parties (" + parties_to_use + " < " + Consts.THRESHOLD + ")", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			return null;
		}
		if (parties_to_use > partiesAmount) {
			Consts.log("Trying to mutually decrypt using more parties than exist (" + parties_to_use + " > " + Consts.PARTIES_AMOUNT + ")", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			return null;
		}
		// TODO randomly choose threshold parties
		wait4KeyExchange();
		synchronized(keyExchangeLock) {
			// TODO decrypt the given ciphertext using the threshold parties' private keys
		}
		return null;
	}

	public void wait4KeyExchange() {
		synchronized(keyExchangeLock) {
			if (!keyExchangeDone) {
				try {
					wait();
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
			handlePrivateKeysExchange();
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
				clientsPublicKeys[i] = computeExponentPolynomValue(mutualPolynom, i, p, q);
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
				server.send(packet.dest, packet); //TODO check return value
				Consts.log("moved packet from "+packet.source+" to "+packet.dest, DebugOutput.STDOUT);
			}
			Consts.log("threshold center: finished key exchange", DebugOutput.STDOUT);
		}
	}

}
