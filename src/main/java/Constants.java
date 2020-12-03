import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Constants {

    public static int NUMBER_OF_BED_BASIC_UNIT;
    public static int NUMBER_OF_BED_INTENSIVE_CARE_UNIT;

    public static double INPUT_LAMBDA;

    public static double BASIC_CARE_UNIT_MU;
    public static double BASIC_CARE_UNIT_SIGMA;
    public static double INTENSIVE_CARE_UNIT_MU;

    public static double P_FROM_BASIC_TO_INTENSIVE;
    public static double P_DEATH_BASIC_CARE_UNIT;
    public static double P_DEATH_INTENSIVE_CARE_UNIT;

    // properties
    private static Properties properties;

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
    }

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