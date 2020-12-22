package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.Program;
import model.enums.LaunchType;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class SimulationWindowController implements Initializable {

    private final Program program;

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
    private Label labelStatus;

    @FXML
    private Button buttonStep;

    @FXML
    private Button buttonStartStepByStep;

    @FXML
    private Button buttonStopStepByStep;

    @FXML
    private Button buttonStartRunByTime;

    @FXML
    private Button buttonStopRunByTime;

    @FXML
    private TextField textFieldMaxTime;

    @FXML
    private TextArea textAreaOutputLog;

    @FXML
    private TextArea textAreaQueue;

    @FXML
    private TextArea textAreaBasicCare;

    @FXML
    private TextArea textAreaIntensiveCare;

    @FXML
    private TextArea textAreaDead;

    @FXML
    private TextArea textAreaHealthy;

    public SimulationWindowController() {
        program = new Program(this);
    }

    public void clear() {
        textAreaOutputLog.clear();
        textAreaQueue.clear();
        textAreaBasicCare.clear();
        textAreaIntensiveCare.clear();
        textAreaDead.clear();
        textAreaHealthy.clear();
    }

    public void initStepByStep() {
        clear();
        program.loadConfigurationFile();
        new Thread(program::initSimStepByStep).start();
        buttonStartStepByStep.setDisable(true);
        labelStatus.setText("Status: Running");
        comboBox.setDisable(true);
    }

    public void finishInitStepByStep() {
        buttonStep.setDisable(false);
        buttonStopStepByStep.setDisable(false);
        labelStatus.setText("Status: Ready");
    }

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
        labelStatus.setText("Status: Ready");
        labelStepByStep.setDisable(false);
        labelStepByStepCurrTime.setDisable(false);
        buttonStep.setDisable(true);
        buttonStartStepByStep.setDisable(false);
        buttonStopStepByStep.setDisable(true);

        labelRunByTime.setDisable(true);
        labelRunByTimeCurrTime.setDisable(true);
        labelRunByTimeMaxTime.setDisable(true);
        buttonStartRunByTime.setDisable(true);
        buttonStopRunByTime.setDisable(true);
        textFieldMaxTime.setDisable(true);

        comboBox.setDisable(false);
    }

    private void runByTimeEnable() {
        labelStatus.setText("Status: Ready");
        labelStepByStep.setDisable(true);
        labelStepByStepCurrTime.setDisable(true);
        buttonStep.setDisable(true);
        buttonStartStepByStep.setDisable(true);
        buttonStopStepByStep.setDisable(true);

        labelRunByTime.setDisable(false);
        labelRunByTimeCurrTime.setDisable(false);
        labelRunByTimeMaxTime.setDisable(false);
        buttonStartRunByTime.setDisable(false);
        buttonStopRunByTime.setDisable(true);
        textFieldMaxTime.setDisable(false);

        comboBox.setDisable(false);
    }

    @FXML
    public void doStep() {
        new Thread(program::doStep).start();
        buttonStep.setDisable(true);
        buttonStopStepByStep.setDisable(true);
        labelStatus.setText("Status: Running");
    }

    public void finishDoStep() {
        finishInitStepByStep();
    }

    public void stopStepByStep() {
        //comboBox.setDisable(true);
        new Thread(program::stopSimStepByStep).start();
        buttonStep.setDisable(true);
        buttonStopStepByStep.setDisable(true);
        labelStatus.setText("Status: Running");
    }

    public void finishStopStepByStep() {
        stepByStepEnable();
    }

    public void appendTextAreaOutputLog(int b) {
        textAreaOutputLog.appendText(String.valueOf((char) b));
    }

    public void appendLineTextAreaQueue(String text) {
        textAreaQueue.appendText(text + "\n");
    }

    public void removeLineTextAreaQueue(String text) {
        String areaText = textAreaQueue.getText();
        String result = areaText.replaceAll(text + "\n", "");
        textAreaQueue.setText(result);
    }

    public void appendLineTextAreaBasicCare(String text) {
        textAreaBasicCare.appendText(text + "\n");
    }

    public void removeLineTextAreaBasicCare(String text) {
        String areaText = textAreaBasicCare.getText();
        String result = areaText.replaceAll(text + "\n", "");
        textAreaBasicCare.setText(result);
    }

    public void appendLineTextAreaIntensiveCare(String text) {
        textAreaIntensiveCare.appendText(text + "\n");
    }

    public void removeLineTextAreaIntensiveCare(String text) {
        String areaText = textAreaIntensiveCare.getText();
        String result = areaText.replaceAll(text + "\n", "");
        textAreaIntensiveCare.setText(result);
    }

    public void appendLineTextAreaDead(String text) {
        textAreaDead.appendText(text + "\n");
    }

    public void appendLineTextAreaHealthy(String text) {
        textAreaHealthy.appendText(text + "\n");
    }

}
