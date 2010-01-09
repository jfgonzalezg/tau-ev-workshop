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
	private BigInteger clientsPublicKeys[];

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
		clientsPolynoms = new BigInteger[partiesAmount][threshold-1];
		clientsPublicKeys = new BigInteger[partiesAmount];
		mutualPolynom = new BigIntegerMod[threshold-1];
		for (int i=0; i<threshold-1; ++i) {
			mutualPolynom[i] = new BigIntegerMod(BigInteger.ONE, p);
		}
		generateMutualPublicKey();
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

	private void handlePrivateKeysExchange() {
		ThresholdPacket packet = null;
		for (int i=0; i<partiesAmount*(partiesAmount-1); ++i) {
			packet = recieveNextPacket();
			if (packet.type != PacketType.CIPHERTEXT) {
				Consts.log("Recieved wrong packet type - " + packet.type.toString(), DebugOutput.STDERR);
				Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			}
			server.send(packet.dest, packet); //TODO check return value
		}
	}

	private void publishPolynoms() {
		ThresholdPacket packet = new ThresholdPacket();
		packet.type = PacketType.ALL_POLYNOMS;
		packet.Data = new BigInteger[partiesAmount+1][threshold];
		for (int i=0; i<partiesAmount; ++i) {
			packet.Data[i][threshold-1] = clientsPublicKeys[i];
			for (int j=0; j<threshold-1; ++j) {
				packet.Data[i][j] = clientsPolynoms[i][j];
			}
		}
		for (int j=0; j<threshold-1; ++j) {
			packet.Data[partiesAmount][j] = mutualPolynom[j].getValue();
		}
		server.broadcast(packet);
	}

	private ThresholdPacket recieveNextPacket() {
		ThresholdPacket packet;
		do {
			packet = (ThresholdPacket)server.getReceivedObject().getMessage();
		} while (packet == null);
		return packet;
	}

	private void assignPolynom(int partyNum, BigInteger polynom[]) {
		if (polynom.length < threshold-1) {
			Consts.log("polynom length is less then threshold", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
		}
		clientsPolynoms[partyNum] = polynom;
		for (int i=0; i<threshold-1; ++i) {
			mutualPolynom[i] = mutualPolynom[i].multiply(new BigIntegerMod(polynom[i], p));
		}
	}

	private void getPolynoms() {
		ThresholdPacket packet;
		for (int i=0; i<partiesAmount; ++i) {
			packet = recieveNextPacket();
			if (packet.type != PacketType.POLYNOM) {
				Consts.log("Recieved wrong packet type - " + packet.type.toString(), DebugOutput.STDERR);
				Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			}
			assignPolynom(packet.source, packet.Data[0]);
		}
		mutualPublicKey = mutualPolynom[0];
		computePublicKeys();
	}

	private void computePublicKeys() {
		for (int i=0; i<partiesAmount; ++i) {
			clientsPublicKeys[i] = computeExponentPolynomValue(mutualPolynom, i, p, q).getValue();
		}
	}

	public static BigIntegerMod computeExponentPolynomValue(BigIntegerMod polynom[], Integer x, BigInteger p, BigInteger q) {
		BigIntegerMod result = new BigIntegerMod(BigInteger.ONE, p);
		BigIntegerMod base = new BigIntegerMod(new BigInteger(x.toString()), q);
		BigIntegerMod currExponent = result;
		for (int i=0; i<polynom.length; ++i) {
			result = result.multiply(polynom[i].pow(currExponent));
			currExponent = currExponent.multiply(base);
		}
		return result;
	}
	
	public static BigIntegerMod computePolynomValue(BigIntegerMod polynom[], Integer x, BigInteger q) {
		BigIntegerMod result = new BigIntegerMod(BigInteger.ONE, q);
		BigIntegerMod base = new BigIntegerMod(new BigInteger(x.toString()), q);
		BigIntegerMod currMultiplier = result;
		for (int i=0; i<polynom.length; ++i) {
			result = result.add(polynom[i].multiply(currMultiplier));
			currMultiplier = currMultiplier.multiply(base);
		}
		return result;
	}

	private void clientsInit() {
		ThresholdPacket packet = new ThresholdPacket();
		packet.type = PacketType.BASIC_INFO;
		packet.Data = new BigInteger[1][2];
		packet.Data[0][0] = p;
		packet.Data[0][1] = g.getValue();
		packet.Parameters = new int[2];
		packet.Parameters[0] = partiesAmount;
		packet.Parameters[1] = threshold;
		while (server.getConnectionNumbers().length < partiesAmount) {
			try {
				wait(2 * Consts.CONNECTION_TIMEOUT);
			} catch (InterruptedException e) {
				Consts.log(e.toString(), Consts.DebugOutput.STDERR);
			}
		}
		server.broadcast(packet);
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
		return mutualPublicKey;
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
		// TODO decrypt the given ciphertext using the threshold parties' private keys
		return null;
	}

}
