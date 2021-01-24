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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controller for GUI window.
 */
public class SimulationWindowController implements Initializable {

    // ----- INSTANCE VARIABLES -----

    /** Instance of created program. */
    private final Program program;

    /** Label for chosen mode. */
    @FXML
    private Label modeLbl;

    /** Instance of progress bar. */
    @FXML
    private ProgressBar progressBar;

    /** Label 'Input lambda'. */
    @FXML
    private Label labelInputLambda;

    /** Label 'Basic care unit - mu'. */
    @FXML
    private Label labelBasicCareUnitMu;

    /** Label 'Basic care unit - sigma'. */
    @FXML
    private Label labelBasicCareUnitSigma;

    /** Label 'Intensive care unit - mu'. */
    @FXML
    private Label labelIntensiveCareUnitMu;

    /** Label 'P - from basic to intensive'. */
    @FXML
    private Label labelPFromBasicToIntensive;

    /** Label 'P - death basic care unit'. */
    @FXML
    private Label labelPDeathBasicCareUnit;

    /** Label 'P - death intensive care unit'. */
    @FXML
    private Label labelPDeathIntensiveCareUnit;

    /** Label 'Max time in queue'. */
    @FXML
    private Label labelMaxTimeInQueue;

    /** Combo box for choose the mode. */
    @FXML
    private ComboBox<String> comboBox;

    /** Label 'Step by step'. */
    @FXML
    private Label labelStepByStep;

    /** Label 'Run by time'. */
    @FXML
    private Label labelRunByTime;

    /** Label 'Current time' in Step by step section. */
    @FXML
    private Label labelStepByStepCurrTime;

    /** Label 'Current time' in Run by time section. */
    @FXML
    private Label labelRunByTimeCurrTime;

    /** Label 'Max time' in Run by time section. */
    @FXML
    private Label labelRunByTimeMaxTime;

    /** Label for the status of the application. */
    @FXML
    private Label labelStatus;

    /** Button 'Step'. */
    @FXML
    private Button buttonStep;

    /** Button 'Start' in Step by step section. */
    @FXML
    private Button buttonStartStepByStep;

    /** Button 'Stop' in Step by step section. */
    @FXML
    private Button buttonStopStepByStep;

    /** Button 'Start' in Run by time section. */
    @FXML
    private Button buttonStartRunByTime;

    /** Button 'Stop' in Run by time section. */
    @FXML
    private Button buttonStopRunByTime;

    /** Text field 'Max time'. */
    @FXML
    private TextField textFieldMaxTime;

    /** Text area for output log. */
    @FXML
    private TextArea textAreaOutputLog;

    /** Text area for patients in queue. */
    @FXML
    private TextArea textAreaQueue;

    /** Text area for patients in basic care. */
    @FXML
    private TextArea textAreaBasicCare;

    /** Text area for patients in intensive care. */
    @FXML
    private TextArea textAreaIntensiveCare;

    /** Text area for dead patients. */
    @FXML
    private TextArea textAreaDead;

    /** Text area for healthy patients. */
    @FXML
    private TextArea textAreaHealthy;

    /** Text area for results of the simulation. */
    @FXML
    private TextArea textAreaResults;

    /** Label for count of beds in basic care unit. */
    @FXML
    private Label labelCurrentBedsBasicCare;

    /** Label for count of beds in intensive care unit. */
    @FXML
    private Label labelCurrentBedsIntensiveCare;

    /** Label for current value of lambda. */
    @FXML
    private Label labelCurrentInputLambda;

    /** Label for current value of mu on basic care unit. */
    @FXML
    private Label labelCurrentBasicCareMu;

    /** Label for current value of sigma on basic care unit. */
    @FXML
    private Label labelCurrentBasicCareSigma;

    /** Label for current value of mu on intensive care unit. */
    @FXML
    private Label labelCurrentIntensiveCareMu;

    /** Label for current probability to move from basic to intensive care. */
    @FXML
    private Label labelCurrentPFromBasicToIntensive;

    /** Label for current probability to die on basic care. */
    @FXML
    private Label labelCurrentPDeathBasicCare;

