package model;

import controller.SimulationWindowController;
import cz.zcu.fav.kiv.jsim.JSimException;
import cz.zcu.fav.kiv.jsim.JSimMethodNotSupportedException;
import cz.zcu.fav.kiv.jsim.JSimSimulation;
import javafx.application.Platform;
import model.enums.InputParams;
import model.enums.OutputParams;

import java.util.ArrayList;
import java.util.List;

public class Program {


    /** Default path to configuration file (properties). */
    private static final String DEFAULT_CONFIG_PATH = "config.properties";

    /** Max simulation time. */
    private static final double MAX_TIME = 10000.0;

    private SimulationWindowController simulationWindowController;

    private JSimSimulation simulation;

    public Program(SimulationWindowController simulationWindowController) {
        this.simulationWindowController = simulationWindowController;
    }

    /**
     * Loads configuration file.
     */
    public void loadConfigurationFile() {
        String configFilePath = DEFAULT_CONFIG_PATH;
        Constants.init(configFilePath);
    }

    public SimulationParams getSimulationParams() {
        return new SimulationParams(Constants.NUMBER_OF_BED_BASIC_UNIT, Constants.NUMBER_OF_BED_INTENSIVE_CARE_UNIT,
                Constants.INPUT_LAMBDA, Constants.BASIC_CARE_UNIT_MU, Constants.BASIC_CARE_UNIT_SIGMA, Constants.INTENSIVE_CARE_UNIT_MU,
                Constants.P_FROM_BASIC_TO_INTENSIVE, Constants.P_DEATH_BASIC_CARE_UNIT, Constants.P_DEATH_INTENSIVE_CARE_UNIT,
                Constants.MAX_TIME_IN_QUEUE);
    }

    private static ScenarioParams getScenarioParams() {
        return new ScenarioParams(
                Constants.INPUT_PARAM, Constants.OUTPUT_PARAM, Constants.STEP, Constants.RUNS_COUNT,

                Constants.NUMBER_OF_BED_BASIC_UNIT, Constants.NUMBER_OF_BED_INTENSIVE_CARE_UNIT,
                Constants.INPUT_LAMBDA, Constants.BASIC_CARE_UNIT_MU, Constants.BASIC_CARE_UNIT_SIGMA, Constants.INTENSIVE_CARE_UNIT_MU,
                Constants.P_FROM_BASIC_TO_INTENSIVE, Constants.P_DEATH_BASIC_CARE_UNIT, Constants.P_DEATH_INTENSIVE_CARE_UNIT,
                Constants.MAX_TIME_IN_QUEUE);
    }
/*
    private static ScenarioResults runScenario(ScenarioParams scenarioParams) {
        double step = scenarioParams.getStep();
        int runsCount = scenarioParams.getRunsCount();

        double[] scenarioResults = new double[runsCount]; // todo - to map

        for (int i = 0; i < runsCount; i++) {
            // run simulation
            SimulationResults results = initSimStepByStep(scenarioParams);

            // save result
            double result = getResultToSave(scenarioParams.getOutputParam(), results);
            scenarioResults[i] = result;

            // update simulation parameter
            updateSimulationParam(scenarioParams.getInputParam(), scenarioParams, step);
        }

        ScenarioResults results = new ScenarioResults(scenarioParams.getOutputParam(), scenarioResults);
        return results;
    }

 */

    private static double getResultToSave(OutputParams outputParam, SimulationResults results) {
        switch (outputParam) {
            case HEALED_PATIENTS:
                return results.getHealedPatients();
            case DEAD_PATIENTS:
                return results.getDeadPatients();
            case TOTAL_RHO:
                return results.getTotalRho();
        }

        return 0;
    }

    private static void updateSimulationParam(InputParams inputParam, SimulationParams params, double step) {
        switch (inputParam) {
            case NUMBER_OF_BED_BASIC_UNIT:
                params.setNumberOfBedsBasicCareUnit((int) (params.getNumberOfBedsBasicCareUnit() + step));
                break;
            case NUMBER_OF_BED_INTENSIVE_CARE_UNIT:
                params.setNumberOfBedsIntensiveCareUnit((int) (params.getNumberOfBedsIntensiveCareUnit() + step));
                break;
            case INPUT_LAMBDA:
                params.setInputLambda(params.getInputLambda() + step);
                break;

            case BASIC_CARE_UNIT_MU:
                params.setBasicCareUnitMu(params.getBasicCareUnitMu() + step);
            case BASIC_CARE_UNIT_SIGMA:
                params.setBasicCareUnitSigma(params.getBasicCareUnitSigma() + step);
            case INTENSIVE_CARE_UNIT_MU:
                params.setIntensiveCareUnitMu(params.getIntensiveCareUnitMu() + step);

            case P_FROM_BASIC_TO_INTENSIVE:
                params.setpFromBasicToIntensive(params.getpFromBasicToIntensive() + step);
            case P_DEATH_BASIC_CARE_UNIT:
                params.setpDeathBasicCareUnit(params.getpDeathBasicCareUnit() + step);
            case P_DEATH_INTENSIVE_CARE_UNIT:
                params.setpDeathIntensiveCareUnit(params.getpDeathIntensiveCareUnit() + step);

            case MAX_TIME_IN_QUEUE:
                params.setMaxTimeInQueue(params.getMaxTimeInQueue() + step);
        }
    }

