import cz.zcu.fav.kiv.jsim.JSimException;
import cz.zcu.fav.kiv.jsim.JSimSimulation;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static String DEFAULT_CONFIG_PATH = "config.properties";

    public static void main(String[] args) {

        SimulationParams simulationParams = initialize(args);

        SimulationResults simulationResults = run(simulationParams);

        printResults(simulationResults);

    }

    private static SimulationParams initialize(String[] args) {
        String configFilePath = DEFAULT_CONFIG_PATH;
        if (args.length > 0) {
            configFilePath = args[0];
        }

        Constants.init(configFilePath);

        return new SimulationParams(Constants.NUMBER_OF_BED_BASIC_UNIT, Constants.NUMBER_OF_BED_INTENSIVE_CARE_UNIT,
                                    Constants.INPUT_LAMBDA, Constants.BASIC_CARE_UNIT_MU, Constants.BASIC_CARE_UNIT_SIGMA, Constants.INTENSIVE_CARE_UNIT_MU,
                                    Constants.P_FROM_BASIC_TO_INTENSIVE, Constants.P_DEATH_BASIC_CARE_UNIT, Constants.P_DEATH_INTENSIVE_CARE_UNIT,
                                    Constants.MAX_TIME_IN_QUEUE);
    }

    private static SimulationResults run(SimulationParams params) {
        JSimSimulation simulation = null;

        QueueWithCareUnitServer basicCareUnitQueue;
        List<BasicCareUnitServer> basicCareUnitServerList;
        List<IntensiveCareUnitServer> intensiveCareUnitServerList;
        InputGenerator inputGenerator;

        try {
            System.out.println("Initializing the simulation..");

            simulation = new JSimSimulation("Hospital");

            basicCareUnitQueue = new QueueWithCareUnitServer("Basic Care Unit Queue", simulation, null);

            intensiveCareUnitServerList = createIntensiveCareUnitServersArray(params.getNumberOfBedsIntensiveCareUnit(),
                                                    simulation, params.getIntensiveCareUnitMu(),
                                                    params.getpDeathIntensiveCareUnit());

            basicCareUnitServerList = createBasicCareUnitServersArray(params.getNumberOfBedsBasicCareUnit(), simulation,
                                            params.getBasicCareUnitMu(), params.getBasicCareUnitSigma(),
                                            params.getpDeathBasicCareUnit(), params.getpFromBasicToIntensive(),
                                            basicCareUnitQueue, intensiveCareUnitServerList, params.getMaxTimeInQueue());

            inputGenerator = new InputGenerator("Input generator", simulation, params.getInputLambda(), basicCareUnitQueue);

            basicCareUnitQueue.setServerList(basicCareUnitServerList);

            // activate generators
            simulation.message("Activating generators...");

            inputGenerator.activate(0.0);

            simulation.message("Running the simulation, please wait.");

            while ((simulation.getCurrentTime() < 10000.0) && (simulation.step() == true))
                ;


            // Now, some boring numbers.
            double totalTime = simulation.getCurrentTime();
            simulation.message("Simulation interrupted at time " + totalTime);
            simulation.message("Queues' statistics:");
            simulation.message("Queue 1: Lw = " + basicCareUnitQueue.getLw() + ", Tw = " + basicCareUnitQueue.getTw() + ", Tw all = " + basicCareUnitQueue.getTwForAllLinks());
            simulation.message("Servers' statistics:");
            //simulation.message("example02.Server 1: Counter = " + careUnitlServer.getCounter() + ", sum of Tq (for transactions thrown away by this server) = " + careUnitlServer.getTransTq());
            //simulation.message("Mean response time = " + ((server1.getTransTq() + server2.getTransTq()) / (server1.getCounter() + server2.getCounter())));

            SimulationResults results = Statistics.calculateResults(basicCareUnitServerList, intensiveCareUnitServerList, totalTime);
            return results;
        } catch (JSimException e) {
            e.printStackTrace();
            e.printComment(System.err);
        } finally {
            simulation.shutdown();
        }

        return null;
    }

    private static List<BasicCareUnitServer> createBasicCareUnitServersArray(int numberOfServers,
                                                                             JSimSimulation simulation,
                                                                             double mu, double sigma, double p1, double p2,
                                                                             QueueWithCareUnitServer queue,
                                                                             List<IntensiveCareUnitServer> intensiveCareUnitServerList,
                                                                             double maxTimeInQueue) throws JSimException {

        List<BasicCareUnitServer> servers = new ArrayList<>();

        for (int i = 0; i < numberOfServers; i++) {
            servers.add(new BasicCareUnitServer("Basic Care Unit Server " + i, simulation, mu, sigma, p1, p2, queue, intensiveCareUnitServerList, maxTimeInQueue));
        }

        return servers;
    }

    private static List<IntensiveCareUnitServer> createIntensiveCareUnitServersArray(int numberOfServers,
                                                                                     JSimSimulation simulation,
                                                                                     double mu, double p) throws JSimException {

        List<IntensiveCareUnitServer> servers = new ArrayList<>();

        for (int i = 0; i < numberOfServers; i++) {
            servers.add(new IntensiveCareUnitServer("Intensive Care Unit Server " + i, simulation, mu, p, false));
        }

        return servers;
    }

    private static void printResults(SimulationResults results) {
        System.out.println("results.getBasicCareRho() = " + results.getBasicCareRho());
        System.out.println("results.getIntensiveCareRho() = " + results.getIntensiveCareRho());
        System.out.println("results.getTotalRho() = " + results.getTotalRho());
    }


}
