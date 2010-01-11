package threshold.parties;


import global.Consts;

public class PartiesManager extends Thread {

	int partiesAmount;
	String serverAddress;
	int serverPort;

	public PartiesManager() {
		startParties(null, null, null);
	}

	public PartiesManager(Integer partiesAmount, String serverAddress, Integer serverPort) {
		startParties(partiesAmount, serverAddress, serverPort);
	}

	private void startParties(Integer partiesAmount, String serverAddress, Integer serverPort) {
		this.partiesAmount = (partiesAmount == null) ? Consts.PARTIES_AMOUNT : partiesAmount;
		this.serverAddress = (serverAddress == null) ? Consts.THRESHOLD_CENTER_HOSTNAME : serverAddress;
		this.serverPort = (serverPort == null) ? Consts.THRESHOLD_CENTER_PORT : serverPort;
		start();
	}

	public void run() {
		Party parties[] = new Party[partiesAmount];
		for (int i=0; i<partiesAmount; ++i) {
			parties[i] = new Party(i, serverAddress, serverPort);
		}
		for (int i=0; i<partiesAmount; ++i) {
			parties[i].wait2Finish();
		}
		System.out.println("Parties manager: All parties finished");
	}

}
