import cz.zcu.fav.kiv.jsim.*;

import java.util.List;

/**
 * Represents one bed in basic care unit.
 */
public class BasicCareUnitServer extends JSimProcess {

    /** mu (gauss distribution parameter) */
    private final double mu;
    /** sigma (gauss distribution parameter) */
    private final double sigma;

    /** probability of death in basic care */
    private final double pDeath;
    /** probability of transfer to intensive care */
    private final double pFromBasicToIntensive;

    /** queue to basic care */
    private final BasicCareUnitQueue queue;
    /** list of intensive care servers */
    private final List<IntensiveCareUnitServer> intensiveCareUnitServerList;
    /** maximum time which can be spent in queue (if exceeded -> death) */
    private final double maxTimeInQueue;

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

        Patient patient;
        JSimLink link;

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
                            message("Patient died in queue.");
                            continue;
                        }

                    }
                    else {
                        passivate();
                        continue;
                    }

                }
                else {
                    message("Patient moved back to basic care.");
                }

                patient = (Patient)link.getData();

                // spend time in basic care
                hold(Utils.gaussPositive(mu, sigma));

                // deciding where to go next
                double rand = JSimSystem.uniform(0.0, 1.0);
                if (rand < pDeath) { // death
                    message("Patient died in basic care.");
                }
                else {
                    rand = JSimSystem.uniform(0.0, 1.0);
                    if (rand < pFromBasicToIntensive) { // move to intensive care unit (if not possible -> death)
                        message("Trying to move to intensive care unit...");

                        if (!moveToIntensiveCare(link)) {
                            message("Patient died in basic care... (no free bed in intensive care unit).");
                        }
                        else {
                            message("Patient moved to intensive care unit successfully.");
                        }

                    } else { // healthy -> goes home
                        message("Patient is healthy.");
                    }
                }

                counter++;
                transTq += myParent.getCurrentTime() - patient.getTimeOfCreation();
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

                if (currentServer.isOccupied() && currentServer.isIdle()) {
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

        firstServerRequest.setPatientOnBed(null);
        firstServerRequest.setOccupied(false);

        return firstRequestPatientLink;
    }

    /**
     * Tries to move patient to intensive care.
     * Returns true if transfer has been successful.
     *
     * @param link patient
     * @return true if patient has been successfully transferred to ICU (false otherwise)
     * @throws JSimInvalidParametersException if problem with process activation occurs
     * @throws JSimSecurityException if problem with process activation occurs
     */
    private synchronized boolean moveToIntensiveCare(JSimLink link) throws JSimInvalidParametersException, JSimSecurityException {

        for (IntensiveCareUnitServer currentServer : intensiveCareUnitServerList) {

                if (!currentServer.isOccupied()) {
                    currentServer.setPatientOnBed(link);
                    currentServer.setOccupied(true);
                    currentServer.activate(myParent.getCurrentTime());

                    return true;
                }
        }

        return false;
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

}
