public class SimulationResults {

    private double basicCareRho;
    private double intensiveCareRho;
    private double totalRho;

    private double basicCareAverage;
    private double intensiveCareAverage;
    private double totalAverage;

    private double lw;
    private double tw;
    private double twForAllLinks;

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

    public double getBasicCareRho() {
        return basicCareRho;
    }

    public double getIntensiveCareRho() {
        return intensiveCareRho;
    }

    public double getTotalRho() {
        return totalRho;
    }

    public double getBasicCareAverage() {
        return basicCareAverage;
    }

    public double getIntensiveCareAverage() {
        return intensiveCareAverage;
    }

    public double getTotalAverage() {
        return totalAverage;
    }

    public double getLw() {
        return lw;
    }

    public double getTw() {
        return tw;
    }

    public double getTwForAllLinks() {
        return twForAllLinks;
    }

}
