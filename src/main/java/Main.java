import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.LogManager;

/**
 * model.Program entry class - has main method which
 * initializes simulation parameters, runs simulation
 * and prints results.
 */
public class Main extends Application {

    /**
     * Program entry point.
     * Arguments can be either empty or one argument as path to configuration file.
     *
     * @param args arguments (empty or first = path to configuration file)
     */
    public static void main(String[] args) {
        // disable all logging
        LogManager.getLogManager().reset();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
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
