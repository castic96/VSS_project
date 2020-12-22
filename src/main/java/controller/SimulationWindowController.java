package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.enums.LaunchType;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class SimulationWindowController implements Initializable {

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private Label labelStepByStep;

    @FXML
    private Label labelRunByTime;

    @FXML
    private Label labelStepByStepCurrTime;

    @FXML
    private Label labelRunByTimeCurrTime;

    @FXML
    private Label labelRunByTimeMaxTime;

    @FXML
    private Button buttonStep;

    @FXML
    private Button buttonStart;

    @FXML
    private Button buttonStop;

    @FXML
    private TextField textFieldMaxTime;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboBox.getItems().addAll(LaunchType.STEP_BY_STEP.getValue(), LaunchType.RUN_BY_TIME.getValue());
        comboBox.setEditable(false);
        comboBox.setValue(LaunchType.STEP_BY_STEP.getValue());
        stepByStepEnable();
    }

    @FXML
    public void exitApplication() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss yyyy/MM/dd");
        LocalDateTime end = LocalDateTime.now();
        System.out.println("Application ended: "+ dtf.format(end));
        System.exit(0);
    }

    @FXML
    public void comboBoxChange() {
        if (comboBox.getValue().equals(LaunchType.STEP_BY_STEP.getValue())) {
            stepByStepEnable();
        }
        else {
            runByTimeEnable();
        }
    }

    private void stepByStepEnable() {
        labelStepByStep.setDisable(false);
        labelStepByStepCurrTime.setDisable(false);
        buttonStep.setDisable(false);

        labelRunByTime.setDisable(true);
        labelRunByTimeCurrTime.setDisable(true);
        labelRunByTimeMaxTime.setDisable(true);
        buttonStart.setDisable(true);
        buttonStop.setDisable(true);
        textFieldMaxTime.setDisable(true);
    }

    private void runByTimeEnable() {
        labelStepByStep.setDisable(true);
        labelStepByStepCurrTime.setDisable(true);
        buttonStep.setDisable(true);

        labelRunByTime.setDisable(false);
        labelRunByTimeCurrTime.setDisable(false);
        labelRunByTimeMaxTime.setDisable(false);
        buttonStart.setDisable(false);
        buttonStop.setDisable(false);
        textFieldMaxTime.setDisable(false);
    }

}
