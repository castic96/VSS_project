import cz.zcu.fav.kiv.jsim.JSimHead;
import cz.zcu.fav.kiv.jsim.JSimInvalidParametersException;
import cz.zcu.fav.kiv.jsim.JSimProcess;
import cz.zcu.fav.kiv.jsim.JSimSimulation;
import cz.zcu.fav.kiv.jsim.JSimTooManyHeadsException;

/**
 * A queue having its own server which takes transactions out from the queue.
 *
 */
public class QueueWithServer extends JSimHead
{
	private JSimProcess server;

	public QueueWithServer(String name, JSimSimulation sim, JSimProcess process) throws JSimInvalidParametersException, JSimTooManyHeadsException
	{
		super(name, sim);
		server = process;
	} // constructor

	public JSimProcess getServer()
	{
		return server;
	} // getServer

	public void setServer(JSimProcess s)
	{
		server = s;
	} // setServer

} // class QueueWithServer