    /** Label for current probability to die on intensive care. */
    @FXML
    private Label labelCurrentPDeathIntensiveCare;

    /** Label for current value of max time in queue. */
    @FXML
    private Label labelCurrentMaxTimeInQueue;

    /** Label for current basic server count. */
    @FXML
    private Label labelMaxBasicServerCount;

    /** Label for current intensive server count. */
    @FXML
    private Label labelMaxIntensiveServerCount;

    /** Text field for input lambda value. */
    @FXML
    private TextField textFieldInputLambda;

    /** Text field for basic care unit mu. */
    @FXML
    private TextField textFieldBasicCareMu;

    /** Text field for basic care unit sigma. */
    @FXML
    private TextField textFieldBasicCareSigma;

    /** Text field for intensive care unit mu. */
    @FXML
    private TextField textFieldIntensiveCareMu;

    /** Text field for probability to move from basic to intensive care. */
    @FXML
    private TextField textFieldPFromBasicToIntensive;

    /** Text field for probability to die on basic care. */
    @FXML
    private TextField textFieldPDeathBasicCare;

    /** Text field for probability to die on intensive care. */
    @FXML
    private TextField textFieldPDeathIntensiveCare;

    /** Text field for max time in queue. */
    @FXML
    private TextField textFieldMaxTimeInQueue;

    /** Button for export results as CSV file. */
    @FXML
    private Button buttonExportDetailedResults;

    /**
     * Creates new instance of SimulationWindowController.
     */
    public SimulationWindowController() {
        program = new Program(this);
    }

    /**
     * Sets all elements to default values.
     */
    private void clear() {
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

    /**
     * Initializes simulation configuration.
     */
    private void initConfigurations() {
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

    /**
     * Updates simulation configuration.
     */
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

    /**
     * Initializes step by step simulation.
     */
    public void initStepByStep() {
        clear();
        buttonStartStepByStep.setDisable(true);
        buttonExportDetailedResults.setDisable(true);
        labelStatus.setText("Status: Running");
        comboBox.setDisable(true);
        new Thread(program::initSimStepByStep).start();
    }

    /**
     * Starts run by time simulation.
     */
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

    /**
     * Shows error alert, if a field is empty.
     * @param emptyField Empty field.
     */
    private static void emptyAlertError(String emptyField) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Empty Field Error");
        alert.setHeaderText("Empty Field Error");
        alert.setContentText("Field " + emptyField + " is empty. Expected value is positive double.");

        alert.showAndWait();
    }

    /**
     * Shows error alert, if there is some problem with parsing inputs.
     * @param wrongInputs Map with wrong inputs.
     */
    private static void parsingAlertError(Map<String, String> wrongInputs) {
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

    /**
     * Finishes initialization part of step by step simulation.
     */
    public void finishInitStepByStep() {
        buttonStep.setDisable(false);
        buttonStopStepByStep.setDisable(false);
        labelStatus.setText("Status: Ready");
    }

    /**
     * Initializes GUI.
     * @param url URL.
     * @param resourceBundle Resource.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboBox.getItems().addAll(LaunchType.STEP_BY_STEP.getValue(), LaunchType.RUN_BY_TIME.getValue());
        comboBox.setEditable(false);
        comboBox.setValue(LaunchType.STEP_BY_STEP.getValue());
        initConfigurations();
        stepByStepEnable();
    }

    /**
     * Changes mode of simulation, if the combo box is changed.
     */
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

    /**
     * Enables all fields for step by step simulation.
     */
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

        modeLbl.setText("Mode: Step by step");
    }

    /**
     * Enables all fields for run by time simulation.
     */
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

        modeLbl.setText("Mode: Run by time");
    }

    /**
     * Realize one step (in Step by step mode).
     */
    @FXML
    public void doStep() {
        buttonStep.setDisable(true);
        buttonStopStepByStep.setDisable(true);
        labelStatus.setText("Status: Running");
        new Thread(program::doStep).start();
    }

    /**
     * Finishes step part of step by step simulation.
     */
    public void finishDoStep() {
        finishInitStepByStep();
    }

