package threshold;

import global.Consts;
import tcp.Server;

public class PartiesManager {

	private Server server;
	private int[] partiesNumbers;

	public PartiesManager() {
		server = new Server(Consts.PARTIES_MANAGER_PORT);
		partiesNumbers = server.getConnectionNumbers();
		server.send(partiesNumbers[0], null); // TODO - remove this line
	}

}
