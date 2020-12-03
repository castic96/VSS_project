import cz.zcu.fav.kiv.jsim.*;

/**
 * Input data (patients) generator.
 */
public class InputGenerator extends JSimProcess {

    /** lambda (Poisson distribution) */
    private double lambda;
    /** queue */
    private BasicCareUnitQueue queue;

    /**
     * Creates new generator.
     *
     * @param name generator name
     * @param simulation simulation
     * @param lambda lambda (Poisson distribution)
     * @param queue queue
     * @throws JSimSimulationAlreadyTerminatedException if simulation is already terminated
     * @throws JSimInvalidParametersException parent (simulation) is invalid parameter
     * @throws JSimTooManyProcessesException process cannot be added to simulation
     */
    public InputGenerator(String name, JSimSimulation simulation, double lambda, BasicCareUnitQueue queue) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException
    {
        super(name, simulation);
        this.lambda = lambda;
        this.queue = queue;
    }

    /**
     * Generating.
     */
    protected void life()
    {
        JSimLink link;

        try
        {
            while (true)
            {
                // generate patient and put him into queue
                link = new JSimLink(new Patient(myParent.getCurrentTime()));
                link.into(queue);

                // find free bed in basic care for patient
                for (JSimProcess currentServer : queue.getServerList()) {
                    if (currentServer.isIdle()) {
                        currentServer.activate(myParent.getCurrentTime());
                        break;
                    }
                }

                // wait before generating another patient
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
