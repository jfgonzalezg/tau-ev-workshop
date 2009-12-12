package threshold.parties;

import global.Consts;
import tcp.Server;

public class PartiesManager {

	private Server server;
	private int[] partiesNumbers;

	public PartiesManager() {
		server = new Server(Consts.THRESHOLD_CENTER_PORT);
		partiesNumbers = server.getConnectionNumbers();
		server.send(partiesNumbers[0], null); // TODO - remove this line
	}

}
