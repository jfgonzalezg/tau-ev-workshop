package parties;

import tcp.Client;

public class Party {

	private final int partyNumber;
	private Client client;

	public Party(int partyNumber) {
		this.partyNumber = partyNumber;
		this.client = new Client(globals.GlobalConsts.PARTIES_MANAGER_HOSTNAME, globals.GlobalConsts.PARTIES_MANAGER_PORT, this.partyNumber);
	}

	public boolean isConnected() {
		return client.isConnected();
	}

}
