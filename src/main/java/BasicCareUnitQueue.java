import cz.zcu.fav.kiv.jsim.*;

import java.util.List;

/**
 * Basic care queue with multiple servers (beds).
 */
public class BasicCareUnitQueue extends JSimHead
{
	/** list of basic unit servers */
	private List<BasicCareUnitServer> serverList;

	/**
	 * Creates new queue with servers
	 *
	 * @param name queue name
	 * @param simulation simulation
	 * @param servers server list
	 * @throws JSimInvalidParametersException parent (simulation) is invalid parameter
	 * @throws JSimTooManyHeadsException queue cannot be added to simulation
	 */
	public BasicCareUnitQueue(String name, JSimSimulation simulation, List<BasicCareUnitServer> servers) throws JSimInvalidParametersException, JSimTooManyHeadsException
	{
		super(name, simulation);
		this.serverList = servers;
	}

	/**
	 * Returns servers.
	 *
	 * @return servers
	 */
	public List<BasicCareUnitServer> getServerList() {
		return serverList;
	}

	/**
	 * Sets servers.
	 *
	 * @param serverList servers
	 */
	public void setServerList(List<BasicCareUnitServer> serverList) {
		this.serverList = serverList;
	}

	/**
	 * Removes first element from queue and returns it.
	 *
	 * @return first element in queue
	 * @throws JSimSecurityException if element to remove is not inside queue
	 */
	public synchronized JSimLink pop() throws JSimSecurityException {
		JSimLink link = super.first();

		if (link == null) return null;

		link.out();
		return link;
	}
}
