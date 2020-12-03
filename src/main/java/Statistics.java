import java.util.List;

public class Statistics {

    public static SimulationResults calculateResults(List<BasicCareUnitServer> basicCareUnitServerList, List<IntensiveCareUnitServer> intensiveCareUnitServerList, double totalTime) {
        // basic care
        double basicCareSum = 0.0;
        for (BasicCareUnitServer basicCareUnitServer : basicCareUnitServerList) {
            basicCareSum += basicCareUnitServer.getTransTq();
        }

        //double basicCareAverage = basicCareSum / basicCareUnitServerList.size();

        // intensive care
        double intensiveCareSum = 0.0;
        for (IntensiveCareUnitServer intensiveCareUnitServer : intensiveCareUnitServerList) {
            intensiveCareSum += intensiveCareUnitServer.getTransTq();
        }

        //double intensiveCareAverage = intensiveCareSum / intensiveCareUnitServerList.size();

        // rhos
        double basicCareRho = basicCareSum / totalTime;
        double intensiveCareRho = intensiveCareSum / totalTime;
        double totalRho = (basicCareRho + intensiveCareRho) / 2.0;

        SimulationResults results = new SimulationResults(basicCareRho, intensiveCareRho, totalRho);
        return results;
    }
}
