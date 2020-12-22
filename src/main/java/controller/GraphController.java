package controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import model.ScenarioResults;
import model.enums.InputParams;

import java.util.Map;

public class GraphController {

    @FXML
    private LineChart lineChart;

    public void addSeries(InputParams inputParam, ScenarioResults scenarioResults) {
        Map<Double, Double> results = scenarioResults.getResults();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        series.setName("Scenario 1");

        for (double key : results.keySet()) {
            series.getData().add(new XYChart.Data<>(key, results.get(key))); // todo - test it if it works
        }

        lineChart.getXAxis().setLabel(inputParam.name());
        lineChart.getYAxis().setLabel(scenarioResults.getOutputParam().name());

        lineChart.getData().add(series);
    }
}