    /**
     * Runs simulation with parameters.
     *
     * @param params simulation parameters
     * @return simulation results or null if some error occurred during simulation
     */
    public SimulationResults runSimRunByTime(SimulationParams params) {

        BasicCareUnitQueue basicCareUnitQueue;
        List<BasicCareUnitServer> basicCareUnitServerList;
        List<IntensiveCareUnitServer> intensiveCareUnitServerList;
        InputGenerator inputGenerator;

        try {
            // init
            System.out.println("Initializing the simulation..");
            simulation = new JSimSimulation("Hospital simulation");
            basicCareUnitQueue = new BasicCareUnitQueue("Basic Care Unit Queue", simulation, null);

            intensiveCareUnitServerList = createIntensiveCareUnitServersArray(params.getNumberOfBedsIntensiveCareUnit(),
                    simulation, params.getIntensiveCareUnitMu(),
                    params.getpDeathIntensiveCareUnit(), basicCareUnitQueue);
            basicCareUnitServerList = createBasicCareUnitServersArray(params.getNumberOfBedsBasicCareUnit(), simulation,
                    params.getBasicCareUnitMu(), params.getBasicCareUnitSigma(),
                    params.getpDeathBasicCareUnit(), params.getpFromBasicToIntensive(),
                    basicCareUnitQueue, intensiveCareUnitServerList, params.getMaxTimeInQueue());

            inputGenerator = new InputGenerator("Input generator", simulation, params.getInputLambda(), basicCareUnitQueue);
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
     *
     * @param params simulation parameters
     * @return simulation results or null if some error occurred during simulation
     */
    public void initSimStepByStep(SimulationParams params) {

        BasicCareUnitQueue basicCareUnitQueue;
        List<BasicCareUnitServer> basicCareUnitServerList;
        List<IntensiveCareUnitServer> intensiveCareUnitServerList;
        InputGenerator inputGenerator;

        try {
            // init
            System.out.println("Initializing the simulation..");
            simulation = new JSimSimulation("Hospital simulation");
            basicCareUnitQueue = new BasicCareUnitQueue("Basic Care Unit Queue", simulation, null);

            intensiveCareUnitServerList = createIntensiveCareUnitServersArray(params.getNumberOfBedsIntensiveCareUnit(),
                    simulation, params.getIntensiveCareUnitMu(),
                    params.getpDeathIntensiveCareUnit(), basicCareUnitQueue);
            basicCareUnitServerList = createBasicCareUnitServersArray(params.getNumberOfBedsBasicCareUnit(), simulation,
                    params.getBasicCareUnitMu(), params.getBasicCareUnitSigma(),
                    params.getpDeathBasicCareUnit(), params.getpFromBasicToIntensive(),
                    basicCareUnitQueue, intensiveCareUnitServerList, params.getMaxTimeInQueue());

            inputGenerator = new InputGenerator("Input generator", simulation, params.getInputLambda(), basicCareUnitQueue);
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
     * @param numberOfServers number of servers (beds) in basic care unit
     * @param simulation simulation
     * @param mu mu (gauss distribution parameter)
     * @param sigma sigma (gauss distribution parameter)
     * @param pDeath probability of death in basic care
     * @param pFromBasicToIntensive probability of transfer to intensive care
     * @param queue queue to basic care
     * @param intensiveCareUnitServerList list of intensive care servers
     * @param maxTimeInQueue maximum time which can be spent in queue (if exceeded -> death)
     * @return list of basic unit servers
     * @throws JSimException if problem in simulation occurs
     */
    private static List<BasicCareUnitServer> createBasicCareUnitServersArray(int numberOfServers,
                                                                             JSimSimulation simulation,
                                                                             double mu, double sigma, double pDeath, double pFromBasicToIntensive,
                                                                             BasicCareUnitQueue queue,
                                                                             List<IntensiveCareUnitServer> intensiveCareUnitServerList,
                                                                             double maxTimeInQueue) throws JSimException {

        List<BasicCareUnitServer> servers = new ArrayList<>();

        for (int i = 0; i < numberOfServers; i++) {
            servers.add(new BasicCareUnitServer("Basic Care Unit Server " + i, simulation, mu, sigma, pDeath, pFromBasicToIntensive, queue, intensiveCareUnitServerList, maxTimeInQueue));
        }

        return servers;
    }

    /**
     * Creates all servers in intensive care unit. One server = one bed.
     *
     * @param numberOfServers number of servers (beds) in intensive care unit
     * @param simulation simulation
     * @param mu mu (exponential distribution parameter)
     * @param pDeath probability of death in intensive care
     * @return list of intensive care servers
     * @throws JSimException if problem in simulation occurs
     */
    private static List<IntensiveCareUnitServer> createIntensiveCareUnitServersArray(int numberOfServers,
                                                                                     JSimSimulation simulation,
                                                                                     double mu, double pDeath,
                                                                                     BasicCareUnitQueue queue) throws JSimException {

        List<IntensiveCareUnitServer> servers = new ArrayList<>();

        for (int i = 0; i < numberOfServers; i++) {
            servers.add(new IntensiveCareUnitServer("Intensive Care Unit Server " + i, simulation, mu, pDeath, false, queue));
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
