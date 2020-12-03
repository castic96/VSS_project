import cz.zcu.fav.kiv.jsim.*;

public class IntensiveCareUnitServer extends JSimProcess {

    private double mu;
    private double pDeath;

    private int counter;
    private double transTq;

    /**
     * Creates a new process having a name and belonging to a simulation. If the simulation is already terminated or there is no free number
     * the process is not created and an exception is thrown out. All processes must have their parent simulation object specified. If not,
     * an exception is thrown, too. After the creation, the process is not scheduled and therefore will not run unless explicitely activated
     * by another process or the main program using activate().
     *
     * @param name   Name of the process being created.
     * @param parent Parent simulation.
     * @throws JSimSimulationAlreadyTerminatedException This exception is thrown out if the simulation has already been terminated.
     * @throws JSimInvalidParametersException           This exception is thrown out if no parent was specified.
     * @throws JSimTooManyProcessesException            This exception is thrown out if no other process can be added to the simulation specified.
     * @throws JSimKernelPanicException                 This exception is thrown out if the simulation is in a unknown state. Do NOT catch this exception!
     */
    public IntensiveCareUnitServer(String name, JSimSimulation parent, double mu, double p) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException {
        super(name, parent);
        this.mu = mu;
        this.pDeath = p;

        this.counter = 0;
        this.transTq = 0.0;
    }

    protected void life() {

        Patient t;
        JSimLink link;

        try
        {
            while (true)
            {
                {
                    // Simulating hard work here...
                    hold(JSimSystem.negExp(mu));
                    double rand = JSimSystem.uniform(0.0, 1.0);

                    if (rand < pDeath) {
                        // death
                        System.out.println("Died.");
                        link = null;

                    } else {
                        // healthy - goes home
                        System.out.println("Healthy.");
                        link = null;
                    }

                    // Now we must decide whether to throw the transaction away or to insert it into another queue.
                    /*if (JSimSystem.uniform(0.0, 1.0) > p1)
                    {
                        t = (Patient) link.getData();
                        counter++;
                        transTq += myParent.getCurrentTime() - t.getCreationTime();
                        link.out();
                        link = null;
                    }
                    else
                    {
                        link.out();
                        link.into(queueOut);
                        if (queueOut.getServer().isIdle())
                            queueOut.getServer().activate(myParent.getCurrentTime());
                    } // else throw away / insert */
                } // else queue is empty / not empty
            } // while
        } // try
        catch (JSimException e)
        {
            e.printStackTrace();
            e.printComment(System.err);
        }

    }

    public int getCounter()
    {
        return counter;
    } // getCounter

    public double getTransTq()
    {
        return transTq;
    } // getTransTq

}
