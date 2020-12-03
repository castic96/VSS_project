import cz.zcu.fav.kiv.jsim.JSimSystem;

public class Utils {

    public static double gaussPositive(double mu, double sigma) {
        double gauss;
        do {
            gauss = JSimSystem.gauss(mu, sigma);
        } while (gauss < 0);

        return gauss;
    }

}
