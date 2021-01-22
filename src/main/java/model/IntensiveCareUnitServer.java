package model;

import controller.SimulationWindowController;
import cz.zcu.fav.kiv.jsim.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents one bed in intensive care unit.
 */
public class IntensiveCareUnitServer extends JSimProcess {

    // ----- STATIC FINAL VARIABLES -----

    /** Counter for patients who die in intensive care unit. */
    private static final AtomicInteger deadPatientsCounter = new AtomicInteger(0);
    /** Death times of patients who died in intensive care unit. */
    private static final List<Double> deadPatientsTimes = new LinkedList<>();

    // ----- INSTANCE FINAL VARIABLES -----

    /** Arrival times of patients. */
    private final List<Double> inTimes = new LinkedList<>();
    /** Leaving times of patients. */
    private final List<Double> outTimes = new LinkedList<>();

    /** Queue to basic care. */
    private final BasicCareUnitQueue queue;

    /** Simulation window controller. */
    private final SimulationWindowController simulationWindowController;

    // ----- INSTANCE VARIABLES -----

    /** Patient */
    private JSimLink patientOnBed;
    /** If server (bed) is occupied. */
    private boolean occupied;

    /** Counter for patients on this server (bed). */
    private int counter;
    /** Time spent on this server (bed). */
    private double transTq;


    /**
     * Creates new server in intensive care unit.
     *
     * @param name server name
     * @param program program
     * @param occupied if server (bed) is occupied
     * @throws JSimSimulationAlreadyTerminatedException if simulation is already terminated
     * @throws JSimInvalidParametersException parent (simulation) is invalid parameter
     * @throws JSimTooManyProcessesException process cannot be added to simulation
     */
    public IntensiveCareUnitServer(String name, Program program, boolean occupied, BasicCareUnitQueue queue) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException {
        super(name, program.getSimulation());
        this.occupied = occupied;
        this.queue = queue;
        this.simulationWindowController = program.getSimulationWindowController();

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

                inTimes.add(myParent.getCurrentTime());
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

                    if (Constants.IS_STEP_BY_STEP) {
                        simulationWindowController.appendLineTextAreaDead(patient.toString());
                        simulationWindowController.removeLineTextAreaIntensiveCare(patient.toString());
                    }

                    double time = myParent.getCurrentTime();
                    outTimes.add(time);
                    deadPatientsTimes.add(time);
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

    /**
     * Returns counter for patients who die in intensive care unit.
     *
     * @return dead patients counter
     */
    public static int getDeadPatientsCounter() {
        return deadPatientsCounter.get();
    }

    /**
     * Sets counter for patients who die in intensive care unit.
     *
     * @param value new number value
     */
    public static void setDeadPatientsCounter(int value) {
        deadPatientsCounter.set(value);
    }

    /**
     * Adds time to leaving times of patients.
     *
     * @param time time when patients left intensive care
     */
    public void addOutTime(double time) {
        this.outTimes.add(time);
    }

    /**
     * Returns arrival times of patients.
     *
     * @return arrival times
     */
    public List<Double> getInTimes() {
        return inTimes;
    }

    /**
     * Returns leaving times of patients.
     *
     * @return leaving times
     */
    public List<Double> getOutTimes() {
        return outTimes;
    }

    /**
     * Clears list with death times of patients who died in intensive care unit.
     */
    public static void clearDeadPatientsTimes() {
        deadPatientsTimes.clear();
    }

    /**
     * Returns death times of patients who died in intensive care unit.
     *
     * @return dead patients times
     */
    public static List<Double> getDeadPatientsTimes() {
        return deadPatientsTimes;
    }
}
