import cz.zcu.fav.kiv.jsim.*;

public class InputGenerator extends JSimProcess {

    private double lambda;
    private QueueWithCareUnitServer queue;

    public InputGenerator(String name, JSimSimulation sim, double l, QueueWithCareUnitServer q) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException
    {
        super(name, sim);
        lambda = l;
        queue = q;
    }

    protected void life()
    {
        JSimLink link;

        try
        {
            while (true)
            {
                // Periodically creating new transactions and putting them into the queue.
                link = new JSimLink(new Patient(myParent.getCurrentTime()));
                link.into(queue);

                // Je vygenerovan pacient
                // Projdu vsechny dostupne servery pro tu prvni frontu (vsechna luzka zakladni pece)
                // Pokud najdu nejaky server (luzko), ktery neni aktivni, tak ho probudim a zastavim foreach (protoze chci aktivovat jen jeden server (luzko))
                for (JSimProcess currentServer : queue.getServerList()) {
                    if (currentServer.isIdle()) {
                        currentServer.activate(myParent.getCurrentTime());
                        break;
                    }
                }

                hold(JSimSystem.negExp(lambda)); // todo Poisson distribution
            }
        }
        catch (JSimException e)
        {
            e.printStackTrace();
            e.printComment(System.err);
        }
    }

}
