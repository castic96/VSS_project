import cz.zcu.fav.kiv.jsim.*;

public class InputGenerator extends JSimProcess {

    private double lambda;
    private QueueWithServer queue;

    public InputGenerator(String name, JSimSimulation sim, double l, QueueWithServer q) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException
    {
        super(name, sim);
        lambda = l;
        queue = q;
    } // constructor

    protected void life()
    {
        JSimLink link;

        try
        {
            while (true)
            {
                // Periodically creating new transactions and putting them into the queue.
                link = new JSimLink(new Patient());
                link.into(queue);
                if (queue.getServer().isIdle())
                    queue.getServer().activate(myParent.getCurrentTime());

                hold(JSimSystem.negExp(lambda)); // todo Poisson distribution
            } // while
        } // try
        catch (JSimException e)
        {
            e.printStackTrace();
            e.printComment(System.err);
        } // catch
    } // life

}
