package model;

import java.util.Map;

public class ScenarioResults {

    /** key = input param value, value = output param value */
    private final Map<Double, Double> results;

    public ScenarioResults(Map<Double, Double> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "ScenarioResults{" +
                "results=" + results +
                '}';
    }

    public Map<Double, Double> getResults() {
        return results;
    }
}
