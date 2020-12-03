public class SimulationResults {

    private double basicCareRho;
    private double intensiveCareRho;
    private double totalRho;
    private double basicCareAverage;
    private double intensiveCareAverage;
    private double totalAverage;


    public SimulationResults(double basicCareRho, double intensiveCareRho, double totalRho, double basicCareAverage, double intensiveCareAverage, double totalAverage) {
        this.basicCareRho = basicCareRho;
        this.intensiveCareRho = intensiveCareRho;
        this.totalRho = totalRho;
        this.basicCareAverage = basicCareAverage;
        this.intensiveCareAverage = intensiveCareAverage;
        this.totalAverage = totalAverage;
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
}
