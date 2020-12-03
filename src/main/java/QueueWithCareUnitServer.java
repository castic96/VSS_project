import cz.zcu.fav.kiv.jsim.JSimHead;
import cz.zcu.fav.kiv.jsim.JSimInvalidParametersException;
import cz.zcu.fav.kiv.jsim.JSimProcess;
import cz.zcu.fav.kiv.jsim.JSimSimulation;
import cz.zcu.fav.kiv.jsim.JSimTooManyHeadsException;

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
}
