package model;

import controller.SimulationWindowController;
import cz.zcu.fav.kiv.jsim.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents one bed in basic care unit.
 */
public class BasicCareUnitServer extends JSimProcess {

    /** Counter for patients who die in basic care unit. */
    private static final AtomicInteger deadPatientsCounter = new AtomicInteger(0);
    /** Counter for patients who die in basic care unit because they needed to move to ICU but there was no free bed. */
    private static final AtomicInteger deadPatientsNoFreeBedInICUCounter = new AtomicInteger(0);
    /** Counter for patients who moved to ICU. */
    private static final AtomicInteger patientsMovedToICUCounter = new AtomicInteger(0);
    /** Counter for patients who moved back from ICU. */
    private static final AtomicInteger patientsMovedBackFromICUCounter = new AtomicInteger(0);
    /** Counter for patients who are leaving hospital as healthy. */
    private static final AtomicInteger healedPatientsCounter = new AtomicInteger(0);
    /** Counter for patients who died in queue. */
    private static final AtomicInteger diedInQueuePatientsCounter = new AtomicInteger(0);

    private static final List<Double> deadPatientsTimes = new LinkedList<>();
    private static final List<Double> deadPatientsNoFreeBedInICUTimes = new LinkedList<>();
    private static final List<Double> patientsMovedToICUTimes = new LinkedList<>();
    private static final List<Double> patientsMovedBackFromICUTimes = new LinkedList<>();
    private static final List<Double> healedPatientsTimes = new LinkedList<>();
    private static final List<Double> diedInQueuePatientsTimes = new LinkedList<>();

    private final List<Double> inTimes = new LinkedList<>();
    private final List<Double> outTimes = new LinkedList<>();

    /** patient */
    private Patient patient;

    private JSimLink link;

    private SimulationWindowController simulationWindowController;

    /** queue to basic care */
    private final BasicCareUnitQueue queue;
    /** list of intensive care servers */
    private final List<IntensiveCareUnitServer> intensiveCareUnitServerList;
    /** if server (bed) is occupied */
    private boolean occupied;

    /** counter for patients on this server (bed) */
    private int counter;
    /** time spent on this server (bed) */
    private double transTq;

    /**
     * Creates new server in basic care unit.
     *
     * @param name server name
     * @param program program
     * @param queue queue to basic care
     * @param intensiveCareUnitServerList list of intensive care servers
     * @throws JSimSimulationAlreadyTerminatedException if simulation is already terminated
     * @throws JSimInvalidParametersException parent (simulation) is invalid parameter
     * @throws JSimTooManyProcessesException process cannot be added to simulation
     */
    public BasicCareUnitServer(String name, Program program, BasicCareUnitQueue queue, List<IntensiveCareUnitServer> intensiveCareUnitServerList) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException {
        super(name, program.getSimulation());
        this.queue = queue;
        this.intensiveCareUnitServerList = intensiveCareUnitServerList;
        this.simulationWindowController = program.getSimulationWindowController();

        this.counter = 0;
        this.transTq = 0.0;
    }

