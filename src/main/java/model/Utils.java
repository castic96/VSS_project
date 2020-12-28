package model;

import cz.zcu.fav.kiv.jsim.JSimSystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Util methods.
 */
public class Utils {

    /**
     * Generates number having a Gaussian (normal) distribution.
     * Generated number will always be positive
     * (if negative number is generated, generation will be executed again).
     *
     * @param mu mu
     * @param sigma sigma
     * @return positive number with Gaussian distribution
     */
    public static double gaussPositive(double mu, double sigma) {
        double gauss;
        do {
            gauss = JSimSystem.gauss(mu, sigma);
        } while (gauss < 0);

        return gauss;
    }

    /**
     * Validates if given string represents positive double.
     *
     * @param s string
     * @return positive double or -1 if string couldn't be converted to positive double
     */
    public static double validateDoublePositive(String s) {
        if (s == null || s.isEmpty()) return -1;

        double d;
        try {
            d = Double.parseDouble(s);
        } catch (Exception e) {
            return -1;
        }

        if (d < 0) return -1;

        return d;
    }

    public static void saveNumbers(Program program, File fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));

            List<Double> incomingPatientsTimes = InputGenerator.getIncomingPatientsTimes();
            writer.append(printListDouble(incomingPatientsTimes) + "\n");

            List<Double> deadPatientsTimesBasicCare = BasicCareUnitServer.getDeadPatientsTimes();
            writer.append(printListDouble(deadPatientsTimesBasicCare) + "\n");

            List<Double> deadPatientsNoFreeBedInICUTimes = BasicCareUnitServer.getDeadPatientsNoFreeBedInICUTimes();
            writer.append(printListDouble(deadPatientsNoFreeBedInICUTimes) + "\n");

            List<Double> patientsMovedToICUTimes = BasicCareUnitServer.getPatientsMovedToICUTimes();
            writer.append(printListDouble(patientsMovedToICUTimes) + "\n");

            List<Double> patientsMovedBackFromICUTimes = BasicCareUnitServer.getPatientsMovedBackFromICUTimes();
            writer.append(printListDouble(patientsMovedBackFromICUTimes) + "\n");

            List<Double> healedPatientsTimes = BasicCareUnitServer.getHealedPatientsTimes();
            writer.append(printListDouble(healedPatientsTimes) + "\n");

            List<Double> diedInQueuePatientsTimes = BasicCareUnitServer.getDiedInQueuePatientsTimes();
            writer.append(printListDouble(diedInQueuePatientsTimes) + "\n");

            List<Double> deadPatientsTimesIntensiveCare = IntensiveCareUnitServer.getDeadPatientsTimes();
            writer.append(printListDouble(deadPatientsTimesIntensiveCare) + "\n");

            for (BasicCareUnitServer basicCareUnitServer : program.getBasicCareUnitServerList()) {
                List<Double> inTimes = basicCareUnitServer.getInTimes();
                writer.append(printListDouble(inTimes) + "\n");
                List<Double> outTimes = basicCareUnitServer.getOutTimes();
                writer.append(printListDouble(outTimes) + "\n");
            }

            for (IntensiveCareUnitServer intensiveCareUnitServer : program.getIntensiveCareUnitServerList()) {
                List<Double> inTimes = intensiveCareUnitServer.getInTimes();
                writer.append(printListDouble(inTimes) + "\n");
                List<Double> outTimes = intensiveCareUnitServer.getOutTimes();
                writer.append(printListDouble(outTimes) + "\n");
            }

            writer.close();

        } catch (IOException e) {
            // todo
            System.out.println("error");
        }
    }

    private static String printListDouble(List<Double> list) {
        String s = "";

        for (Double d : list) {
            s += d + ",";
        }

        return s;
    }
}
