package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.Constants;
import model.Program;
import model.Utils;
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

    @FXML
    private Label labelCurrentBedsBasicCare;

    @FXML
    private Label labelCurrentBedsIntensiveCare;

    @FXML
    private Label labelCurrentInputLambda;

    @FXML
    private Label labelCurrentBasicCareMu;

    @FXML
    private Label labelCurrentBasicCareSigma;

    @FXML
    private Label labelCurrentIntensiveCareMu;

    @FXML
    private Label labelCurrentPFromBasicToIntensive;

    @FXML
    private Label labelCurrentPDeathBasicCare;

    @FXML
    private Label labelCurrentPDeathIntensiveCare;

    @FXML
    private Label labelCurrentMaxTimeInQueue;

    @FXML
    private Label labelMaxBasicServerCount;

    @FXML
    private Label labelMaxIntensiveServerCount;

    @FXML
    private TextField textFieldInputLambda;

    @FXML
    private TextField textFieldBasicCareMu;

    @FXML
    private TextField textFieldBasicCareSigma;

    @FXML
    private TextField textFieldIntensiveCareMu;

    @FXML
    private TextField textFieldPFromBasicToIntensive;

    @FXML
    private TextField textFieldPDeathBasicCare;

    @FXML
    private TextField textFieldPDeathIntensiveCare;

    @FXML
    private TextField textFieldMaxTimeInQueue;

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

    public void initConfigurations() {
        program.loadConfigurationFile();

        labelCurrentBedsBasicCare.setText(String.valueOf(Constants.NUMBER_OF_BED_BASIC_UNIT));
        labelCurrentBedsIntensiveCare.setText(String.valueOf(Constants.NUMBER_OF_BED_INTENSIVE_CARE_UNIT));
        labelCurrentInputLambda.setText(String.valueOf(Constants.INPUT_LAMBDA));
        labelCurrentBasicCareMu.setText(String.valueOf(Constants.BASIC_CARE_UNIT_MU));
        labelCurrentBasicCareSigma.setText(String.valueOf(Constants.BASIC_CARE_UNIT_SIGMA));
        labelCurrentIntensiveCareMu.setText(String.valueOf(Constants.INTENSIVE_CARE_UNIT_MU));
        labelCurrentPFromBasicToIntensive.setText(String.valueOf(Constants.P_FROM_BASIC_TO_INTENSIVE));
        labelCurrentPDeathBasicCare.setText(String.valueOf(Constants.P_DEATH_BASIC_CARE_UNIT));
        labelCurrentPDeathIntensiveCare.setText(String.valueOf(Constants.P_DEATH_INTENSIVE_CARE_UNIT));
        labelCurrentMaxTimeInQueue.setText(String.valueOf(Constants.MAX_TIME_IN_QUEUE));

        labelMaxBasicServerCount.setText(String.valueOf(Constants.NUMBER_OF_BED_BASIC_UNIT));
        labelMaxIntensiveServerCount.setText(String.valueOf(Constants.NUMBER_OF_BED_INTENSIVE_CARE_UNIT));
    }

    @FXML
    public void updateConfig() {
        String newText;
        double newDouble;

        newText = textFieldInputLambda.getText();
        newDouble = Utils.validateDoublePositive(newText);
        if (newDouble > 0) {
            labelCurrentInputLambda.setText(newText);
            Constants.INPUT_LAMBDA = newDouble;
        }

        newText = textFieldBasicCareMu.getText();
        newDouble = Utils.validateDoublePositive(newText);
        if (newDouble > 0) {
            labelCurrentBasicCareMu.setText(newText);
            Constants.BASIC_CARE_UNIT_MU = newDouble;
        }

        newText = textFieldBasicCareSigma.getText();
        newDouble = Utils.validateDoublePositive(newText);
        if (newDouble > 0) {
            labelCurrentBasicCareSigma.setText(newText);
            Constants.BASIC_CARE_UNIT_SIGMA = newDouble;
        }

        newText = textFieldIntensiveCareMu.getText();
        newDouble = Utils.validateDoublePositive(newText);
        if (newDouble > 0) {
            labelCurrentIntensiveCareMu.setText(newText);
            Constants.INTENSIVE_CARE_UNIT_MU = newDouble;
        }

        newText = textFieldPFromBasicToIntensive.getText();
        newDouble = Utils.validateDoublePositive(newText);
        if (newDouble > 0) {
            labelCurrentPFromBasicToIntensive.setText(newText);
            Constants.P_FROM_BASIC_TO_INTENSIVE = newDouble;
        }

        newText = textFieldPDeathBasicCare.getText();
        newDouble = Utils.validateDoublePositive(newText);
        if (newDouble > 0) {
            labelCurrentPDeathBasicCare.setText(newText);
            Constants.P_DEATH_BASIC_CARE_UNIT = newDouble;
        }

        newText = textFieldPDeathIntensiveCare.getText();
        newDouble = Utils.validateDoublePositive(newText);
        if (newDouble > 0) {
            labelCurrentPDeathIntensiveCare.setText(newText);
            Constants.P_DEATH_INTENSIVE_CARE_UNIT = newDouble;
        }

        newText = textFieldMaxTimeInQueue.getText();
        newDouble = Utils.validateDoublePositive(newText);
        if (newDouble > 0) {
            labelCurrentMaxTimeInQueue.setText(newText);
            Constants.MAX_TIME_IN_QUEUE = newDouble;
        }
    }

    public void initStepByStep() {
        clear();
        buttonStartStepByStep.setDisable(true);
        labelStatus.setText("Status: Running");
        comboBox.setDisable(true);
        new Thread(program::initSimStepByStep).start();
    }

    public void startRunByTime() {
        clear();
        textFieldMaxTime.setDisable(true);
        buttonStartRunByTime.setDisable(true);
        labelStatus.setText("Status: Running");
        comboBox.setDisable(true);

        double maxTime = tryParseMaxTimeValue(textFieldMaxTime.getText());

        if (Double.isNaN(maxTime)) {
            parsingAllertError(textFieldMaxTime.getText());
            textFieldMaxTime.setDisable(false);
            buttonStartRunByTime.setDisable(false);
            labelStatus.setText("Status: Ready");
            comboBox.setDisable(false);
            return;
        }

        new Thread(() -> program.runSimRunByTime(maxTime)).start();
    }

    public static void parsingAllertError(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Parse Error");

        alert.setHeaderText("Parse Error");
        alert.setContentText("Entered value " + text + " is not convertible to double!");

        alert.showAndWait();
    }

    private double tryParseMaxTimeValue(String text) {
        try {
            double value = Double.parseDouble(text);
            return value;
        }
        catch (NumberFormatException e) {
            return Double.NaN;
        }
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
        initConfigurations();
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
        buttonStep.setDisable(true);
        buttonStopStepByStep.setDisable(true);
        labelStatus.setText("Status: Running");
        new Thread(program::doStep).start();
    }

    public void finishDoStep() {
        finishInitStepByStep();
    }

    public void stopStepByStep() {
        buttonStep.setDisable(true);
        buttonStopStepByStep.setDisable(true);
        labelStatus.setText("Status: Running");
        new Thread(program::stopSimStepByStep).start();
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
