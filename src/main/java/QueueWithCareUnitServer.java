import cz.zcu.fav.kiv.jsim.*;

import java.util.List;

/**
 * A queue having its own server which takes transactions out from the queue.
 *
 */
public class QueueWithCareUnitServer extends JSimHead
{
	private List<BasicCareUnitServer> serverList;

	public QueueWithCareUnitServer(String name, JSimSimulation sim, List<BasicCareUnitServer> processes) throws JSimInvalidParametersException, JSimTooManyHeadsException
	{
		super(name, sim);
		this.serverList = processes;
	}

	public List<BasicCareUnitServer> getServerList() {
		return serverList;
	}

	public void setServerList(List<BasicCareUnitServer> serverList) {
		this.serverList = serverList;
	}

	public synchronized JSimLink pop() throws JSimSecurityException {
		JSimLink link = super.first();

		if (link == null) return null;

		link.out();
		return link;
	}
}
