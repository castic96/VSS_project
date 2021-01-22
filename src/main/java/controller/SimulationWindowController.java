package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import model.Constants;
import model.Program;
import model.Utils;
import model.enums.LaunchType;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimulationWindowController implements Initializable {

    private final Program program;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label labelInputLambda;

    @FXML
    private Label labelBasicCareUnitMu;

    @FXML
    private Label labelBasicCareUnitSigma;

    @FXML
    private Label labelIntensiveCareUnitMu;

    @FXML
    private Label labelPFromBasicToIntensive;

    @FXML
    private Label labelPDeathBasicCareUnit;

    @FXML
    private Label labelPDeathIntensiveCareUnit;

    @FXML
    private Label labelMaxTimeInQueue;

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
    private TextArea textAreaResults;

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

    @FXML
    private Button buttonExportDetailedResults;


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
        textAreaResults.clear();
        progressBar.setProgress(0);
        labelRunByTimeCurrTime.setText("Current time: 0");
        labelStepByStepCurrTime.setText("Current time: 0");
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
        Map<String, String> wrongInputs = new HashMap<>();

        newText = textFieldInputLambda.getText();

        if (!newText.isEmpty()) {
            newDouble = Utils.validateDoublePositive(newText);
            if (newDouble >= 0) {
                labelCurrentInputLambda.setText(newText);
                Constants.INPUT_LAMBDA = newDouble;
            }
            else {
                wrongInputs.put(labelInputLambda.getText(), newText);
            }
        }

        newText = textFieldBasicCareMu.getText();

        if (!newText.isEmpty()) {
            newDouble = Utils.validateDoublePositive(newText);
            if (newDouble >= 0) {
                labelCurrentBasicCareMu.setText(newText);
                Constants.BASIC_CARE_UNIT_MU = newDouble;
            }
            else {
                wrongInputs.put(labelBasicCareUnitMu.getText(), newText);
            }
        }

        newText = textFieldBasicCareSigma.getText();

        if (!newText.isEmpty()) {
            newDouble = Utils.validateDoublePositive(newText);
            if (newDouble >= 0) {
                labelCurrentBasicCareSigma.setText(newText);
                Constants.BASIC_CARE_UNIT_SIGMA = newDouble;
            }
            else {
                wrongInputs.put(labelBasicCareUnitSigma.getText(), newText);
            }
        }

        newText = textFieldIntensiveCareMu.getText();

        if (!newText.isEmpty()) {
            newDouble = Utils.validateDoublePositive(newText);
            if (newDouble >= 0) {
                labelCurrentIntensiveCareMu.setText(newText);
                Constants.INTENSIVE_CARE_UNIT_MU = newDouble;
            }
            else {
                wrongInputs.put(labelIntensiveCareUnitMu.getText(), newText);
            }

        }

        newText = textFieldPFromBasicToIntensive.getText();

        if (!newText.isEmpty()) {
            newDouble = Utils.validateDoublePositive(newText);
            if (newDouble >= 0) {
                labelCurrentPFromBasicToIntensive.setText(newText);
                Constants.P_FROM_BASIC_TO_INTENSIVE = newDouble;
            }
            else {
                wrongInputs.put(labelPFromBasicToIntensive.getText(), newText);
            }
        }

        newText = textFieldPDeathBasicCare.getText();

        if (!newText.isEmpty()) {
            newDouble = Utils.validateDoublePositive(newText);
            if (newDouble >= 0) {
                labelCurrentPDeathBasicCare.setText(newText);
                Constants.P_DEATH_BASIC_CARE_UNIT = newDouble;
            }
            else {
                wrongInputs.put(labelPDeathBasicCareUnit.getText(), newText);
            }
        }

        newText = textFieldPDeathIntensiveCare.getText();

        if (!newText.isEmpty()) {
            newDouble = Utils.validateDoublePositive(newText);
            if (newDouble >= 0) {
                labelCurrentPDeathIntensiveCare.setText(newText);
                Constants.P_DEATH_INTENSIVE_CARE_UNIT = newDouble;
            }
            else {
                wrongInputs.put(labelPDeathIntensiveCareUnit.getText(), newText);
            }
        }

        newText = textFieldMaxTimeInQueue.getText();

        if (!newText.isEmpty()) {
            newDouble = Utils.validateDoublePositive(newText);
            if (newDouble >= 0) {
                labelCurrentMaxTimeInQueue.setText(newText);
                Constants.MAX_TIME_IN_QUEUE = newDouble;
            }
            else {
                wrongInputs.put(labelMaxTimeInQueue.getText(), newText);
            }
        }

        if (!wrongInputs.isEmpty()) {
            parsingAlertError(wrongInputs);
        }
    }

    public void initStepByStep() {
        clear();
        buttonStartStepByStep.setDisable(true);
        buttonExportDetailedResults.setDisable(true);
        labelStatus.setText("Status: Running");
        comboBox.setDisable(true);
        new Thread(program::initSimStepByStep).start();
    }

    public void startRunByTime() {
        program.setRunning(true);
        clear();
        progressBar.setDisable(false);
        textFieldMaxTime.setDisable(true);
        buttonStartRunByTime.setDisable(true);
        buttonExportDetailedResults.setDisable(true);
        labelStatus.setText("Status: Running");
        comboBox.setDisable(true);

        if (textFieldMaxTime.getText().isEmpty()) {
            emptyAlertError(labelRunByTimeMaxTime.getText());
            textFieldMaxTime.setDisable(false);
            buttonStartRunByTime.setDisable(false);
            labelStatus.setText("Status: Ready");
            comboBox.setDisable(false);
            return;
        }

        double maxTime = Utils.validateDoublePositive(textFieldMaxTime.getText());

        if (maxTime < 0) {
            //parsingAlertError(Map.of(labelRunByTimeMaxTime.getText(), textFieldMaxTime.getText()));

            Map<String, String> map = Stream.of(new String[][] {
                    { labelRunByTimeMaxTime.getText(), textFieldMaxTime.getText() }
            }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

            parsingAlertError(map);


            textFieldMaxTime.setDisable(false);
            buttonStartRunByTime.setDisable(false);
            labelStatus.setText("Status: Ready");
            comboBox.setDisable(false);
            return;
        }

        new Thread(() -> program.runSimRunByTime(maxTime)).start();
        buttonStopRunByTime.setDisable(false);
    }

    public static void emptyAlertError(String emptyField) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Empty Field Error");
        alert.setHeaderText("Empty Field Error");
        alert.setContentText("Field " + emptyField + " is empty. Expected value is positive double.");

        alert.showAndWait();
    }

    public static void parsingAlertError(Map<String, String> wrongInputs) {
        StringBuilder alertText = new StringBuilder();

        alertText.append("Following entered values are not convertible to positive double:");
        alertText.append("\n");

        for (Map.Entry<String, String> currentEntry : wrongInputs.entrySet()) {
            alertText.append(currentEntry.getKey());
            alertText.append(" ");
            alertText.append(currentEntry.getValue());
            alertText.append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Parse Error");
        alert.setHeaderText("Parse Error");
        alert.setContentText(alertText.toString());

        alert.showAndWait();
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
        System.out.println("Application ended: " + dtf.format(end));
        System.exit(0);
    }

    @FXML
    public void comboBoxChange() {
        if (comboBox.getValue().equals(LaunchType.STEP_BY_STEP.getValue())) {
            Constants.IS_STEP_BY_STEP = true;
            stepByStepEnable();
        }
        else {
            Constants.IS_STEP_BY_STEP = false;
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

        progressBar.setProgress(0);
        progressBar.setDisable(true);
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
        progressBar.setProgress(0);
        progressBar.setDisable(true);
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
        buttonExportDetailedResults.setDisable(false);
        stepByStepEnable();
    }

    public void finishRunByTime () {
        buttonExportDetailedResults.setDisable(false);
        runByTimeEnable();
    }

    public void appendTextAreaOutputLog(int b) {
        textAreaOutputLog.appendText(String.valueOf((char) b));
    }

    public void setTextAreaResults(String text) {
        textAreaResults.setText(text);
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

    public void stopRunByTime() {
        buttonStopRunByTime.setDisable(true);
        new Thread(program::setRunningFalse).start();
    }

    public void setProgressBarValue(double value) {
        if (value < 0) {
            progressBar.setProgress(0);
        }
        else if (value > 1) {
            progressBar.setProgress(1);
        }
        else {
            progressBar.setProgress(value);
        }
    }

    public void setCurrentTimeRunByTime(int value) {
        labelRunByTimeCurrTime.setText("Current time: " + value);
    }

    public void setCurrentTimeStepByStep(int value) {
        labelStepByStepCurrTime.setText("Current time: " + value);
    }

    @FXML
    public void exportDetailedResults(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile;

        fileChooser.setTitle("Export detailed results");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV", ".csv"));
        fileChooser.setInitialFileName("DetailedResults.csv");
        selectedFile = fileChooser.showSaveDialog(((Node)actionEvent.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            Utils.saveResultNumbers(program, selectedFile);
        }
        else {
            wrongPathError();
        }

    }

    public static void wrongPathError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Wrong Path Error");
        alert.setHeaderText("Wrong Path");
        alert.setContentText("Destination to save Detailed Results file is incorrect.");

        alert.showAndWait();
    }

}
