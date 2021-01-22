package model;

import controller.SimulationWindowController;
import cz.zcu.fav.kiv.jsim.*;
import javafx.application.Platform;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Instance of program - takes care of starting simulations.
 */
public class Program {

    /** Default path to configuration file (properties). */
    private static final String DEFAULT_CONFIG_PATH = "config.properties";

    /** Max simulation time. */
    private static double maxTimeGlobal = 5000.0;
    /** Simulation window controller. */
    private final SimulationWindowController simulationWindowController;

    /** Simulation. */
    private JSimSimulation simulation;

    /** Queue to basic care. */
    private BasicCareUnitQueue basicCareUnitQueue;
    /** List of basic care servers. */
    private List<BasicCareUnitServer> basicCareUnitServerList;
    /** List of intensive care servers. */
    private List<IntensiveCareUnitServer> intensiveCareUnitServerList;

    /** If simulation is running. */
    private boolean running = true;

    /**
     * Creates new instance.
     *
     * @param simulationWindowController simulation window controller
     */
    public Program(SimulationWindowController simulationWindowController) {
        this.simulationWindowController = simulationWindowController;
        initLogging();
    }

    /**
     * Loads configuration file.
     */
    public void loadConfigurationFile() {
        Constants.init(DEFAULT_CONFIG_PATH);
    }

    /**
     * Initializes simulation.
     *
     * @param maxTime max time of simulation
     * @throws JSimException if problem in simulation initialization occurs
     */
    public void initSimulation(double maxTime) throws JSimException {
        // null all counters
        BasicCareUnitServer.setDeadPatientsCounter(0);
        BasicCareUnitServer.setDeadPatientsNoFreeBedInICUCounter(0);
        BasicCareUnitServer.setPatientsMovedToICUCounter(0);
        BasicCareUnitServer.setPatientsMovedBackFromICUCounter(0);
        BasicCareUnitServer.setHealedPatientsCounter(0);
        BasicCareUnitServer.setDiedInQueuePatientsCounter(0);

        BasicCareUnitServer.clearDeadPatientsTimes();
        BasicCareUnitServer.clearDeadPatientsNoFreeBedInICUTimes();
        BasicCareUnitServer.clearPatientsMovedToICUTimes();
        BasicCareUnitServer.clearPatientsMovedBackFromICUTimes();
        BasicCareUnitServer.clearHealedPatientsTimes();
        BasicCareUnitServer.clearDiedInQueuePatientsTimes();

        InputGenerator.setIncomingPatientsCounter(0);
        InputGenerator.clearIncomingPatientsTimes();

        IntensiveCareUnitServer.setDeadPatientsCounter(0);
        IntensiveCareUnitServer.clearDeadPatientsTimes();

        Patient.setPatientsCounter(0);

        // init
        InputGenerator inputGenerator;
        System.out.println("Initializing the simulation..");
        simulation = new JSimSimulation("Hospital simulation");
        basicCareUnitQueue = new BasicCareUnitQueue("Basic Care Unit Queue", simulation, null);

        intensiveCareUnitServerList = createIntensiveCareUnitServersArray(this, basicCareUnitQueue);
        basicCareUnitServerList = createBasicCareUnitServersArray(this, basicCareUnitQueue, intensiveCareUnitServerList);

        inputGenerator = new InputGenerator("Input generator", this, basicCareUnitQueue);
        basicCareUnitQueue.setServerList(basicCareUnitServerList);

        // activate generators
        simulation.message("Activating generators...");
        inputGenerator.activate(0.0);

        for (int i = 1; i < maxTime; i++) {
            new DailyProbability("Daily probability " + i, simulation, basicCareUnitServerList, intensiveCareUnitServerList)
                    .activate(i);
        }
    }

    /**
     * Runs simulation with parameters.
     */
    public void runSimRunByTime(double maxTime) {
        try {
            // init
            initSimulation(maxTime);

            // run simulation
            simulation.message("Running the simulation, please wait.");
            while ((simulation.getCurrentTime() < maxTime) && (simulation.step()) && (isRunning())) {
                Platform.runLater(() -> {
                    simulationWindowController.setCurrentTimeRunByTime((int)(simulation.getCurrentTime()));
                    simulationWindowController.setProgressBarValue((simulation.getCurrentTime() / maxTime));
                });
            }

            // results
            double totalTime = simulation.getCurrentTime();
            simulation.message("Simulation interrupted at time " + totalTime);
            SimulationResults results = Statistics.calculateResults(basicCareUnitServerList, intensiveCareUnitServerList, totalTime, basicCareUnitQueue);
            Platform.runLater(() -> simulationWindowController.setTextAreaResults(results.toString()));

        } catch (JSimException e) {
            e.printStackTrace();
            e.printComment(System.err);
        } finally {
            simulation.shutdown();
            Platform.runLater(simulationWindowController::finishRunByTime);
        }

    }

