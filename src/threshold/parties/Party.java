package threshold.parties;

import global.Consts;
import tcp.Client;

public class Party {

	private final int partyNumber;
	private Client client;

	public Party(int partyNumber) {
		this.partyNumber = partyNumber;
		this.client = new Client(Consts.PARTIES_MANAGER_HOSTNAME, Consts.PARTIES_MANAGER_PORT, this.partyNumber);
	}

	public boolean isConnected() {
		return client.isConnected();
	}

	// TODO - find out which command line runs the party as a process, and then call Utils.runProcess(command line)
	public static void main(String[] args) {
		if (args.length > 0) {
			int i = Integer.parseInt(args[0]);
			if (i >= 0) {
				new Party(i);
			}
		} else {
			new Party(-1);
		}
	}

}
