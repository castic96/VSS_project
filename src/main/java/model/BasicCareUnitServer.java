package model;

import controller.SimulationWindowController;
import cz.zcu.fav.kiv.jsim.*;

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
                        simulationWindowController.removeLineTextAreaQueue(patient.toString());

                        if (myParent.getCurrentTime() - patient.getTimeOfCreation() > Constants.MAX_TIME_IN_QUEUE) {
                            message("Patient died in queue, patient: " + patient.getPatientNumber());
                            simulationWindowController.appendLineTextAreaDead(patient.toString());
                            simulationWindowController.removeLineTextAreaBasicCare(patient.toString());
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
                    simulationWindowController.removeLineTextAreaIntensiveCare(patient.toString());
                    patientsMovedBackFromICUCounter.incrementAndGet();
                }

                patient = (Patient) link.getData();
                simulationWindowController.appendLineTextAreaBasicCare(patient.toString());

                setOccupied(true);

                // spend time in basic care
                double startTime = myParent.getCurrentTime();
                hold(Utils.gaussPositive(Constants.BASIC_CARE_UNIT_MU, Constants.BASIC_CARE_UNIT_SIGMA));
                transTq += myParent.getCurrentTime() - startTime; // time spent on bed
                counter++;

                setOccupied(false);

                // deciding where to go next
                if (patient.isDead()) {
                    if (patient.isInMoveToIntensiveCare()) {
                        message("Patient died in basic care... (no free bed in intensive care unit), patient: " + patient.getPatientNumber());
                        simulationWindowController.appendLineTextAreaDead(patient.toString());
                        simulationWindowController.removeLineTextAreaBasicCare(patient.toString());
                        deadPatientsNoFreeBedInICUCounter.incrementAndGet();

                    }
                    else {
                        message("Patient died on basic care, patient: " + patient.getPatientNumber());
                        simulationWindowController.appendLineTextAreaDead(patient.toString());
                        simulationWindowController.removeLineTextAreaBasicCare(patient.toString());
                        deadPatientsCounter.incrementAndGet();
                    }
                }
                else {
                    if (patient.isInMoveToIntensiveCare()) {
                        message("Patient moved to intensive care unit successfully, patient: " + patient.getPatientNumber());
                        simulationWindowController.appendLineTextAreaIntensiveCare(patient.toString());
                        simulationWindowController.removeLineTextAreaBasicCare(patient.toString());
                        patientsMovedToICUCounter.incrementAndGet();
                    } else {
                        message("Patient is healthy, patient: " + patient.getPatientNumber());
                        simulationWindowController.appendLineTextAreaHealthy(patient.toString());
                        simulationWindowController.removeLineTextAreaBasicCare(patient.toString());
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

}