    /**
     * Stops step by step simulation.
     */
    public void stopStepByStep() {
        buttonStep.setDisable(true);
        buttonStopStepByStep.setDisable(true);
        labelStatus.setText("Status: Running");
        new Thread(program::stopSimStepByStep).start();
    }

    /**
     * Finishes stop part of step by step simulation.
     */
    public void finishStopStepByStep() {
        buttonExportDetailedResults.setDisable(false);
        stepByStepEnable();
    }

    /**
     * Finishes run by time simulation.
     */
    public void finishRunByTime() {
        buttonExportDetailedResults.setDisable(false);
        runByTimeEnable();
    }

    /**
     * Appends text to output log.
     * @param b Text to append.
     */
    public void appendTextAreaOutputLog(int b) {
        textAreaOutputLog.appendText(String.valueOf((char) b));
    }

    /**
     * Sets results to text area.
     * @param text Text to set.
     */
    public void setTextAreaResults(String text) {
        textAreaResults.setText(text);
    }

    /**
     * Appends text line to text area for queue.
     * @param text Text line.
     */
    public void appendLineTextAreaQueue(String text) {
        textAreaQueue.appendText(text + "\n");
    }

    /**
     * Removes text line from text area for queue.
     * @param text Text line.
     */
    public void removeLineTextAreaQueue(String text) {
        String areaText = textAreaQueue.getText();
        String result = areaText.replaceAll(text + "\n", "");
        textAreaQueue.setText(result);
    }

    /**
     * Appends text line to text area for basic care.
     * @param text Text line.
     */
    public void appendLineTextAreaBasicCare(String text) {
        textAreaBasicCare.appendText(text + "\n");
    }

    /**
     * Removes text line from text area for basic care.
     * @param text Text line.
     */
    public void removeLineTextAreaBasicCare(String text) {
        String areaText = textAreaBasicCare.getText();
        String result = areaText.replaceAll(text + "\n", "");
        textAreaBasicCare.setText(result);
    }

    /**
     * Appends text line to text area for intensive care.
     * @param text Text line.
     */
    public void appendLineTextAreaIntensiveCare(String text) {
        textAreaIntensiveCare.appendText(text + "\n");
    }

    /**
     * Removes text line from text area for intensive care.
     * @param text Text line.
     */
    public void removeLineTextAreaIntensiveCare(String text) {
        String areaText = textAreaIntensiveCare.getText();
        String result = areaText.replaceAll(text + "\n", "");
        textAreaIntensiveCare.setText(result);
    }

    /**
     * Appends text line to text area for dead patients.
     * @param text Text line.
     */
    public void appendLineTextAreaDead(String text) {
        textAreaDead.appendText(text + "\n");
    }

    /**
     * Appends text line to text area for healthy patients.
     * @param text Text line.
     */
    public void appendLineTextAreaHealthy(String text) {
        textAreaHealthy.appendText(text + "\n");
    }

    /**
     * Stops run by time simulation.
     */
    public void stopRunByTime() {
        buttonStopRunByTime.setDisable(true);
        new Thread(program::setRunningFalse).start();
    }

    /**
     * Sets progress bar value.
     * @param value Set value.
     */
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

    /**
     * Sets current time to 'Current time' field in Run by time section.
     * @param value Value of current time.
     */
    public void setCurrentTimeRunByTime(int value) {
        labelRunByTimeCurrTime.setText("Current time: " + value);
    }

    /**
     * Sets current time to 'Current time' field in Step by step section.
     * @param value Value of current time.
     */
    public void setCurrentTimeStepByStep(int value) {
        labelStepByStepCurrTime.setText("Current time: " + value);
    }

    /**
     * Exports results of the simulation to CSV file.
     * @param actionEvent Handler to action event.
     */
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

    /**
     * Shows error alert for wrong path to file.
     */
    private static void wrongPathError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Wrong Path Error");
        alert.setHeaderText("Wrong Path");
        alert.setContentText("Destination to save Detailed Results file is incorrect.");

        alert.showAndWait();
    }

}
