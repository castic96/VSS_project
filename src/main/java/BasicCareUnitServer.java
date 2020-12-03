import cz.zcu.fav.kiv.jsim.*;

import java.util.List;

public class BasicCareUnitServer extends JSimProcess {

    private double mu;
    private double sigma;
    private double pDeath;
    private double pFromBasicToIntensive;
    private QueueWithCareUnitServer queue;
    private List<JSimProcess> intensiveCareUnitServerList;

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
    public BasicCareUnitServer(String name, JSimSimulation parent, double mu, double sigma, double p1, double p2, QueueWithCareUnitServer queue, List<JSimProcess> intensiveCareUnitServerList) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException {
        super(name, parent);
        this.mu = mu;
        this.sigma = sigma;
        this.pDeath = p1;
        this.pFromBasicToIntensive = p2;
        this.queue = queue;
        this.intensiveCareUnitServerList = intensiveCareUnitServerList;

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
                }
                else {
                    message("Moved back to basic care.");
                }

                if (link == null)
                {
                    // If we have nothing to do, we sleep.
                    passivate();
                }
                else
                {
                    // Simulating hard work here...
                    double gauss = JSimSystem.gauss(mu, sigma);
                    if (gauss < 0) { // todo!! very weird hack...
                        gauss = Math.abs(gauss);
                    }
                    //System.out.println("gauss = " + gauss);

                    hold(gauss); // Gauss
                    //link = queue.pop();

                    double rand = JSimSystem.uniform(0.0, 1.0);
                    if (rand < pDeath) {
                        // death
                        message("Died on basic care.");
                        //link.out();
                        link = null;
                    }
                    else {

                        rand = JSimSystem.uniform(0.0, 1.0);
                        if (rand < pFromBasicToIntensive) {
                            // move to ICU
                            message("Trying to move to intensive care unit...");

                            if (!moveToIntensiveCare(link)) {
                                message("Patient died... No necessary bed on intensive care unit.");
                            }
                            else {
                                message("Patient moved to intensive care unit successfully.");
                            }
                            //link.out();
                            //link = null;

                        } else {
                            // healthy - goes home
                            message("Healthy.");
                            //link.out();
                            link = null;
                        }
                    }



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
                } // else queue is empty / not empty
            } // while
        } // try
        catch (JSimException e)
        {
            e.printStackTrace();
            e.printComment(System.err);
        }

    }

    private synchronized JSimLink waitingForBasicCare() {

        IntensiveCareUnitServer currentServer;
        Patient currentPatient;
        IntensiveCareUnitServer firstServerRequest = null;
        JSimLink firstRequestPatientLink;
        double firstPatientRequestTime = Long.MAX_VALUE;

        for (JSimProcess currentProcess : intensiveCareUnitServerList) {

            if (currentProcess instanceof IntensiveCareUnitServer) {
                currentServer = (IntensiveCareUnitServer)currentProcess;

                if (currentServer.isBusy() && currentServer.isIdle()) {
                    currentPatient = (Patient)currentServer.getPatientOnBed().getData();

                    if (currentPatient.getTimeOfRequestToBasicCare() < firstPatientRequestTime) {
                        firstPatientRequestTime = currentPatient.getTimeOfRequestToBasicCare();
                        firstServerRequest = currentServer;
                    }

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

        IntensiveCareUnitServer currentServer;

        for (JSimProcess currentProcess : intensiveCareUnitServerList) {

            if (currentProcess instanceof IntensiveCareUnitServer) {
                currentServer = (IntensiveCareUnitServer)currentProcess;

                if (!currentServer.isBusy()) {
                    currentServer.setPatientOnBed(link);
                    currentServer.setBusy(true);
                    currentServer.activate(myParent.getCurrentTime());

                    return true;
                }

            }

        }

        return false;
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
