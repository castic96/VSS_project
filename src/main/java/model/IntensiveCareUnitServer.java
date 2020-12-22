package model;

import cz.zcu.fav.kiv.jsim.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents one bed in intensive care unit.
 */
public class IntensiveCareUnitServer extends JSimProcess {

    /** Counter for patients who die in intensive care unit. */
    private static final AtomicInteger deadPatientsCounter = new AtomicInteger(0);

    /** patient */
    private JSimLink patientOnBed;
    /** if server (bed) is occupied */
    private boolean occupied;

    /** counter for patients on this server (bed) */
    private int counter;
    /** time spent on this server (bed) */
    private double transTq;
    /** queue to basic care */
    private final BasicCareUnitQueue queue;

    /**
     * Creates new server in intensive care unit.
     *
     * @param name server name
     * @param parent simulation
     * @param occupied if server (bed) is occupied
     * @throws JSimSimulationAlreadyTerminatedException if simulation is already terminated
     * @throws JSimInvalidParametersException parent (simulation) is invalid parameter
     * @throws JSimTooManyProcessesException process cannot be added to simulation
     */
    public IntensiveCareUnitServer(String name, JSimSimulation parent, boolean occupied, BasicCareUnitQueue queue) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException {
        super(name, parent);
        this.occupied = occupied;
        this.queue = queue;

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

                patient = (Patient) patientOnBed.getData();
                patient.setInMoveToIntensiveCare(false);

                // spend time in intensive care
                double startTime = myParent.getCurrentTime();
                hold(JSimSystem.negExp(Constants.INTENSIVE_CARE_UNIT_MU));
                transTq += myParent.getCurrentTime() - startTime; // time spent on bed
                counter++;

                setOccupied(false);

                if (patient.isDead()) {
                    message("Patient died on intensive care, patient: " + patient.getPatientNumber());
                    deadPatientsCounter.incrementAndGet();
                    setPatientOnBed(null);

                }
                else {
                    patient.setTimeOfRequestToBasicCare(myParent.getCurrentTime());
                    message("Trying to move to basic care unit..., patient: " + patient.getPatientNumber());
                    activateBasicCareUnitServer();
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

    private void activateBasicCareUnitServer() throws JSimInvalidParametersException, JSimSecurityException {
        // find free bed in basic care for patient
        for (JSimProcess currentServer : queue.getServerList()) {
            if (currentServer.isIdle()) {
                currentServer.activate(myParent.getCurrentTime());
                break;
            }
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

    public static int getDeadPatientsCounter() {
        return deadPatientsCounter.get();
    }
}
