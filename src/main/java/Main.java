import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * model.Program entry class - has main method which
 * initializes simulation parameters, runs simulation
 * and prints results.
 */
public class Main extends Application {

    /**
     * model.Program entry point.
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
//        initialize(args);
//
//        if (Constants.isScenario) {
//            ScenarioParams scenarioParams = getScenarioParams();
//            ScenarioResults results = runScenario(scenarioParams);
//            printScenarioResults(results);
//        } else {
//            SimulationParams simulationParams = getSimulationParams();
//            SimulationResults simulationResults = run(simulationParams);
//            printResults(simulationResults);
//        }
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


}