    /**
     * Server simulation.
     */
    protected void life() {

        try
        {
            while (true)
            {
                // check if someone in intensive care wants to return to basic care
                link = waitingForBasicCare();

                if (link == null) {
                    link = queue.pop();

                    if (link != null) {
                        patient = (Patient) link.getData();

                        if (Constants.IS_STEP_BY_STEP) {
                            simulationWindowController.removeLineTextAreaQueue(patient.toString());
                        }

                        if (myParent.getCurrentTime() - patient.getTimeOfCreation() > Constants.MAX_TIME_IN_QUEUE) {
                            message("Patient died in queue, patient: " + patient.getPatientNumber());

                            if (Constants.IS_STEP_BY_STEP) {
                                simulationWindowController.appendLineTextAreaDead(patient.toString());
                                simulationWindowController.removeLineTextAreaBasicCare(patient.toString());
                            }

                            diedInQueuePatientsTimes.add(myParent.getCurrentTime());
                            diedInQueuePatientsCounter.incrementAndGet();
                            continue;
                        }

                    }
                    else {
                        passivate();
                        continue;
                    }

                }
                else {
                    patient = (Patient) link.getData();
                    message("Patient moved back to basic care, patient: " + patient.getPatientNumber());

                    if (Constants.IS_STEP_BY_STEP) {
                        simulationWindowController.removeLineTextAreaIntensiveCare(patient.toString());
                    }

                    patientsMovedBackFromICUTimes.add(myParent.getCurrentTime());
                    patientsMovedBackFromICUCounter.incrementAndGet();
                }

                patient = (Patient) link.getData();

                if (Constants.IS_STEP_BY_STEP) {
                    simulationWindowController.appendLineTextAreaBasicCare(patient.toString());
                }

                inTimes.add(myParent.getCurrentTime());
                setOccupied(true);

                // spend time in basic care
                double startTime = myParent.getCurrentTime();
                hold(Utils.gaussPositive(Constants.BASIC_CARE_UNIT_MU, Constants.BASIC_CARE_UNIT_SIGMA));
                transTq += myParent.getCurrentTime() - startTime; // time spent on bed
                counter++;

                setOccupied(false);
                outTimes.add(myParent.getCurrentTime());

                // deciding where to go next
                if (patient.isDead()) {
                    if (patient.isInMoveToIntensiveCare()) {
                        message("Patient died in basic care... (no free bed in intensive care unit), patient: " + patient.getPatientNumber());

                        if (Constants.IS_STEP_BY_STEP) {
                            simulationWindowController.appendLineTextAreaDead(patient.toString());
                            simulationWindowController.removeLineTextAreaBasicCare(patient.toString());
                        }

                        deadPatientsNoFreeBedInICUTimes.add(myParent.getCurrentTime());
                        deadPatientsNoFreeBedInICUCounter.incrementAndGet();

                    }
                    else {
                        message("Patient died on basic care, patient: " + patient.getPatientNumber());

                        if (Constants.IS_STEP_BY_STEP) {
                            simulationWindowController.appendLineTextAreaDead(patient.toString());
                            simulationWindowController.removeLineTextAreaBasicCare(patient.toString());
                        }

                        deadPatientsTimes.add(myParent.getCurrentTime());
                        deadPatientsCounter.incrementAndGet();
                    }
                }
                else {
                    if (patient.isInMoveToIntensiveCare()) {
                        message("Patient moved to intensive care unit successfully, patient: " + patient.getPatientNumber());

                        if (Constants.IS_STEP_BY_STEP) {
                            simulationWindowController.appendLineTextAreaIntensiveCare(patient.toString());
                            simulationWindowController.removeLineTextAreaBasicCare(patient.toString());
                        }

                        patientsMovedToICUTimes.add(myParent.getCurrentTime());
                        patientsMovedToICUCounter.incrementAndGet();

                    } else {
                        message("Patient is healthy, patient: " + patient.getPatientNumber());

                        if (Constants.IS_STEP_BY_STEP) {
                            simulationWindowController.appendLineTextAreaHealthy(patient.toString());
                            simulationWindowController.removeLineTextAreaBasicCare(patient.toString());
                        }

                        healedPatientsTimes.add(myParent.getCurrentTime());
                        healedPatientsCounter.incrementAndGet();
                    }
                }

            }
        }
        catch (JSimException e) {
            e.printStackTrace();
            e.printComment(System.err);
        }

    }

