package model;

import model.enums.InputParams;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * model.Program constants.
 */
public class Constants {

    /** Number of care units. */
    public static final int NUMBER_OF_UNITS = 2;

    /** If simulation is in step by step mode. */
    public static boolean IS_STEP_BY_STEP = true;

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
        NUMBER_OF_BED_BASIC_UNIT = parseInteger(InputParams.NUMBER_OF_BED_BASIC_UNIT.name());
        NUMBER_OF_BED_INTENSIVE_CARE_UNIT = parseInteger(InputParams.NUMBER_OF_BED_INTENSIVE_CARE_UNIT.name());

        INPUT_LAMBDA = parseDouble(InputParams.INPUT_LAMBDA.name());

        BASIC_CARE_UNIT_MU = parseDouble(InputParams.BASIC_CARE_UNIT_MU.name());
        BASIC_CARE_UNIT_SIGMA = parseDouble(InputParams.BASIC_CARE_UNIT_SIGMA.name());
        INTENSIVE_CARE_UNIT_MU = parseDouble(InputParams.INTENSIVE_CARE_UNIT_MU.name());

        P_FROM_BASIC_TO_INTENSIVE = parseDouble(InputParams.P_FROM_BASIC_TO_INTENSIVE.name());
        P_DEATH_BASIC_CARE_UNIT = parseDouble(InputParams.P_DEATH_BASIC_CARE_UNIT.name());
        P_DEATH_INTENSIVE_CARE_UNIT = parseDouble(InputParams.P_DEATH_INTENSIVE_CARE_UNIT.name());

        MAX_TIME_IN_QUEUE = parseDouble(InputParams.MAX_TIME_IN_QUEUE.name());
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

        if (value == null) {
            System.out.println("Config file: int value for " + name + " not found!");
            return -1;
        }

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

        if (value == null) {
            System.out.println("Config file: string value for " + name + " not found!");
            return -1;
        }

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
