package model;

import cz.zcu.fav.kiv.jsim.JSimSystem;

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

}