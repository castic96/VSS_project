package model;

import model.enums.OutputParams;

import java.util.Arrays;

public class ScenarioResults {

    private OutputParams outputParam;

    private double[] results;

    public ScenarioResults(OutputParams outputParam, double[] results) {
        this.outputParam = outputParam;
        this.results = results;
    }

    @Override
    public String toString() {
        return "ScenarioResults{" +
                "outputParam=" + outputParam +
                ", results=" + Arrays.toString(results) +
                '}';
    }
}