    /**
     * Check if someone in intensive care wants to return to basic care unit.
     *
     * @return patient from intensive care unit (or null)
     */
    private synchronized JSimLink waitingForBasicCare() {
        Patient currentPatient;
        IntensiveCareUnitServer firstServerRequest = null;
        JSimLink firstRequestPatientLink;
        double firstPatientRequestTime = Long.MAX_VALUE;

        for (IntensiveCareUnitServer currentServer : intensiveCareUnitServerList) {

            if (currentServer.isOccupied() && currentServer.isIdle()) { // occupied but idle == waiting for basic care
                currentPatient = (Patient)currentServer.getPatientOnBed().getData();

                if (currentPatient.getTimeOfRequestToBasicCare() < firstPatientRequestTime) {
                    firstPatientRequestTime = currentPatient.getTimeOfRequestToBasicCare();
                    firstServerRequest = currentServer;
                }

            }
        }

        if (firstServerRequest == null) {
            return null;
        }

        firstRequestPatientLink = firstServerRequest.getPatientOnBed();
        //((Patient)firstRequestPatientLink.getData()).setInMoveToIntensiveCare(false);

        firstServerRequest.setPatientOnBed(null);
        firstServerRequest.setOccupied(false);
        firstServerRequest.addOutTime(myParent.getCurrentTime());

        return firstRequestPatientLink;
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public JSimLink getLink() {
        return link;
    }

    public static int getDeadPatientsCounter() {
        return deadPatientsCounter.get();
    }

    public static int getDeadPatientsNoFreeBedInICUCounter() {
        return deadPatientsNoFreeBedInICUCounter.get();
    }

    public static int getDiedInQueuePatientsCounter() {
        return diedInQueuePatientsCounter.get();
    }

    public static int getHealedPatientsCounter() {
        return healedPatientsCounter.get();
    }

    public static int getPatientsMovedBackFromICUCounter() {
        return patientsMovedBackFromICUCounter.get();
    }

    public static int getPatientsMovedToICUCounter() {
        return patientsMovedToICUCounter.get();
    }

    public static void setDeadPatientsCounter(int value) {
        deadPatientsCounter.set(value);
    }

    public static void setDeadPatientsNoFreeBedInICUCounter(int value) {
        deadPatientsNoFreeBedInICUCounter.set(value);
    }

    public static void setPatientsMovedToICUCounter(int value) {
        patientsMovedToICUCounter.set(value);
    }

    public static void setPatientsMovedBackFromICUCounter(int value) {
        patientsMovedBackFromICUCounter.set(value);
    }

    public static void setHealedPatientsCounter(int value) {
        healedPatientsCounter.set(value);
    }

    public static void setDiedInQueuePatientsCounter(int value) {
        diedInQueuePatientsCounter.set(value);
    }

    public List<Double> getInTimes() {
        return inTimes;
    }

    public List<Double> getOutTimes() {
        return outTimes;
    }

    public static void clearDeadPatientsTimes() {
        deadPatientsTimes.clear();
    }

    public static void clearDeadPatientsNoFreeBedInICUTimes() {
        deadPatientsNoFreeBedInICUTimes.clear();
    }

    public static void clearPatientsMovedToICUTimes() {
        patientsMovedToICUTimes.clear();
    }

    public static void clearPatientsMovedBackFromICUTimes() {
        patientsMovedBackFromICUTimes.clear();
    }

    public static void clearHealedPatientsTimes() {
        healedPatientsTimes.clear();
    }

    public static void clearDiedInQueuePatientsTimes() {
        diedInQueuePatientsTimes.clear();
    }

    public static List<Double> getDeadPatientsTimes() {
        return deadPatientsTimes;
    }

    public static List<Double> getDeadPatientsNoFreeBedInICUTimes() {
        return deadPatientsNoFreeBedInICUTimes;
    }

    public static List<Double> getPatientsMovedToICUTimes() {
        return patientsMovedToICUTimes;
    }

    public static List<Double> getPatientsMovedBackFromICUTimes() {
        return patientsMovedBackFromICUTimes;
    }

    public static List<Double> getHealedPatientsTimes() {
        return healedPatientsTimes;
    }

    public static List<Double> getDiedInQueuePatientsTimes() {
        return diedInQueuePatientsTimes;
    }
}
