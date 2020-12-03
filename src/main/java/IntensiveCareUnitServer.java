import cz.zcu.fav.kiv.jsim.*;

public class IntensiveCareUnitServer extends JSimProcess {

    private double mu;
    private double pDeath;
    private JSimLink patientOnBed;
    private boolean isBusy;

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
    public IntensiveCareUnitServer(String name, JSimSimulation parent, double mu, double p, boolean isBusy) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException {
        super(name, parent);
        this.mu = mu;
        this.pDeath = p;
        this.isBusy = isBusy;

        this.counter = 0;
        this.transTq = 0.0;
    }

    protected void life() {

        Patient patient;

        try
        {
            while (true) {
                // Simulating hard work here...
                hold(JSimSystem.negExp(mu));

                double rand = JSimSystem.uniform(0.0, 1.0);
                if (rand < pDeath) {
                    // death
                    message("Died on intensive care.");
                    //link.out();
                    setBusy(false);
                    setPatientOnBed(null);
                }
                else {

                    patient = (Patient) patientOnBed.getData();
                    patient.setTimeOfRequestToBasicCare(myParent.getCurrentTime());

                    message("Trying to move to basic care unit...");

                }

                passivate();



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
            }

        } // try
        catch (JSimException e)
        {
            e.printStackTrace();
            e.printComment(System.err);
        }

    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public JSimLink getPatientOnBed() {
        return patientOnBed;
    }

    public void setPatientOnBed(JSimLink patientOnBed) {
        this.patientOnBed = patientOnBed;
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
