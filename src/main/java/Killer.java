import cz.zcu.fav.kiv.jsim.*;

import java.util.List;

/**
 * TODO
 */
public class Killer extends JSimProcess {

    List<BasicCareUnitServer> basicCareUnitServerList;
    List<IntensiveCareUnitServer> intensiveCareUnitServerList;
    double killerStartTime = 1.0;

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
        JSimLink link;

        try
        {
            while (true)
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

                killerStartTime += 1.0;
                message("Killer start:" + killerStartTime);
                hold(killerStartTime);


//                // generate patient and put him into queue
//                link = new JSimLink(new Patient(myParent.getCurrentTime()));
//                link.into(queue);
//
//                // find free bed in basic care for patient
//                for (JSimProcess currentServer : queue.getServerList()) {
//                    if (currentServer.isIdle()) {
//                        currentServer.activate(myParent.getCurrentTime());
//                        break;
//                    }
//                }
//
//                // wait before generating another patient
//                hold(JSimSystem.negExp(lambda));
            }
        }
        catch (JSimException e)
        {
            e.printStackTrace();
            e.printComment(System.err);
        }
    }

}
