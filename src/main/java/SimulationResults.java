public class SimulationResults {

    private double basicCareRho;
    private double intensiveCareRho;
    private double totalRho;

    public SimulationResults(double basicCareRho, double intensiveCareRho, double totalRho) {
        this.basicCareRho = basicCareRho;
        this.intensiveCareRho = intensiveCareRho;
        this.totalRho = totalRho;
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
}
