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

    /** mu (gauss distribution parameter) */
    private final double mu;
    /** sigma (gauss distribution parameter) */
    private final double sigma;

    /** probability of death in basic care */
    private final double pDeath;
    /** probability of transfer to intensive care */
    private final double pFromBasicToIntensive;
    /** patient */
    private Patient patient;

    private JSimLink link;

    /** queue to basic care */
    private final BasicCareUnitQueue queue;
    /** list of intensive care servers */
    private final List<IntensiveCareUnitServer> intensiveCareUnitServerList;
    /** maximum time which can be spent in queue (if exceeded -> death) */
    private final double maxTimeInQueue;
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
     * @param parent simulation
     * @param mu mu (gauss distribution parameter)
     * @param sigma sigma (gauss distribution parameter)
     * @param pDeath probability of death in basic care
     * @param pFromBasicToIntensive probability of transfer to intensive care
     * @param queue queue to basic care
     * @param intensiveCareUnitServerList list of intensive care servers
     * @param maxTimeInQueue maximum time which can be spent in queue (if exceeded -> death)
     * @throws JSimSimulationAlreadyTerminatedException if simulation is already terminated
     * @throws JSimInvalidParametersException parent (simulation) is invalid parameter
     * @throws JSimTooManyProcessesException process cannot be added to simulation
     */
    public BasicCareUnitServer(String name, JSimSimulation parent, double mu, double sigma, double pDeath, double pFromBasicToIntensive, BasicCareUnitQueue queue, List<IntensiveCareUnitServer> intensiveCareUnitServerList, double maxTimeInQueue) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException {
        super(name, parent);
        this.mu = mu;
        this.sigma = sigma;
        this.pDeath = pDeath;
        this.pFromBasicToIntensive = pFromBasicToIntensive;
        this.queue = queue;
        this.intensiveCareUnitServerList = intensiveCareUnitServerList;
        this.maxTimeInQueue = maxTimeInQueue;

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
                        patient = (Patient)link.getData();

                        if (myParent.getCurrentTime() - patient.getTimeOfCreation() > maxTimeInQueue) {
                            message("Patient died in queue, patient: " + patient.getPatientNumber());
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
                    message("Patient moved back to basic care, patient: " + ((Patient)link.getData()).getPatientNumber());
                    patientsMovedBackFromICUCounter.incrementAndGet();
                }

                patient = (Patient)link.getData();

                setOccupied(true);

                // spend time in basic care
                double startTime = myParent.getCurrentTime();
                hold(Utils.gaussPositive(mu, sigma));
                transTq += myParent.getCurrentTime() - startTime; // time spent on bed
                counter++;

                setOccupied(false);

                // deciding where to go next
                if (patient.isDead()) {
                    if (patient.isInMoveToIntensiveCare()) {
                        message("Patient died in basic care... (no free bed in intensive care unit), patient: " + patient.getPatientNumber());
                        deadPatientsNoFreeBedInICUCounter.incrementAndGet();

                    }
                    else {
                        message("Patient died on basic care, patient: " + patient.getPatientNumber());
                        deadPatientsCounter.incrementAndGet();
                    }
                }
                else {
                    if (patient.isInMoveToIntensiveCare()) {
                        message("Patient moved to intensive care unit successfully, patient: " + patient.getPatientNumber());
                        patientsMovedToICUCounter.incrementAndGet();
                    } else {
                        message("Patient is healthy, patient: " + patient.getPatientNumber());
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

    public double getpDeath() {
        return pDeath;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public double getpFromBasicToIntensive() {
        return pFromBasicToIntensive;
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
