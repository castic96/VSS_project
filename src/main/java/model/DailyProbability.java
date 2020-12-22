package model;

import cz.zcu.fav.kiv.jsim.*;

import java.util.List;

/**
 * TODO
 */
public class DailyProbability extends JSimProcess {

    List<BasicCareUnitServer> basicCareUnitServerList;
    List<IntensiveCareUnitServer> intensiveCareUnitServerList;

    public DailyProbability(String name, JSimSimulation simulation, List<BasicCareUnitServer> basicCareUnitServerList, List<IntensiveCareUnitServer> intensiveCareUnitServerList) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException
    {
        super(name, simulation);
        this.basicCareUnitServerList = basicCareUnitServerList;
        this.intensiveCareUnitServerList = intensiveCareUnitServerList;
    }

    /**
     * Generating.
     */
    protected void life()
    {
        try
        {
            double rand;
            for (BasicCareUnitServer currentServer : basicCareUnitServerList) {

                if (currentServer.isOccupied()) {
                    rand = JSimSystem.uniform(0.0, 1.0);

                    if (rand < Constants.P_DEATH_BASIC_CARE_UNIT) { // death
                        currentServer.getPatient().setInMoveToIntensiveCare(false);
                        currentServer.getPatient().setDead(true);
                        currentServer.cancel();
                        currentServer.activate(myParent.getCurrentTime());
                    }
                    else {
                        rand = JSimSystem.uniform(0.0, 1.0);

                        if (rand < Constants.P_FROM_BASIC_TO_INTENSIVE) { // move to intensive care unit (if not possible -> death)
                            message("Trying to move to intensive care unit..., patient: " + currentServer.getPatient().getPatientNumber());
                            currentServer.getPatient().setInMoveToIntensiveCare(true);

                            currentServer.cancel();
                            currentServer.activate(myParent.getCurrentTime());

                            currentServer.getPatient().setDead(!moveToIntensiveCare(currentServer.getLink()));

                        }
                    }
                }
            }

            for (IntensiveCareUnitServer currentServer : intensiveCareUnitServerList) {

                if (currentServer.isOccupied() && !currentServer.isIdle()) { // without the ones waiting for basic care
                    Patient patient = (Patient)currentServer.getPatientOnBed().getData();

                    if (!patient.isInMoveToIntensiveCare()) {
                        rand = JSimSystem.uniform(0.0, 1.0);

                        if (rand < Constants.P_DEATH_INTENSIVE_CARE_UNIT) { // death
                            patient.setDead(true);
                            currentServer.cancel();
                            currentServer.activate(myParent.getCurrentTime());
                        }
                    }
                }

            }

            message("DailyProbability time: " + myParent.getCurrentTime());
        }
        catch (JSimException e)
        {
            e.printStackTrace();
            e.printComment(System.err);
        }
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

            if (!currentServer.isOccupied() && currentServer.isIdle()) {
                currentServer.setPatientOnBed(link);
                currentServer.setOccupied(true);
                currentServer.activate(myParent.getCurrentTime());

                return true;
            }

        }

        return false;
    }

}
