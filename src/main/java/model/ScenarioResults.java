package model;

import model.enums.OutputParams;

import java.util.Arrays;
import java.util.Map;

public class ScenarioResults {

    private OutputParams outputParam;

    /** key = input param value, value = output param value */
    private Map<Double, Double> results;

    public ScenarioResults(OutputParams outputParam, Map<Double, Double> results) {
        this.outputParam = outputParam;
        this.results = results;
    }

    @Override
    public String toString() {
        return "ScenarioResults{" +
                "outputParam=" + outputParam +
                ", results=" + results +
                '}';
    }

    public Map<Double, Double> getResults() {
        return results;
    }

    public OutputParams getOutputParam() {
        return outputParam;
    }
}
