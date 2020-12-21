package model;

import model.enums.InputParams;
import model.enums.OutputParams;

public class ScenarioParams extends SimulationParams {

    private InputParams inputParam;

    private OutputParams outputParam;

    private double step;
    private int runsCount;

    /**
     * Creates new simulation parameters.
     *
     * @param inputParam
     * @param outputParam
     * @param step
     * @param runsCount
     * @param numberOfBedsBasicCareUnit     number of beds in basic care unit
     * @param numberOfBedsIntensiveCareUnit number of beds in intensive care unit
     * @param inputLambda                   input parameter - lambda (Poisson distribution)
     * @param basicCareUnitMu               basic care parameter - mu (gauss distribution parameter)
     * @param basicCareUnitSigma            basic care parameter - sigma (gauss distribution parameter)
     * @param intensiveCareUnitMu           intensive care parameter - mu (exponential distribution parameter)
     * @param pFromBasicToIntensive         probability of transfer from basic care to intensive care
     * @param pDeathBasicCareUnit           probability of death in basic care
     * @param pDeathIntensiveCareUnit       probability of death in intensive care
     * @param maxTimeInQueue                maximum time which can be spent in queue (if exceeded -> death)
     */
    public ScenarioParams(InputParams inputParam, OutputParams outputParam, double step, int runsCount,
            int numberOfBedsBasicCareUnit, int numberOfBedsIntensiveCareUnit, double inputLambda, double basicCareUnitMu, double basicCareUnitSigma, double intensiveCareUnitMu, double pFromBasicToIntensive, double pDeathBasicCareUnit, double pDeathIntensiveCareUnit, double maxTimeInQueue) {
        super(numberOfBedsBasicCareUnit, numberOfBedsIntensiveCareUnit, inputLambda, basicCareUnitMu, basicCareUnitSigma, intensiveCareUnitMu, pFromBasicToIntensive, pDeathBasicCareUnit, pDeathIntensiveCareUnit, maxTimeInQueue);

        this.inputParam = inputParam;
        this.outputParam = outputParam;
        this.step = step;
        this.runsCount = runsCount;
    }

    public InputParams getInputParam() {
        return inputParam;
    }

    public OutputParams getOutputParam() {
        return outputParam;
    }

    public double getStep() {
        return step;
    }

    public int getRunsCount() {
        return runsCount;
    }
}
