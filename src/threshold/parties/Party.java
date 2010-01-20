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
import zkp.ZkpException;
import zkp.EDlog.EDlog;

public class Party {

	private static final int WAITING_TIME = 0;//5 * Consts.CONNECTION_TIMEOUT; //mili-seconds
	private static final int RECEIVING_RETRIES = 20;
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
	private BigIntegerMod mutualPublicKey = null;
	private EDlog ZKP;
	private ElGamal elGamal;
	private PartyThread thread;
	private boolean polynomGenerated;
	private Integer polynomGeneratedLock;
	private boolean keyExchangeFinished;
	private Integer keyExchangeFinishedLock;
	private Integer waitLock = new Integer(0);
	private boolean closing = false;

	public Party(int partyNumber, String serverName, int serverPort) {
		this.partyNumber = partyNumber;
		this.client = new Client(serverName, serverPort, this.partyNumber);
		polynomGenerated = false;
		polynomGeneratedLock = new Integer(0);
		keyExchangeFinished = false;
		keyExchangeFinishedLock = new Integer(0);
		ZKP = new EDlog();
		//waitingLock = new Integer(0);
		waitToConnect();
		thread = new PartyThread();
	}

	public boolean isFinished() {
		return !thread.isAlive();
	}

	public void wait2Finish() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			Consts.log("Party "+partyNumber+": unable to join thread", DebugOutput.STDERR);
		}
	}

	public BigIntegerMod[] getPrivatePolynom() {
		wait4PolynomGeneration();
		return privatePolynom;
	}

	public void wait4PolynomGeneration() {
		synchronized(polynomGeneratedLock){
			if (!polynomGenerated) {
				try {
					polynomGeneratedLock.wait();
				} catch (InterruptedException e) {
					Consts.log("Party "+partyNumber+": unable to lock - "+e, DebugOutput.STDERR);
				}
			}
		}
	}
	
	public BigIntegerMod getMutualPrivateKey() {
		wait4KeyExchange();
		return mutualPrivateKey;
	}

	public void wait4KeyExchange() {
		synchronized(keyExchangeFinishedLock){
			if (!keyExchangeFinished) {
				try {
					keyExchangeFinishedLock.wait();
				} catch (InterruptedException e) {
					Consts.log("Party "+partyNumber+": unable to lock - "+e, DebugOutput.STDERR);
				}
			}
		}
	}

	private class PartyThread extends Thread {
		
		PartyThread() {
			start();
		}

		public void run() {
			ExchangeKey();
			removeUnnecessaryData();
			DecryptMessages();
			synchronized(waitLock) {
				try {
					waitLock.wait(10 * Consts.CONNECTION_TIMEOUT);
				} catch (InterruptedException e) {
					Consts.log(e.toString(), Consts.DebugOutput.STDERR);
				}
			}
		}
		
		private void removeUnnecessaryData() {
			if (mutualPublicKey == null) {
				privatePolynom = null;
				publicPolynoms = null;
				publicKeys = null;
				mutualPolynom = null;
			}
			mutualPublicKey = g.pow(mutualPrivateKey);
			
		}
		
		private void DecryptMessages() {
			if (closing) return;
			Consts.log("party "+partyNumber+": waiting to decrypt message", Consts.DebugOutput.STDOUT);
			ThresholdPacket receivingPacket = receive();
			Consts.log("party "+partyNumber+": received packet to decrypt", Consts.DebugOutput.STDOUT);
			ThresholdPacket sendingPacket;
			BigIntegerMod m;
			BigIntegerMod mpow;
			while (!closing) {
				sendingPacket = new ThresholdPacket();
				sendingPacket.type = PacketType.NUMBER;
				sendingPacket.dest = receivingPacket.source;
				sendingPacket.source = receivingPacket.dest;
				sendingPacket.Data = new BigInteger[1][1];
				m = new BigIntegerMod(receivingPacket.Data[0][0], p);
				mpow = m.pow(mutualPrivateKey);
				sendingPacket.Data[0][0] = mpow.getValue();
				try {
					sendingPacket.ZKP = ZKP.createEDlogProof(m, mutualPublicKey, mpow, mutualPrivateKey);
				} catch (ZkpException e) {
					e.printStackTrace();
				}
				client.send(sendingPacket);
				Consts.log("party "+partyNumber+": sent decryption. waiting to decrypt another message", Consts.DebugOutput.STDOUT);
				receivingPacket = receive();
				Consts.log("party "+partyNumber+": received another packet to decrypt", Consts.DebugOutput.STDOUT);
			}
		}
		
		private void ExchangeKey() {
			Consts.log("party "+partyNumber+":key exchane started", Consts.DebugOutput.STDOUT);
			initValues();
			if (closing) return;
			Consts.log("party "+partyNumber+": all values initialized", Consts.DebugOutput.STDOUT);
			synchronized(polynomGeneratedLock) {
				polynomGenerated = true;
				polynomGeneratedLock.notifyAll();
			}
			sendPolynom();
			receivePublicPolynoms();
			if (closing) return;
			sendPolynomValues();
			receivePolynomValues();
			if (closing) return;
			synchronized(keyExchangeFinishedLock) {
				keyExchangeFinished = true;
				keyExchangeFinishedLock.notifyAll();
			}
			Consts.log("party "+partyNumber+": finished key exchange", Consts.DebugOutput.STDOUT);
		}
		
		private void initValues() {
			Consts.log("party "+partyNumber+":getting basic info from server", Consts.DebugOutput.STDOUT);
			ThresholdPacket packet = receive();
			if (closing) {
				Consts.log("Party "+partyNumber+": closing party in initValues stage.", DebugOutput.STDERR);
				return;
			}
			if (packet.type != PacketType.BASIC_INFO) {
				Consts.log("Recieved wrong packet type - " + packet.type, DebugOutput.STDERR);
				Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			}
			partiesAmount = packet.Parameters[0];
			threshold = packet.Parameters[1];
			p = packet.Data[0][0];
			q = p.subtract(BigInteger.ONE).divide(Consts.TWO);
			g = new BigIntegerMod(packet.Data[0][1], p);
			Consts.log("Party "+partyNumber+": got values:\np: "+p+"\ng: "+g, DebugOutput.STDOUT);
			genPrivatePolynom();
			elGamal = new ElGamal(p, g, null, privatePolynom[0]);
		}
		
		private ThresholdPacket receive() {
			ThresholdPacket packet = null;
			for (int i=0; ((i<RECEIVING_RETRIES)&&(packet == null)); ++i) {
				if (client.canReceive()) {
					packet = (ThresholdPacket)client.receive(WAITING_TIME);
					if (packet == null) {
						Consts.log("party " + partyNumber + ": trying again", Consts.DebugOutput.STDOUT);
					}
				} else {
					Consts.log("party " + partyNumber + ": cannot receive, returing from initValues()'s loop.", Consts.DebugOutput.STDOUT);
					closeMe();
					return null;
				}
			}
			if (packet == null) {
				Consts.log("party "+partyNumber+": Tried to receive packet "+RECEIVING_RETRIES+", unsuccessful. closing...", Consts.DebugOutput.STDOUT);
				closeMe();
				return null;
			}
			if (packet.type == PacketType.END) {
				Consts.log("party "+partyNumber+": recieved END packet. closing...", Consts.DebugOutput.STDOUT);
				closeMe();
			}
			return packet;
		}

		private void closeMe() {
			closing = true;
			synchronized(polynomGeneratedLock) {
				polynomGenerated = true;
				polynomGeneratedLock.notifyAll();
			}
			synchronized(keyExchangeFinishedLock) {
				keyExchangeFinished = true;
				keyExchangeFinishedLock.notifyAll();
			}
		}
		
		private void genPrivatePolynom() {
			String s = "";
			privatePolynom = new BigIntegerMod[threshold];
			for (int i=0; i<threshold; ++i) {
				privatePolynom[i] = new BigIntegerMod(q);
				s = s+privatePolynom[i].getValue() + " ";
			}
			Consts.log("Party "+partyNumber+": generated polynom:" + s, DebugOutput.STDOUT);
		}
		private void sendPolynom() {
			Consts.log("party "+partyNumber+": sending my polynom", Consts.DebugOutput.STDOUT);
			ThresholdPacket packet = new ThresholdPacket();
			packet.type = PacketType.POLYNOM;
			packet.source = partyNumber;
			packet.Data = new BigInteger[1][threshold];
			for (int i=0; i<threshold; ++i) {
				packet.Data[0][i] = g.pow(privatePolynom[i]).getValue();
			}
			client.send(packet);
			Consts.log("party "+partyNumber+": sent polynom", Consts.DebugOutput.STDOUT);
		}
		
		private void receivePublicPolynoms() {
			Consts.log("party "+partyNumber+": trying to recieve all polynoms", Consts.DebugOutput.STDOUT);
			ThresholdPacket packet = receive();
			if (closing) {
				Consts.log("Party "+partyNumber+": closing party in receivePublicPolynoms stage.", DebugOutput.STDERR);
				return;
			}
			if (packet.type != PacketType.ALL_POLYNOMS) {
				Consts.log("Recieved wrong packet type - " + packet.type, DebugOutput.STDERR);
				Consts.log((new Exception()).getStackTrace().toString(), DebugOutput.STDERR);
			}
			Consts.log("party "+partyNumber+": got all polynoms. parsing...", Consts.DebugOutput.STDOUT);
			publicPolynoms = new BigIntegerMod[partiesAmount][threshold];
			publicKeys = new BigIntegerMod[partiesAmount];
			mutualPolynom = new BigIntegerMod[threshold];
			for (int i=0; i<partiesAmount; ++i) {
				publicKeys[i] = new BigIntegerMod(packet.Data[i][threshold], p);
				for (int j=0; j<threshold; ++j) {
					publicPolynoms[i][j] = new BigIntegerMod(packet.Data[i][j], p);
				}
			}
			for (int j=0; j<threshold; ++j) {
				mutualPolynom[j] = new BigIntegerMod(packet.Data[partiesAmount][j], p);
			}
			Consts.log("party "+partyNumber+": finished parsing all polynoms", Consts.DebugOutput.STDOUT);
		}
		
		private void sendPolynomValues() {
			Consts.log("party "+partyNumber+": sending polynom values to all other parties", Consts.DebugOutput.STDOUT);
			Ciphertext c;
			BigIntegerMod m;
			ThresholdPacket packet;
			for (int i=0; i<partiesAmount; ++i) {
				m = ThresholdCryptosystem.computePolynomValue(privatePolynom, i+1, q);
				if (i == partyNumber) {
					mutualPrivateKey = m;
					continue;
				}
				packet = new ThresholdPacket();
				packet.type = PacketType.CIPHERTEXT;
				packet.source = partyNumber;
				packet.Data = new BigInteger[1][2];
				packet.dest = i;
				elGamal.SetPublicKey(publicPolynoms[i][0]);
				m = new BigIntegerMod(m.getValue(),p);
				c = elGamal.encrypt(m).getCiphertext();
				packet.Data[0][0] = c.getA().getValue();
				packet.Data[0][1] = c.getB().getValue();
				client.send(packet);
			}
			Consts.log("party "+partyNumber+": finished sending all polynom values", Consts.DebugOutput.STDOUT);
		}
		
		private void receivePolynomValues() {
			Consts.log("party "+partyNumber+": waiting to receive all polynom values", Consts.DebugOutput.STDOUT);
			elGamal.SetPrivateAndPublicKeys(privatePolynom[0]);
			ThresholdPacket packet;
			BigIntegerMod a, b, m;
			Ciphertext c;
			for (int i=0; i<partiesAmount-1; ++i) {
				packet = receive();
				if (closing) {
					Consts.log("Party "+partyNumber+": closing party in receivePolynomValues stage.", DebugOutput.STDERR);
					return;
				}
				if (packet.type != PacketType.CIPHERTEXT) {
					Consts.log("Recieved wrong packet type - " + packet.type, DebugOutput.STDERR);
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
			Consts.log("party "+partyNumber+": got all polynom values.", Consts.DebugOutput.STDOUT);
		}

		private boolean verifyMutualPrivateKey() {
			return publicKeys[partyNumber].equals(g.pow(mutualPrivateKey));
		}

		private boolean verifyPolynomValue(int otherParty, BigIntegerMod value) {
			BigIntegerMod polynomValue = ThresholdCryptosystem.computeExponentPolynomValue(publicPolynoms[otherParty], partyNumber+1, p, q);
			return polynomValue.equals(g.pow(value));
		}
	}

	private void waitToConnect() {
		while (!isConnected()) {
			Consts.log("party "+partyNumber+":waiting for client to connect", Consts.DebugOutput.STDOUT);
			try {
				wait(2 * Consts.CONNECTION_TIMEOUT);
			} catch (InterruptedException e) {
				Consts.log(e.toString(), Consts.DebugOutput.STDERR);
			}
		}
		Consts.log("party "+partyNumber+":client connected!", Consts.DebugOutput.STDOUT);
	}

	public boolean isConnected() {
		return client.isConnected();
	}

	public static void main(String[] args) {
		if (args.length < 3) {
			Consts.log("Not enough arguments: " + args.length, Consts.DebugOutput.STDERR);
			return;
		}
		int partyNumber = Integer.parseInt(args[0]);
		int serverPort = Integer.parseInt(args[2]);
		Party party = new Party(partyNumber, args[1], serverPort);
		party.wait2Finish();
	}

}
