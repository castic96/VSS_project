package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.ResourceBundle;

public class GraphController implements Initializable {

    @FXML
    private LineChart lineChart;

    private double[] results;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        // todo
        series.setName("Series 1");
        series.getData().add(new XYChart.Data<>(1, 20));
        series.getData().add(new XYChart.Data<>(2, 30));
        series.getData().add(new XYChart.Data<>(3, 40));

        lineChart.getData().add(series);
    }

    public void setResults(double[] results) {
        this.results = results;
    }
}
