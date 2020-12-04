import java.util.List;

/**
 * Class for calculating statistical numbers.
 */
public class Statistics {

    /**
     * Calculates results.
     *
     * @param basicCareUnitServerList basic care servers
     * @param intensiveCareUnitServerList intensive care servers
     * @param totalTime total simulation time
     * @param basicCareUnitQueue queue for basic care
     * @return calculated results
     */
    public static SimulationResults calculateResults(List<BasicCareUnitServer> basicCareUnitServerList, List<IntensiveCareUnitServer> intensiveCareUnitServerList, double totalTime, BasicCareUnitQueue basicCareUnitQueue) {
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

        // init results
        SimulationResults results = new SimulationResults(basicCareRho, intensiveCareRho, totalRho, basicCareAverage, intensiveCareAverage, totalAverage,
                basicCareUnitQueue.getLw(), basicCareUnitQueue.getTw(), basicCareUnitQueue.getTwForAllLinks());
        return results;
    }
}
