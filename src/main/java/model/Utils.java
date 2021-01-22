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

    /**
     * Saves all result numbers to file.
     *
     * @param program program
     * @param fileName filename where to save numbers
     */
    public static void saveResultNumbers(Program program, File fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));

            List<Double> incomingPatientsTimes = InputGenerator.getIncomingPatientsTimes();
            writer.append(printListDouble(incomingPatientsTimes)).append("\n");

            List<Double> deadPatientsTimesBasicCare = BasicCareUnitServer.getDeadPatientsTimes();
            writer.append(printListDouble(deadPatientsTimesBasicCare)).append("\n");

            List<Double> deadPatientsNoFreeBedInICUTimes = BasicCareUnitServer.getDeadPatientsNoFreeBedInICUTimes();
            writer.append(printListDouble(deadPatientsNoFreeBedInICUTimes)).append("\n");

            List<Double> patientsMovedToICUTimes = BasicCareUnitServer.getPatientsMovedToICUTimes();
            writer.append(printListDouble(patientsMovedToICUTimes)).append("\n");

            List<Double> patientsMovedBackFromICUTimes = BasicCareUnitServer.getPatientsMovedBackFromICUTimes();
            writer.append(printListDouble(patientsMovedBackFromICUTimes)).append("\n");

            List<Double> healedPatientsTimes = BasicCareUnitServer.getHealedPatientsTimes();
            writer.append(printListDouble(healedPatientsTimes)).append("\n");

            List<Double> diedInQueuePatientsTimes = BasicCareUnitServer.getDiedInQueuePatientsTimes();
            writer.append(printListDouble(diedInQueuePatientsTimes)).append("\n");

            List<Double> deadPatientsTimesIntensiveCare = IntensiveCareUnitServer.getDeadPatientsTimes();
            writer.append(printListDouble(deadPatientsTimesIntensiveCare)).append("\n");

            for (BasicCareUnitServer basicCareUnitServer : program.getBasicCareUnitServerList()) {
                List<Double> inTimes = basicCareUnitServer.getInTimes();
                writer.append(printListDouble(inTimes)).append("\n");
                List<Double> outTimes = basicCareUnitServer.getOutTimes();
                writer.append(printListDouble(outTimes)).append("\n");
            }

            for (IntensiveCareUnitServer intensiveCareUnitServer : program.getIntensiveCareUnitServerList()) {
                List<Double> inTimes = intensiveCareUnitServer.getInTimes();
                writer.append(printListDouble(inTimes)).append("\n");
                List<Double> outTimes = intensiveCareUnitServer.getOutTimes();
                writer.append(printListDouble(outTimes)).append("\n");
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints list of doubles to string.
     *
     * @param list list of doubles
     * @return string
     */
    private static String printListDouble(List<Double> list) {
        StringBuilder s = new StringBuilder();

        for (Double d : list) {
            s.append(d).append(",");
        }

        return s.toString();
    }
}
