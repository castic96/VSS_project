/**
 * Stores all simulation parameters.
 */
public class SimulationParams {

    /** Number of beds in basic care unit. */
    private int numberOfBedsBasicCareUnit;
    /** Number of beds in intensive care unit. */
    private int numberOfBedsIntensiveCareUnit;

    /** Input parameter - lambda (Poisson distribution). */
    private double inputLambda;

    /** Basic care parameter - mu (gauss distribution parameter). */
    private double basicCareUnitMu;
    /** Basic care parameter - sigma (gauss distribution parameter). */
    private double basicCareUnitSigma;
    /** Intensive care parameter - mu (exponential distribution parameter). */
    private double intensiveCareUnitMu;

    /** Probability of transfer from basic care to intensive care. */
    private double pFromBasicToIntensive;
    /** Probability of death in basic care. */
    private double pDeathBasicCareUnit;
    /** Probability of death in intensive care. */
    private double pDeathIntensiveCareUnit;

    /** Maximum time which can be spent in queue (if exceeded -> death). */
    private double maxTimeInQueue;

    /**
     * Creates new simulation parameters.
     *
     * @param numberOfBedsBasicCareUnit number of beds in basic care unit
     * @param numberOfBedsIntensiveCareUnit number of beds in intensive care unit
     * @param inputLambda input parameter - lambda (Poisson distribution)
     * @param basicCareUnitMu basic care parameter - mu (gauss distribution parameter)
     * @param basicCareUnitSigma basic care parameter - sigma (gauss distribution parameter)
     * @param intensiveCareUnitMu intensive care parameter - mu (exponential distribution parameter)
     * @param pFromBasicToIntensive probability of transfer from basic care to intensive care
     * @param pDeathBasicCareUnit probability of death in basic care
     * @param pDeathIntensiveCareUnit probability of death in intensive care
     * @param maxTimeInQueue maximum time which can be spent in queue (if exceeded -> death)
     */
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

    /**
     * Returns number of beds in basic care unit.
     *
     * @return number of beds in basic care unit
     */
    public int getNumberOfBedsBasicCareUnit() {
        return numberOfBedsBasicCareUnit;
    }

    /**
     * Returns number of beds in intensive care unit.
     *
     * @return number of beds in intensive care unit
     */
    public int getNumberOfBedsIntensiveCareUnit() {
        return numberOfBedsIntensiveCareUnit;
    }

    /**
     * Returns input parameter - lambda (Poisson distribution).
     *
     * @return input parameter - lambda (Poisson distribution)
     */
    public double getInputLambda() {
        return inputLambda;
    }

    /**
     * Returns basic care parameter - mu (gauss distribution parameter).
     *
     * @return basic care parameter - mu (gauss distribution parameter)
     */
    public double getBasicCareUnitMu() {
        return basicCareUnitMu;
    }

    /**
     * Returns basic care parameter - sigma (gauss distribution parameter).
     *
     * @return basic care parameter - sigma (gauss distribution parameter)
     */
    public double getBasicCareUnitSigma() {
        return basicCareUnitSigma;
    }

    /**
     * Returns intensive care parameter - mu (exponential distribution parameter).
     *
     * @return intensive care parameter - mu (exponential distribution parameter)
     */
    public double getIntensiveCareUnitMu() {
        return intensiveCareUnitMu;
    }

    /**
     * Returns probability of transfer from basic care to intensive care.
     *
     * @return probability of transfer from basic care to intensive care
     */
    public double getpFromBasicToIntensive() {
        return pFromBasicToIntensive;
    }

    /**
     * Returns probability of death in basic care.
     *
     * @return probability of death in basic care
     */
    public double getpDeathBasicCareUnit() {
        return pDeathBasicCareUnit;
    }

    /**
     * Returns probability of death in intensive care.
     *
     * @return probability of death in intensive care
     */
    public double getpDeathIntensiveCareUnit() {
        return pDeathIntensiveCareUnit;
    }

    /**
     * Returns maximum time which can be spent in queue (if exceeded -> death).
     *
     * @return maximum time which can be spent in queue (if exceeded -> death)
     */
    public double getMaxTimeInQueue() {
        return maxTimeInQueue;
    }
}
