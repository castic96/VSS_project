import cz.zcu.fav.kiv.jsim.*;

import java.util.List;

/**
 * A queue having its own server which takes transactions out from the queue.
 *
 */
public class QueueWithCareUnitServer extends JSimHead
{
	private List<JSimProcess> serverList;

	public QueueWithCareUnitServer(String name, JSimSimulation sim, List<JSimProcess> processes) throws JSimInvalidParametersException, JSimTooManyHeadsException
	{
		super(name, sim);
		this.serverList = processes;
	}

	public List<JSimProcess> getServerList() {
		return serverList;
	}

	public void setServerList(List<JSimProcess> serverList) {
		this.serverList = serverList;
	}

	@Override
	public synchronized JSimLink first() {
		JSimLink link = super.first();

		if (link == null) return null;

		try {
			link.out();
		} catch (JSimSecurityException e) {
			System.out.println("error"); // todo
			e.printStackTrace();
		}
		return link;
	}
}