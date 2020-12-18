import cz.zcu.fav.kiv.jsim.*;

import java.util.List;

/**
 * TODO
 */
public class Killer extends JSimProcess {

    List<BasicCareUnitServer> basicCareUnitServerList;
    List<IntensiveCareUnitServer> intensiveCareUnitServerList;

    public Killer(String name, JSimSimulation simulation, List<BasicCareUnitServer> basicCareUnitServerList, List<IntensiveCareUnitServer> intensiveCareUnitServerList) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException
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

                    if (rand < currentServer.getpDeath()) { // death
                        currentServer.getPatient().setDied(true);
                        currentServer.cancel();
                        currentServer.activate(myParent.getCurrentTime());
                        //pridat counter na zesnuleho pacienta na zakladni peci
                    }

                }
            }

            for (IntensiveCareUnitServer currentServer : intensiveCareUnitServerList) {

                if (currentServer.isOccupied() && !currentServer.isIdle()) {
                    rand = JSimSystem.uniform(0.0, 1.0);

                    if (rand < currentServer.getpDeath()) { // death
                        ((Patient)currentServer.getPatientOnBed().getData()).setDied(true);
                        currentServer.cancel();
                        currentServer.activate(myParent.getCurrentTime());
                        // counter - zesnuly na intenzivni
                    }

                }

            }

            message("Killer time: " + myParent.getCurrentTime());
        }
        catch (JSimException e)
        {
            e.printStackTrace();
            e.printComment(System.err);
        }
    }

}
