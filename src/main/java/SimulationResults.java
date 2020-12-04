/**
 * Stores simulation results.
 */
public class SimulationResults {

    /** Rho (load) in basic care. */
    private double basicCareRho;
    /** Rho (load) in intensive care. */
    private double intensiveCareRho;
    /** Rho (load) in whole system. */
    private double totalRho;

    /** Tq - mean time spent in basic care. */
    private double basicCareAverage;
    /** Tq - mean time spent in intensive care. */
    private double intensiveCareAverage;
    /** Tq - mean time spent in whole system. */
    private double totalAverage;

    /** Lw - mean length of queue. */
    private double lw;
    /** Tw - mean waiting time in queue. */
    private double tw;
    /** Tw - mean waiting time in queue for all elements. */
    private double twForAllLinks;

    /**
     * Creates new simulation results.
     *
     * @param basicCareRho rho (load) in basic care
     * @param intensiveCareRho rho (load) in intensive care
     * @param totalRho rho (load) in whole system
     * @param basicCareAverage Tq - mean time spent in basic care
     * @param intensiveCareAverage Tq - mean time spent in intensive care
     * @param totalAverage Tq - mean time spent in whole system
     * @param lw Lw - mean length of queue
     * @param tw Tw - mean waiting time in queue
     * @param twForAllLinks Tw - mean waiting time in queue for all elements
     */
    public SimulationResults(double basicCareRho, double intensiveCareRho, double totalRho, double basicCareAverage, double intensiveCareAverage, double totalAverage, double lw, double tw, double twForAllLinks) {
        this.basicCareRho = basicCareRho;
        this.intensiveCareRho = intensiveCareRho;
        this.totalRho = totalRho;
        this.basicCareAverage = basicCareAverage;
        this.intensiveCareAverage = intensiveCareAverage;
        this.totalAverage = totalAverage;
        this.lw = lw;
        this.tw = tw;
        this.twForAllLinks = twForAllLinks;
    }

    /**
     * Returns rho (load) in basic care.
     *
     * @return rho (load) in basic care
     */
    public double getBasicCareRho() {
        return basicCareRho;
    }

    /**
     * Returns rho (load) in intensive care.
     *
     * @return rho (load) in intensive care
     */
    public double getIntensiveCareRho() {
        return intensiveCareRho;
    }

    /**
     * Returns rho (load) in whole system.
     *
     * @return rho (load) in whole system
     */
    public double getTotalRho() {
        return totalRho;
    }

    /**
     * Returns Tq - mean time spent in basic care.
     *
     * @return Tq - mean time spent in basic care
     */
    public double getBasicCareAverage() {
        return basicCareAverage;
    }

    /**
     * Returns Tq - mean time spent in intensive care.
     *
     * @return Tq - mean time spent in intensive care
     */
    public double getIntensiveCareAverage() {
        return intensiveCareAverage;
    }

    /**
     * Returns Tq - mean time spent in whole system.
     *
     * @return Tq - mean time spent in whole system
     */
    public double getTotalAverage() {
        return totalAverage;
    }

    /**
     * Returns Lw - mean length of queue.
     *
     * @return Lw - mean length of queue
     */
    public double getLw() {
        return lw;
    }

    /**
     * Returns Tw - mean waiting time in queue.
     *
     * @return Tw - mean waiting time in queue
     */
    public double getTw() {
        return tw;
    }

    /**
     * Returns Tw - mean waiting time in queue for all elements.
     *
     * @return Tw - mean waiting time in queue for all elements
     */
    public double getTwForAllLinks() {
        return twForAllLinks;
    }

}
