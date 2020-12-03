import java.util.List;

public class Statistics {

    public static SimulationResults calculateResults(List<BasicCareUnitServer> basicCareUnitServerList, List<IntensiveCareUnitServer> intensiveCareUnitServerList, double totalTime) {
        // basic care
        double basicCareSum = 0.0;
        int basicCarePatientsSum = 0;
        for (BasicCareUnitServer basicCareUnitServer : basicCareUnitServerList) {
            basicCareSum += basicCareUnitServer.getTransTq();
            basicCarePatientsSum += basicCareUnitServer.getCounter();
        }

        // intensive care
        double intensiveCareSum = 0.0;
        int intensiveCarePatientsSum = 0;
        for (IntensiveCareUnitServer intensiveCareUnitServer : intensiveCareUnitServerList) {
            intensiveCareSum += intensiveCareUnitServer.getTransTq();
            intensiveCarePatientsSum += intensiveCareUnitServer.getCounter();
        }

        // Tqs
        double basicCareAverage = basicCareSum /  basicCarePatientsSum;
        double intensiveCareAverage = intensiveCareSum / intensiveCarePatientsSum;
        double totalAverage = (basicCareAverage + intensiveCareAverage) / 2.0;

        // rhos
        double basicCareRho = basicCareSum / totalTime;
        double intensiveCareRho = intensiveCareSum / totalTime;
        double totalRho = (basicCareRho + intensiveCareRho) / 2.0;

        SimulationResults results = new SimulationResults(basicCareRho, intensiveCareRho, totalRho, basicCareAverage, intensiveCareAverage, totalAverage);
        return results;
    }
}
