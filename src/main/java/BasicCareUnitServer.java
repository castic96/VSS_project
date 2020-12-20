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
                }

                patient = (Patient)link.getData();

                setOccupied(true);

                // spend time in basic care
                hold(Utils.gaussPositive(mu, sigma));

                setOccupied(false);

                // deciding where to go next
                if (patient.isDied()) {
                    if (patient.isInMoveToIntensiveCare()) {
                        message("Patient died in basic care... (no free bed in intensive care unit), patient: " + patient.getPatientNumber());
                    }
                    else {
                        message("Patient died on basic care, patient: " + patient.getPatientNumber());
                    }
                }
                else {
                    if (patient.isInMoveToIntensiveCare()) {
                        message("Patient moved to intensive care unit successfully, patient: " + patient.getPatientNumber());
                    } else {
                        message("Patient is healthy, patient: " + patient.getPatientNumber());
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
}
