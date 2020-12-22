package model;

import controller.SimulationWindowController;
import cz.zcu.fav.kiv.jsim.JSimException;
import cz.zcu.fav.kiv.jsim.JSimMethodNotSupportedException;
import cz.zcu.fav.kiv.jsim.JSimSimulation;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program {


    /** Default path to configuration file (properties). */
    private static final String DEFAULT_CONFIG_PATH = "config.properties";

    /** Max simulation time. */
    private static final double MAX_TIME = 10000.0;

    private final SimulationWindowController simulationWindowController;

    private JSimSimulation simulation;

    public Program(SimulationWindowController simulationWindowController) {
        this.simulationWindowController = simulationWindowController;
    }

    /**
     * Loads configuration file.
     */
    public void loadConfigurationFile() {
        Constants.init(DEFAULT_CONFIG_PATH);
    }


    private ScenarioResults runScenario() {
        Map<Double, Double> scenarioResults = new HashMap<>();

        double newValue = updateSimulationParam(0); // step == 0 -> no update, just getting value
        for (int i = 0; i < Constants.RUNS_COUNT; i++) {
            // run simulation
            SimulationResults results = runSimRunByTime();

            // save result
            double result = getResultToSave(results);
            scenarioResults.put(newValue, result);

            // update simulation parameter
            newValue = updateSimulationParam(Constants.STEP);
        }

        return new ScenarioResults(scenarioResults);
    }

    private static double getResultToSave(SimulationResults results) {
        switch (Constants.OUTPUT_PARAM) {
            case HEALED_PATIENTS:
                return results.getHealedPatients();
            case DEAD_PATIENTS:
                return results.getDeadPatients();
            case TOTAL_RHO:
                return results.getTotalRho();
        }

        return 0;
    }

    private static double updateSimulationParam(double step) {
        double newValue = 0;

        switch (Constants.INPUT_PARAM) {
            case NUMBER_OF_BED_BASIC_UNIT:
                newValue = Constants.NUMBER_OF_BED_BASIC_UNIT + (int) step;
                Constants.NUMBER_OF_BED_BASIC_UNIT = (int) newValue;
                break;
            case NUMBER_OF_BED_INTENSIVE_CARE_UNIT:
                newValue = Constants.NUMBER_OF_BED_INTENSIVE_CARE_UNIT + (int) step;
                Constants.NUMBER_OF_BED_INTENSIVE_CARE_UNIT = (int) newValue;
                break;
            case INPUT_LAMBDA:
                newValue = Constants.INPUT_LAMBDA + step;
                Constants.INPUT_LAMBDA = newValue;
                break;

            case BASIC_CARE_UNIT_MU:
                newValue = Constants.BASIC_CARE_UNIT_MU + step;
                Constants.BASIC_CARE_UNIT_MU = newValue;
                break;
            case BASIC_CARE_UNIT_SIGMA:
                newValue = Constants.BASIC_CARE_UNIT_SIGMA + step;
                Constants.BASIC_CARE_UNIT_SIGMA = newValue;
                break;
            case INTENSIVE_CARE_UNIT_MU:
                newValue = Constants.INTENSIVE_CARE_UNIT_MU + step;
                Constants.INTENSIVE_CARE_UNIT_MU = newValue;
                break;

            case P_FROM_BASIC_TO_INTENSIVE:
                newValue = Constants.P_FROM_BASIC_TO_INTENSIVE + step;
                Constants.P_FROM_BASIC_TO_INTENSIVE = newValue;
                break;
            case P_DEATH_BASIC_CARE_UNIT:
                newValue = Constants.P_DEATH_BASIC_CARE_UNIT + step;
                Constants.P_DEATH_BASIC_CARE_UNIT = newValue;
                break;
            case P_DEATH_INTENSIVE_CARE_UNIT:
                newValue = Constants.P_DEATH_INTENSIVE_CARE_UNIT + step;
                Constants.P_DEATH_INTENSIVE_CARE_UNIT = newValue;
                break;

            case MAX_TIME_IN_QUEUE:
                newValue = Constants.MAX_TIME_IN_QUEUE + step;
                Constants.MAX_TIME_IN_QUEUE = newValue;
                break;
        }

        return newValue;
    }

    /**
     * Runs simulation with parameters.
     *
     * @return simulation results or null if some error occurred during simulation
     */
    public SimulationResults runSimRunByTime() {

        BasicCareUnitQueue basicCareUnitQueue;
        List<BasicCareUnitServer> basicCareUnitServerList;
        List<IntensiveCareUnitServer> intensiveCareUnitServerList;
        InputGenerator inputGenerator;

        try {
            // init
            System.out.println("Initializing the simulation..");
            simulation = new JSimSimulation("Hospital simulation");
            basicCareUnitQueue = new BasicCareUnitQueue("Basic Care Unit Queue", simulation, null);

            intensiveCareUnitServerList = createIntensiveCareUnitServersArray(simulation, basicCareUnitQueue);
            basicCareUnitServerList = createBasicCareUnitServersArray(simulation, basicCareUnitQueue, intensiveCareUnitServerList);

            inputGenerator = new InputGenerator("Input generator", simulation, basicCareUnitQueue);
            basicCareUnitQueue.setServerList(basicCareUnitServerList);

            // activate generators
            simulation.message("Activating generators...");
            inputGenerator.activate(0.0);

            for (int i = 1; i < MAX_TIME; i++) {
                new DailyProbability("Daily probability " + i, simulation, basicCareUnitServerList, intensiveCareUnitServerList)
                        .activate(i);
            }

            // run simulation
            simulation.message("Running the simulation, please wait.");
            while ((simulation.getCurrentTime() < MAX_TIME) && (simulation.step() == true))
                ;

            // results
            double totalTime = simulation.getCurrentTime();
            simulation.message("Simulation interrupted at time " + totalTime);
            SimulationResults results = Statistics.calculateResults(basicCareUnitServerList, intensiveCareUnitServerList, totalTime, basicCareUnitQueue);
            return results;
        } catch (JSimException e) {
            e.printStackTrace();
            e.printComment(System.err);
        } finally {
            simulation.shutdown();
        }

        return null;
    }

    /**
     * Runs simulation with parameters.
     */
    public void initSimStepByStep() {

        BasicCareUnitQueue basicCareUnitQueue;
        List<BasicCareUnitServer> basicCareUnitServerList;
        List<IntensiveCareUnitServer> intensiveCareUnitServerList;
        InputGenerator inputGenerator;

        try {
            // init
            System.out.println("Initializing the simulation..");
            simulation = new JSimSimulation("Hospital simulation");
            basicCareUnitQueue = new BasicCareUnitQueue("Basic Care Unit Queue", simulation, null);

            intensiveCareUnitServerList = createIntensiveCareUnitServersArray(simulation, basicCareUnitQueue);
            basicCareUnitServerList = createBasicCareUnitServersArray(simulation, basicCareUnitQueue, intensiveCareUnitServerList);

            inputGenerator = new InputGenerator("Input generator", simulation, basicCareUnitQueue);
            basicCareUnitQueue.setServerList(basicCareUnitServerList);

            // activate generators
            simulation.message("Activating generators...");
            inputGenerator.activate(0.0);

            for (int i = 1; i < MAX_TIME; i++) {
                new DailyProbability("Daily probability " + i, simulation, basicCareUnitServerList, intensiveCareUnitServerList)
                        .activate(i);
            }

            Platform.runLater(() -> simulationWindowController.finishInitStepByStep());

            // run simulation
            simulation.message("Running the simulation, please wait.");
//            while ((simulation.getCurrentTime() < MAX_TIME) && (simulation.step() == true))
//                ;

//            // results
//            double totalTime = simulation.getCurrentTime();
//            simulation.message("Simulation interrupted at time " + totalTime);
//            SimulationResults results = Statistics.calculateResults(basicCareUnitServerList, intensiveCareUnitServerList, totalTime, basicCareUnitQueue);
//            return results;
        } catch (JSimException e) {
            e.printStackTrace();
            e.printComment(System.err);
            simulation.shutdown();
        }
//        finally {
//            simulation.shutdown();
//        }
    }

    public void doStep() {
        try {
            simulation.step();
            Platform.runLater(() -> simulationWindowController.finishDoStep());
        } catch (JSimMethodNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void stopSimStepByStep(List<BasicCareUnitServer> basicCareUnitServerList,
                                        List<IntensiveCareUnitServer> intensiveCareUnitServerList,
                                        BasicCareUnitQueue basicCareUnitQueue) {
        double totalTime = simulation.getCurrentTime();
        simulation.message("Simulation interrupted at time " + totalTime);
        SimulationResults results = Statistics.calculateResults(basicCareUnitServerList, intensiveCareUnitServerList, totalTime, basicCareUnitQueue);
        simulation.shutdown();

        printResults(results);
    }

    /**
     * Creates all servers in basic care unit. One server = one bed.
     *
     * @param simulation simulation
     * @param queue queue to basic care
     * @param intensiveCareUnitServerList list of intensive care servers
     * @return list of basic unit servers
     * @throws JSimException if problem in simulation occurs
     */
    private static List<BasicCareUnitServer> createBasicCareUnitServersArray(JSimSimulation simulation,
                                                                             BasicCareUnitQueue queue,
                                                                             List<IntensiveCareUnitServer> intensiveCareUnitServerList) throws JSimException {

        List<BasicCareUnitServer> servers = new ArrayList<>();

        for (int i = 0; i < Constants.NUMBER_OF_BED_BASIC_UNIT; i++) {
            servers.add(new BasicCareUnitServer("Basic Care Unit Server " + i, simulation, queue, intensiveCareUnitServerList));
        }

        return servers;
    }

    /**
     * Creates all servers in intensive care unit. One server = one bed.
     *
     * @param simulation simulation
     * @return list of intensive care servers
     * @throws JSimException if problem in simulation occurs
     */
    private static List<IntensiveCareUnitServer> createIntensiveCareUnitServersArray(JSimSimulation simulation, BasicCareUnitQueue queue) throws JSimException {

        List<IntensiveCareUnitServer> servers = new ArrayList<>();

        for (int i = 0; i < Constants.NUMBER_OF_BED_INTENSIVE_CARE_UNIT; i++) {
            servers.add(new IntensiveCareUnitServer("Intensive Care Unit Server " + i, simulation, false, queue));
        }

        return servers;
    }

    /**
     * Prints simulation results.
     *
     * @param results simulation results
     */
    private static void printResults(SimulationResults results) {
        if (results == null) return;

        System.out.println(results);
    }

    private static void printScenarioResults(ScenarioResults results) {
        if (results == null) return;

        System.out.println(results);
    }

    public JSimSimulation getSimulation() {
        return simulation;
    }
}