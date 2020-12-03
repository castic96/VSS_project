import cz.zcu.fav.kiv.jsim.JSimException;
import cz.zcu.fav.kiv.jsim.JSimProcess;
import cz.zcu.fav.kiv.jsim.JSimSimulation;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static String DEFAULT_CONFIG_PATH = "config.properties";

    public static void main(String[] args) {
        String configFilePath = DEFAULT_CONFIG_PATH;
        if (args.length > 0) {
            configFilePath = args[0];
        }

        Constants.init(configFilePath);

        SimulationParams simulationParams = initialize();

        SimulationResults simulationResults = run(simulationParams);

        printResults(simulationResults);

    }

    private static SimulationParams initialize() {
        return new SimulationParams(Constants.NUMBER_OF_BED_BASIC_UNIT, Constants.NUMBER_OF_BED_INTENSIVE_CARE_UNIT,
                                    Constants.INPUT_LAMBDA, Constants.BASIC_CARE_UNIT_MU, Constants.BASIC_CARE_UNIT_SIGMA, Constants.INTENSIVE_CARE_UNIT_MU,
                                    Constants.P_FROM_BASIC_TO_INTENSIVE, Constants.P_DEATH_BASIC_CARE_UNIT, Constants.P_DEATH_INTENSIVE_CARE_UNIT);
    }

    private static SimulationResults run(SimulationParams params) {
        JSimSimulation simulation = null;

        QueueWithCareUnitServer basicCareUnitQueue;
        List<JSimProcess> basicCareUnitServerList;
        List<JSimProcess> intensiveCareUnitServerList;
        InputGenerator inputGenerator;

        try {
            System.out.println("Initializing the simulation..");

            simulation = new JSimSimulation("Hospital");

            basicCareUnitQueue = new QueueWithCareUnitServer("Basic Care Unit Queue", simulation, null);

            // Tady jsem schvalne prohodil pravdepodobnosti, ze nejdriv je pravdepodobnost smrti a pak presunu na JIPku
            basicCareUnitServerList = createBasicCareUnitServersArray(params.getNumberOfBedsBasicCareUnit(), simulation,
                                            params.getBasicCareUnitMu(), params.getBasicCareUnitSigma(),
                                            params.getpDeathBasicCareUnit(), params.getpFromBasicToIntensive(),
                                            basicCareUnitQueue);

            intensiveCareUnitServerList = createIntensiveCareUnitServersArray(params.getNumberOfBedsIntensiveCareUnit(),
                                                    simulation, params.getIntensiveCareUnitMu(),
                                                    params.getpDeathIntensiveCareUnit());

            inputGenerator = new InputGenerator("Input generator", simulation, params.getInputLambda(), basicCareUnitQueue);

            basicCareUnitQueue.setServerList(basicCareUnitServerList);

            // activate generators
            simulation.message("Activating generators...");

            inputGenerator.activate(0.0);

            simulation.message("Running the simulation, please wait.");

            while ((simulation.getCurrentTime() < 10000.0) && (simulation.step() == true))
                ;


            // Now, some boring numbers.
            simulation.message("Simulation interrupted at time " + simulation.getCurrentTime());
            simulation.message("Queues' statistics:");
            simulation.message("Queue 1: Lw = " + basicCareUnitQueue.getLw() + ", Tw = " + basicCareUnitQueue.getTw() + ", Tw all = " + basicCareUnitQueue.getTwForAllLinks());
            simulation.message("Servers' statistics:");
            //simulation.message("example02.Server 1: Counter = " + careUnitlServer.getCounter() + ", sum of Tq (for transactions thrown away by this server) = " + careUnitlServer.getTransTq());
            //simulation.message("Mean response time = " + ((server1.getTransTq() + server2.getTransTq()) / (server1.getCounter() + server2.getCounter())));
        } catch (JSimException e) {
            e.printStackTrace();
            e.printComment(System.err);
        }

        finally
        {
            simulation.shutdown();
        }

        return new SimulationResults();
    }

    private static List<JSimProcess> createBasicCareUnitServersArray(int numberOfServers,
                                                                             JSimSimulation simulation,
                                                                             double mu, double sigma, double p1, double p2,
                                                                             QueueWithCareUnitServer queue) throws JSimException {

        List<JSimProcess> servers = new ArrayList<>();

        for (int i = 0; i < numberOfServers; i++) {
            servers.add(new BasicCareUnitServer("Basic Care Unit Server " + i, simulation, mu, sigma, p1, p2, queue));
        }

        return servers;
    }

    private static List<JSimProcess> createIntensiveCareUnitServersArray(int numberOfServers,
                                                                             JSimSimulation simulation,
                                                                             double mu, double p) throws JSimException {

        List<JSimProcess> servers = new ArrayList<>();

        for (int i = 0; i < numberOfServers; i++) {
            servers.add(new IntensiveCareUnitServer("Intensive Care Unit Server " + i, simulation, mu, p));
        }

        return servers;
    }

    private static void printResults(SimulationResults results) {
        // TODO
    }


}
