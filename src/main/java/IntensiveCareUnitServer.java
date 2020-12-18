import cz.zcu.fav.kiv.jsim.*;

/**
 * Represents one bed in intensive care unit.
 */
public class IntensiveCareUnitServer extends JSimProcess {

    /** mu (exponential distribution parameter) */
    private double mu;
    /** probability of death in intensive care */
    private double pDeath;
    /** patient */
    private JSimLink patientOnBed;
    /** if server (bed) is occupied */
    private boolean occupied;

    /** counter for patients on this server (bed) */
    private int counter;
    /** time spent on this server (bed) */
    private double transTq;

    /**
     * Creates new server in intensive care unit.
     *
     * @param name server name
     * @param parent simulation
     * @param mu mu (exponential distribution parameter)
     * @param pDeath probability of death in intensive care
     * @param occupied if server (bed) is occupied
     * @throws JSimSimulationAlreadyTerminatedException if simulation is already terminated
     * @throws JSimInvalidParametersException parent (simulation) is invalid parameter
     * @throws JSimTooManyProcessesException process cannot be added to simulation
     */
    public IntensiveCareUnitServer(String name, JSimSimulation parent, double mu, double pDeath, boolean occupied) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException {
        super(name, parent);
        this.mu = mu;
        this.pDeath = pDeath;
        this.occupied = occupied;

        this.counter = 0;
        this.transTq = 0.0;
    }

    /**
     * Server simulation.
     */
    protected void life() {

        Patient patient;

        try
        {
            while (true) {
                // spend time in intensive care
                hold(JSimSystem.negExp(mu));

                setOccupied(false);

                patient = (Patient) patientOnBed.getData();

                if (patient.isDied()) {
                    message("Patient died on intensive care.");
                    setPatientOnBed(null);

                    counter++;
                    transTq += myParent.getCurrentTime() - patient.getTimeOfCreation();

                }
                else {
                    patient.setTimeOfRequestToBasicCare(myParent.getCurrentTime());

                    message("Trying to move to basic care unit...");

                    counter++;
                    transTq += myParent.getCurrentTime() - patient.getTimeOfCreation();

                    setOccupied(true);
                }

                passivate();
            }

        }
        catch (JSimException e)
        {
            e.printStackTrace();
            e.printComment(System.err);
        }

    }

    /**
     * Returns if server (bed) is occupied.
     *
     * @return is occupied
     */
    public boolean isOccupied() {
        return occupied;
    }

    /**
     * Sets if server (bed) is occupied.
     *
     * @param occupied true if occupied
     */
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    /**
     * Returns patient.
     *
     * @return patient
     */
    public JSimLink getPatientOnBed() {
        return patientOnBed;
    }

    /**
     * Sets patient.
     *
     * @param patientOnBed patient
     */
    public void setPatientOnBed(JSimLink patientOnBed) {
        this.patientOnBed = patientOnBed;
    }

    /**
     * Returns counter for patients on this server (bed).
     *
     * @return counter
     */
    public int getCounter()
    {
        return counter;
    }

    /**
     * Returns time spent on this server (bed).
     *
     * @return time
     */
    public double getTransTq()
    {
        return transTq;
    }

    public double getpDeath() {
        return pDeath;
    }

    public void setpDeath(double pDeath) {
        this.pDeath = pDeath;
    }
}
