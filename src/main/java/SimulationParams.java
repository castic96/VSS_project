/**
 * Stores all simulation parameters.
 */
public class SimulationParams {

    private int numberOfBedsBasicCareUnit;
    private int numberOfBedsIntensiveCareUnit;

    private double inputLambda;

    private double basicCareUnitMu;
    private double basicCareUnitSigma;
    private double intensiveCareUnitMu;

    private double pFromBasicToIntensive;
    private double pDeathBasicCareUnit;
    private double pDeathIntensiveCareUnit;

    private double maxTimeInQueue;

    public SimulationParams(int numberOfBedsBasicCareUnit, int numberOfBedsIntensiveCareUnit, double inputLambda, double basicCareUnitMu, double basicCareUnitSigma, double intensiveCareUnitMu, double pFromBasicToIntensive, double pDeathBasicCareUnit, double pDeathIntensiveCareUnit, double maxTimeInQueue) {
        this.numberOfBedsBasicCareUnit = numberOfBedsBasicCareUnit;
        this.numberOfBedsIntensiveCareUnit = numberOfBedsIntensiveCareUnit;
        this.inputLambda = inputLambda;
        this.basicCareUnitMu = basicCareUnitMu;
        this.basicCareUnitSigma = basicCareUnitSigma;
        this.intensiveCareUnitMu = intensiveCareUnitMu;
        this.pFromBasicToIntensive = pFromBasicToIntensive;
        this.pDeathBasicCareUnit = pDeathBasicCareUnit;
        this.pDeathIntensiveCareUnit = pDeathIntensiveCareUnit;
        this.maxTimeInQueue = maxTimeInQueue;
    }

    public int getNumberOfBedsBasicCareUnit() {
        return numberOfBedsBasicCareUnit;
    }

    public int getNumberOfBedsIntensiveCareUnit() {
        return numberOfBedsIntensiveCareUnit;
    }

    public double getInputLambda() {
        return inputLambda;
    }

    public double getBasicCareUnitMu() {
        return basicCareUnitMu;
    }

    public double getBasicCareUnitSigma() {
        return basicCareUnitSigma;
    }

    public double getIntensiveCareUnitMu() {
        return intensiveCareUnitMu;
    }

    public double getpFromBasicToIntensive() {
        return pFromBasicToIntensive;
    }

    public double getpDeathBasicCareUnit() {
        return pDeathBasicCareUnit;
    }

    public double getpDeathIntensiveCareUnit() {
        return pDeathIntensiveCareUnit;
    }

    public double getMaxTimeInQueue() {
        return maxTimeInQueue;
    }
}