    /**
     * Runs simulation with parameters.
     */
    public void initSimStepByStep() {
        try {
            initSimulation(maxTimeGlobal);

            Platform.runLater(simulationWindowController::finishInitStepByStep);

        } catch (JSimException e) {
            e.printStackTrace();
            e.printComment(System.err);
            simulation.shutdown();
            Platform.runLater(simulationWindowController::finishStopStepByStep);
        }
    }

    /**
     * Does one step of simulation.
     */
    public void doStep() {
        try {
            simulation.step();
            new DailyProbability("Daily probability " + maxTimeGlobal, simulation, basicCareUnitServerList, intensiveCareUnitServerList)
                    .activate(maxTimeGlobal);
            maxTimeGlobal++;
            Platform.runLater(() -> {
                simulationWindowController.setCurrentTimeStepByStep((int)simulation.getCurrentTime());
                simulationWindowController.finishDoStep();
            });

        } catch (JSimMethodNotSupportedException | JSimInvalidParametersException | JSimSecurityException | JSimSimulationAlreadyTerminatedException | JSimTooManyProcessesException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops step by step simulation.
     */
    public void stopSimStepByStep() {
        double totalTime = simulation.getCurrentTime();
        simulation.message("Simulation interrupted at time " + totalTime);
        SimulationResults results = Statistics.calculateResults(basicCareUnitServerList, intensiveCareUnitServerList, totalTime, basicCareUnitQueue);
        simulation.shutdown();

        //printResults(results);
        Platform.runLater(() -> simulationWindowController.setTextAreaResults(results.toString()));
        Platform.runLater(simulationWindowController::finishStopStepByStep);
    }

    /**
     * Creates all servers in basic care unit. One server = one bed.
     *
     * @param program program
     * @param queue queue to basic care
     * @param intensiveCareUnitServerList list of intensive care servers
     * @return list of basic unit servers
     * @throws JSimException if problem in simulation occurs
     */
    private static List<BasicCareUnitServer> createBasicCareUnitServersArray(Program program,
                                                                             BasicCareUnitQueue queue,
                                                                             List<IntensiveCareUnitServer> intensiveCareUnitServerList) throws JSimException {

        List<BasicCareUnitServer> servers = new ArrayList<>();

        for (int i = 0; i < Constants.NUMBER_OF_BED_BASIC_UNIT; i++) {
            servers.add(new BasicCareUnitServer("Basic Care Unit Server " + i, program, queue, intensiveCareUnitServerList));
        }

        return servers;
    }

    /**
     * Creates all servers in intensive care unit. One server = one bed.
     *
     * @param program program
     * @return list of intensive care servers
     * @throws JSimException if problem in simulation occurs
     */
    private static List<IntensiveCareUnitServer> createIntensiveCareUnitServersArray(Program program, BasicCareUnitQueue queue) throws JSimException {

        List<IntensiveCareUnitServer> servers = new ArrayList<>();

        for (int i = 0; i < Constants.NUMBER_OF_BED_INTENSIVE_CARE_UNIT; i++) {
            servers.add(new IntensiveCareUnitServer("Intensive Care Unit Server " + i, program, false, queue));
        }

        return servers;
    }

    /**
     * Initializes logging.
     */
    public void initLogging() {
        try {
            // creating log file
            File file = new File("simulation.log");
            FileOutputStream fos = new FileOutputStream(file);

            // stdout -> to file AND to text area in GUI
            System.setOut(new PrintStream(new OutputStream() {
                PrintStream ps = new PrintStream(fos);

                @Override
                public void write(int b) {
                    ps.write(b);

                    if (Constants.IS_STEP_BY_STEP) {
                        Platform.runLater(() -> simulationWindowController.appendTextAreaOutputLog(b));
                    }
                }
            }));

        } catch (FileNotFoundException e) {
            System.err.println("Simulation log file not found.");
        }
    }

    /**
     * Returns simulation.
     *
     * @return simulation
     */
    public JSimSimulation getSimulation() {
        return simulation;
    }

    /**
     * Returns simulation window controller.
     *
     * @return simulation window controller
     */
    public SimulationWindowController getSimulationWindowController() {
        return simulationWindowController;
    }

    /**
     * Returns if simulation is running.
     *
     * @return true if simulation is running; false otherwise
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Sets if simulation is running.
     *
     * @param running new value
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Sets that simulation is not running.
     */
    public void setRunningFalse() {
        this.running = false;
    }

    /**
     * Returns list of basic care servers.
     *
     * @return list of basic care servers
     */
    public List<BasicCareUnitServer> getBasicCareUnitServerList() {
        return basicCareUnitServerList;
    }

    /**
     * Returns list of intensive care servers.
     *
     * @return list of intensive care servers
     */
    public List<IntensiveCareUnitServer> getIntensiveCareUnitServerList() {
        return intensiveCareUnitServerList;
    }
}
