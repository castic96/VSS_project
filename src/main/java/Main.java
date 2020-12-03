import cz.zcu.fav.kiv.jsim.JSimException;
import cz.zcu.fav.kiv.jsim.JSimProcess;
import cz.zcu.fav.kiv.jsim.JSimSimulation;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        SimulationParams simulationParams = initialize();

        SimulationResults simulationResults = run(simulationParams);

        printResults(simulationResults);

    }

    private static SimulationParams initialize() {
        int numberOfBedsBasicCareUnit = 15;
        int numberOfBedsIntensiveCareUnit = 8;

        double inputLambda = 0.4;

        double basicCareUnitMu = 1.0;
        double basicCareUnitSigma = 0.3;
        double intensiveCareUnitMu = 1.0;

        double pFromBasicToIntensive = 0.2;
        double pDeathBasicCareUnit = 0.1;
        double pDeathIntensiveCareUnit = 0.3;

        return new SimulationParams(numberOfBedsBasicCareUnit, numberOfBedsIntensiveCareUnit,
                                    inputLambda, basicCareUnitMu, basicCareUnitSigma, intensiveCareUnitMu,
                                    pFromBasicToIntensive, pDeathBasicCareUnit, pDeathIntensiveCareUnit);
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
