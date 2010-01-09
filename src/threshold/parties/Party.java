package threshold.parties;

import java.math.BigInteger;

import elgamal.Ciphertext;
import elgamal.ElGamal;
import global.BigIntegerMod;
import global.Consts;
import global.Consts.DebugOutput;
import tcp.Client;
import threshold.ThresholdPacket;
import threshold.ThresholdPacket.PacketType;
import threshold.center.ThresholdCryptosystem;

public class Party {

	private static final int WAITING_TIME = 30000; //mili-seconds
	private final int partyNumber;
	private Client client;
	private int partiesAmount;
	private int threshold;
	private BigInteger p;
	private BigInteger q;
	private BigIntegerMod g;
	private BigIntegerMod privatePolynom[];
	private BigIntegerMod publicPolynoms[][];
	private BigIntegerMod publicKeys[];
	private BigIntegerMod mutualPolynom[];
	private BigIntegerMod mutualPrivateKey;
	private ElGamal elGamal;

	public Party(int partyNumber, String serverName, int serverPort) {
		this.partyNumber = partyNumber;
		this.client = new Client(serverName, serverPort, this.partyNumber);
		waitToConnect();
		initValues();
		sendPolynom();
		receivePublicPolynoms();
		sendPolynomValues();
		receivePolynomValues();
	}

	private void receivePolynomValues() {
		elGamal.SetPrivateAndPublicKeys(privatePolynom[0]);
		ThresholdPacket packet;
		BigIntegerMod a, b, m;
		Ciphertext c;
		mutualPrivateKey = new BigIntegerMod(BigInteger.ZERO, q);
		for (int i=0; i<partiesAmount; ++i) {
			do {
				packet = (ThresholdPacket)client.receive(WAITING_TIME);
			} while (packet == null);
			if (packet.type != PacketType.CIPHERTEXT) {
				Consts.log("Recieved wrong packet type - " + packet.type.toString(), DebugOutput.STDERR);
				Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			}
			a = new BigIntegerMod(packet.Data[0][0], p);
			b = new BigIntegerMod(packet.Data[0][1], p);
			c = new Ciphertext(a,b);
			m = elGamal.decrypt(c);
			m = new BigIntegerMod(m.getValue(), q);
			if (!verifyPolynomValue(packet.source, m)) {
				Consts.log("Polynom value got wrong", DebugOutput.STDERR);
				Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			}
			mutualPrivateKey = mutualPrivateKey.add(m);
		}
		if (!verifyMutualPrivateKey()) {
			Consts.log("Mutual private key got wrong", DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
		}
	}

	private boolean verifyMutualPrivateKey() {
		return publicKeys[partyNumber].equals(g.pow(mutualPrivateKey));
	}

	private boolean verifyPolynomValue(int otherParty, BigIntegerMod value) {
		BigIntegerMod polynomValue = ThresholdCryptosystem.computeExponentPolynomValue(publicPolynoms[otherParty], partyNumber+1, p, q);
		return polynomValue.equals(g.pow(value));
	}

	private void sendPolynomValues() {
		Ciphertext c;
		BigIntegerMod m;
		ThresholdPacket packet = new ThresholdPacket();
		packet.type = PacketType.CIPHERTEXT;
		packet.source = partyNumber;
		packet.Data = new BigInteger[1][2];
		for (int i=0; i<partiesAmount; ++i) {
			if (i == partyNumber) continue;
			packet.dest = i;
			elGamal.SetPublicKey(publicPolynoms[i][0]);
			m = new BigIntegerMod(ThresholdCryptosystem.computePolynomValue(privatePolynom, i+1, q).getValue(),p);
			c = elGamal.encrypt(m).getCiphertext();
			packet.Data[0][0] = c.getA().getValue();
			packet.Data[0][1] = c.getB().getValue();
			client.send(packet); //TODO check return value
		}
	}

	private void receivePublicPolynoms() {
		ThresholdPacket packet;
		do {
			packet = (ThresholdPacket)client.receive(WAITING_TIME);
		} while (packet == null);
		if (packet.type != PacketType.ALL_POLYNOMS) {
			Consts.log("Recieved wrong packet type - " + packet.type.toString(), DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
		}
		publicPolynoms = new BigIntegerMod[partiesAmount][threshold-1];
		publicKeys = new BigIntegerMod[partiesAmount];
		mutualPolynom = new BigIntegerMod[threshold-1];
		for (int i=0; i<partiesAmount; ++i) {
			publicKeys[i] = new BigIntegerMod(packet.Data[i][threshold], p);
			for (int j=0; j<threshold-1; ++j) {
				publicPolynoms[i][j] = new BigIntegerMod(packet.Data[i][j], p);
			}
		}
		for (int j=0; j<threshold-1; ++j) {
			mutualPolynom[j] = new BigIntegerMod(packet.Data[partiesAmount][j], p);
		}
	}

	private void sendPolynom() {
		ThresholdPacket packet = new ThresholdPacket();
		packet.type = PacketType.POLYNOM;
		packet.source = partyNumber;
		packet.Data = new BigInteger[1][threshold-1];
		for (int i=0; i<threshold-1; ++i) {
			packet.Data[0][i] = g.pow(privatePolynom[i]).getValue();
		}
		client.send(packet); //TODO check return value
	}

	private void waitToConnect() {
		while (!isConnected()) {
			try {
				wait(2 * Consts.CONNECTION_TIMEOUT);
			} catch (InterruptedException e) {
				Consts.log(e.toString(), Consts.DebugOutput.STDERR);
			}
		}
	}

	private void initValues() {
		ThresholdPacket packet;
		do {
			packet = (ThresholdPacket)client.receive(WAITING_TIME);
		} while (packet == null);
		if (packet.type != PacketType.BASIC_INFO) {
			Consts.log("Recieved wrong packet type - " + packet.type.toString(), DebugOutput.STDERR);
			Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
		}
		partiesAmount = packet.Parameters[0];
		threshold = packet.Parameters[1];
		p = packet.Data[0][0];
		q = p.subtract(BigInteger.ONE).divide(Consts.TWO);
		g = new BigIntegerMod(packet.Data[0][1], p);
		genPrivatePolynom();
		elGamal = new ElGamal(p, g, null, privatePolynom[0]);
	}

	private void genPrivatePolynom() {
		privatePolynom = new BigIntegerMod[threshold-1];
		for (int i=0; i<threshold-1; ++i) {
			privatePolynom[i] = new BigIntegerMod(q);
		}
	}

	public boolean isConnected() {
		return client.isConnected();
	}

	// TODO - find out which command line runs the party as a process, and then call Utils.runProcess(command line)
	public static void main(String[] args) {
		if (args.length > 0) {
			int i = Integer.parseInt(args[0]);
			if (i >= 0) {
				//new Party(i);
			}
		} else {
			//new Party(-1);
		}
	}

}
