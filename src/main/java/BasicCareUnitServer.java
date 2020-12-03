import cz.zcu.fav.kiv.jsim.*;

import java.util.List;

public class BasicCareUnitServer extends JSimProcess {

    private final double mu;
    private final double sigma;
    private final double pDeath;
    private final double pFromBasicToIntensive;
    private final QueueWithCareUnitServer queue;
    private final List<IntensiveCareUnitServer> intensiveCareUnitServerList;
    private final double maxTimeInQueue;

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
     * @param intensiveCareUnitServerList
     * @throws JSimSimulationAlreadyTerminatedException This exception is thrown out if the simulation has already been terminated.
     * @throws JSimInvalidParametersException           This exception is thrown out if no parent was specified.
     * @throws JSimTooManyProcessesException            This exception is thrown out if no other process can be added to the simulation specified.
     * @throws JSimKernelPanicException                 This exception is thrown out if the simulation is in a unknown state. Do NOT catch this exception!
     */
    public BasicCareUnitServer(String name, JSimSimulation parent, double mu, double sigma, double p1, double p2, QueueWithCareUnitServer queue, List<IntensiveCareUnitServer> intensiveCareUnitServerList, double maxTimeInQueue) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException {
        super(name, parent);
        this.mu = mu;
        this.sigma = sigma;
        this.pDeath = p1;
        this.pFromBasicToIntensive = p2;
        this.queue = queue;
        this.intensiveCareUnitServerList = intensiveCareUnitServerList;
        this.maxTimeInQueue = maxTimeInQueue;

        this.counter = 0;
        this.transTq = 0.0;
    }

    protected void life() {

        Patient patient;
        JSimLink link;

        try
        {
            while (true)
            {

                link = waitingForBasicCare();

                if (link == null) {
                    link = queue.pop();

                    if (link != null) {
                        patient = (Patient)link.getData();

                        if (myParent.getCurrentTime() - patient.getTimeOfCreation() > maxTimeInQueue) {
                            message("Died in queue.");
                            continue;
                        }

                    }
                    else {
                        passivate();
                        continue;
                    }

                }
                else {
                    message("Moved back to basic care.");
                }

                patient = (Patient)link.getData();

                // spend time in basic care
                hold(Utils.gaussPositive(mu, sigma));

                // deciding where to go next
                double rand = JSimSystem.uniform(0.0, 1.0);
                if (rand < pDeath) { // death
                    message("Died on basic care.");
                }
                else {
                    rand = JSimSystem.uniform(0.0, 1.0);
                    if (rand < pFromBasicToIntensive) { // move to intensive care unit (if not possible -> death)
                        message("Trying to move to intensive care unit...");

                        if (!moveToIntensiveCare(link)) {
                            message("Patient died... No necessary bed on intensive care unit.");
                        }
                        else {
                            message("Patient moved to intensive care unit successfully.");
                        }

                    } else { // healthy -> goes home
                        message("Healthy.");
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

    private synchronized JSimLink waitingForBasicCare() {

        Patient currentPatient;
        IntensiveCareUnitServer firstServerRequest = null;
        JSimLink firstRequestPatientLink;
        double firstPatientRequestTime = Long.MAX_VALUE;

        for (IntensiveCareUnitServer currentServer : intensiveCareUnitServerList) {

                if (currentServer.isBusy() && currentServer.isIdle()) {
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
        firstServerRequest.setBusy(false);

        return firstRequestPatientLink;
    }

    private synchronized boolean moveToIntensiveCare(JSimLink link) throws JSimInvalidParametersException, JSimSecurityException {

        for (IntensiveCareUnitServer currentServer : intensiveCareUnitServerList) {

                if (!currentServer.isBusy()) {
                    currentServer.setPatientOnBed(link);
                    currentServer.setBusy(true);
                    currentServer.activate(myParent.getCurrentTime());

                    return true;
                }
        }

        return false;
    }

    public int getCounter()
    {
        return counter;
    }

    public double getTransTq()
    {
        return transTq;
    }

}
