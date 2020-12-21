import java.util.List;

/**
 * Class for calculating statistical numbers.
 */
public class Statistics {

    private static int NUMBER_OF_SERVERS = 2;

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
        double totalAverage = (basicCareAverage + intensiveCareAverage) / NUMBER_OF_SERVERS;

        // rhos
        double basicCareRho = (basicCareSum / totalTime) / basicCareUnitServerList.size();
        double intensiveCareRho = (intensiveCareSum / totalTime) / intensiveCareUnitServerList.size();
        double totalRho = (basicCareRho + intensiveCareRho) / NUMBER_OF_SERVERS;

        int totalDeadPatients =
                BasicCareUnitServer.getDeadPatientsCounter() + BasicCareUnitServer.getDeadPatientsNoFreeBedInICUCounter() // died in basic care
                + BasicCareUnitServer.getDiedInQueuePatientsCounter() // died in queue
                + IntensiveCareUnitServer.getDeadPatientsCounter();  // died in ICU
        int totalLeavingPatients = BasicCareUnitServer.getHealedPatientsCounter() + totalDeadPatients;

        // init results
        SimulationResults results = new SimulationResults(
                InputGenerator.getIncomingPatientsCounter(), BasicCareUnitServer.getPatientsMovedToICUCounter(), BasicCareUnitServer.getPatientsMovedBackFromICUCounter(),
                BasicCareUnitServer.getDiedInQueuePatientsCounter(), BasicCareUnitServer.getDeadPatientsCounter(), BasicCareUnitServer.getDeadPatientsNoFreeBedInICUCounter(),
                IntensiveCareUnitServer.getDeadPatientsCounter(), BasicCareUnitServer.getHealedPatientsCounter(), totalDeadPatients, totalLeavingPatients,
                basicCareRho, intensiveCareRho, totalRho, basicCareAverage, intensiveCareAverage, totalAverage,
                basicCareUnitQueue.getLw(), basicCareUnitQueue.getTw(), basicCareUnitQueue.getTwForAllLinks());
        return results;
    }
}
