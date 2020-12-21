import javafx.application.Application;
import cz.zcu.fav.kiv.jsim.JSimException;
import cz.zcu.fav.kiv.jsim.JSimSimulation;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Program entry class - has main method which
 * initializes simulation parameters, runs simulation
 * and prints results.
 */
public class Main extends Application {

    /** Default path to configuration file (properties). */
    private static final String DEFAULT_CONFIG_PATH = "config.properties";

    /** Max simulation time. */
    private static final double MAX_TIME = 10000.0;

    /**
     * Program entry point.
     * Arguments can be either empty or one argument as path to configuration file.
     *
     * @param args arguments (empty or first = path to configuration file)
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hospital Simulation 1.0");
        primaryStage.setScene(getScene());
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();

        // creating log file
        File file = new File("simulation.log");
        FileOutputStream fos = new FileOutputStream(file);
        PrintStream ps = new PrintStream(fos);
        System.setOut(ps);

        // append information about app start
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss yyyy/MM/dd");
        LocalDateTime start = LocalDateTime.now();
        System.out.println("Application started: "+ dtf.format(start));

        // exit application by press cross
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            LocalDateTime end = LocalDateTime.now();
            System.out.println("Application ended: "+ dtf.format(end));
            System.exit(0);
        });
//
//        SimulationParams simulationParams = initialize(args);
//        SimulationResults simulationResults = run(simulationParams);
//        printResults(simulationResults);
    }

    private Scene getScene() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/simulationWindow.fxml"));
        Parent root = null;

        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Scene(root);
    }

    /**
     * Initializes simulation parameters from configuration file.
     * Arguments can be either empty or one argument as path to configuration file.
     *
     * @param args arguments (empty or first = path to configuration file)
     * @return initialized simulation parameters
     */
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

    /**
     * Runs simulation with parameters.
     *
     * @param params simulation parameters
     * @return simulation results or null if some error occurred during simulation
     */
    private static SimulationResults run(SimulationParams params) {
        JSimSimulation simulation = null;

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

}
