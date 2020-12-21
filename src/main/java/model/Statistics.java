package model;

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
        int patientsStillInBasicCare = 0;
        double[] basicCareRhos = new double[basicCareUnitServerList.size()];
        for (int i = 0; i < basicCareUnitServerList.size(); i++) {
            BasicCareUnitServer basicCareUnitServer = basicCareUnitServerList.get(i);
            basicCareSum += basicCareUnitServer.getTransTq();
            basicCarePatientsSum += basicCareUnitServer.getCounter();

            if (basicCareUnitServer.isOccupied() || !basicCareUnitServer.isIdle()) {
                patientsStillInBasicCare++;
            }

            basicCareRhos[i] = (basicCareUnitServer.getTransTq() / totalTime);
        }

        // intensive care
        double intensiveCareSum = 0.0;
        int intensiveCarePatientsSum = 0;
        int patientsStillInIntensiveCare = 0;
        double[] intensiveCareRhos = new double[intensiveCareUnitServerList.size()];
        for (int i = 0; i < intensiveCareUnitServerList.size(); i++) {
            IntensiveCareUnitServer intensiveCareUnitServer = intensiveCareUnitServerList.get(i);
            intensiveCareSum += intensiveCareUnitServer.getTransTq();
            intensiveCarePatientsSum += intensiveCareUnitServer.getCounter();

            if (intensiveCareUnitServer.isOccupied() || !intensiveCareUnitServer.isIdle()) {
                patientsStillInIntensiveCare++;
            }

            intensiveCareRhos[i] = (intensiveCareUnitServer.getTransTq() / totalTime);
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
                IntensiveCareUnitServer.getDeadPatientsCounter(),
                patientsStillInBasicCare, patientsStillInIntensiveCare,
                BasicCareUnitServer.getHealedPatientsCounter(), totalDeadPatients, totalLeavingPatients,
                basicCareRhos, intensiveCareRhos,
                basicCareRho, intensiveCareRho, totalRho, basicCareAverage, intensiveCareAverage, totalAverage,
                basicCareUnitQueue.getLw(), basicCareUnitQueue.getTw(), basicCareUnitQueue.getTwForAllLinks());
        return results;
    }
}
