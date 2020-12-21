package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Program constants.
 */
public class Constants {

    /** Number of beds in basic care unit. */
    public static int NUMBER_OF_BED_BASIC_UNIT;
    /** Number of beds in intensive care unit. */
    public static int NUMBER_OF_BED_INTENSIVE_CARE_UNIT;

    /** Input parameter - lambda (exponential distribution). */
    public static double INPUT_LAMBDA;

    /** Basic care parameter - mu (gauss distribution parameter). */
    public static double BASIC_CARE_UNIT_MU;
    /** Basic care parameter - sigma (gauss distribution parameter). */
    public static double BASIC_CARE_UNIT_SIGMA;
    /** Intensive care parameter - mu (exponential distribution parameter). */
    public static double INTENSIVE_CARE_UNIT_MU;

    /** Probability of transfer from basic care to intensive care. */
    public static double P_FROM_BASIC_TO_INTENSIVE;
    /** Probability of death in basic care. */
    public static double P_DEATH_BASIC_CARE_UNIT;
    /** Probability of death in intensive care. */
    public static double P_DEATH_INTENSIVE_CARE_UNIT;

    /** Maximum time which can be spent in queue (if exceeded -> death). */
    public static double MAX_TIME_IN_QUEUE;

    /** Properties. */
    private static Properties properties;

    /**
     * Loads all constants from file.
     *
     * @param path Path to properties file.
     */
    public static void init(String path) {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            System.out.println("Config file not found on path: " + path);
            return;
        } catch (IOException e) {
            System.out.println("Error while reading config file.");
            return;
        }

        loadProperties();
    }

    /**
     * Loads constants.
     */
    private static void loadProperties() {
        NUMBER_OF_BED_BASIC_UNIT = parseInteger("NUMBER_OF_BED_BASIC_UNIT");
        NUMBER_OF_BED_INTENSIVE_CARE_UNIT = parseInteger("NUMBER_OF_BED_INTENSIVE_CARE_UNIT");

        INPUT_LAMBDA = parseDouble("INPUT_LAMBDA");

        BASIC_CARE_UNIT_MU = parseDouble("BASIC_CARE_UNIT_MU");
        BASIC_CARE_UNIT_SIGMA = parseDouble("BASIC_CARE_UNIT_SIGMA");
        INTENSIVE_CARE_UNIT_MU = parseDouble("INTENSIVE_CARE_UNIT_MU");

        P_FROM_BASIC_TO_INTENSIVE = parseDouble("P_FROM_BASIC_TO_INTENSIVE");
        P_DEATH_BASIC_CARE_UNIT = parseDouble("P_DEATH_BASIC_CARE_UNIT");
        P_DEATH_INTENSIVE_CARE_UNIT = parseDouble("P_DEATH_INTENSIVE_CARE_UNIT");

        MAX_TIME_IN_QUEUE = parseDouble("MAX_TIME_IN_QUEUE");
    }

    /**
     * Gets property and parses its value to integer.
     * Returns 0 if it couldn't be parsed.
     *
     * @param name property name
     * @return integer value
     */
    private static int parseInteger(String name) {
        String value = properties.getProperty(name);

        int i;

        try {
            i = Integer.parseInt(value);
        } catch (Exception e) {
            System.out.println("Config file: cannot parse value to int!");
            i = 0;
        }

        return i;
    }

    /**
     * Gets property and parses its value to double.
     * Returns 0 if it couldn't be parsed.
     *
     * @param name property name
     * @return double value
     */
    private static double parseDouble(String name) {
        String value = properties.getProperty(name);

        double d;
        try {
            d = Double.parseDouble(value);
        } catch (Exception e) {
            System.out.println("Config file: cannot parse value to double!");
            d = 0;
        }

        return d;
    }

}
