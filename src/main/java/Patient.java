/**
 * Represents patient.
 */
public class Patient {

    /** Time when patient arrived to hospital (to queue). */
    private double timeOfCreation;
    /** Time when patient requested to transfer back to basic care unit. */
    private double timeOfRequestToBasicCare;

    private boolean died = false;
    private boolean inMoveToIntensiveCare = false;
    private int patientNumber;
    public static int patientsCounter = 0;

    /**
     * Creates new patient with arrival time.
     *
     * @param timeOfCreation arrival time
     */
    public Patient(double timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
        patientsCounter++;
        patientNumber = patientsCounter;
    }

    /**
     * Sets time of request for transfer back to basic care unit.
     *
     * @param timeOfRequestToBasicCare time
     */
    public void setTimeOfRequestToBasicCare(double timeOfRequestToBasicCare) {
        this.timeOfRequestToBasicCare = timeOfRequestToBasicCare;
    }

    /**
     * Returns patient's arrival time.
     *
     * @return arrival time
     */
    public double getTimeOfCreation() {
        return timeOfCreation;
    }

    /**
     * Returns time of request for transfer back to basic care unit.
     *
     * @return time
     */
    public double getTimeOfRequestToBasicCare() {
        return timeOfRequestToBasicCare;
    }

    public boolean isDied() {
        return died;
    }

    public void setDied(boolean died) {
        this.died = died;
    }

    public boolean isInMoveToIntensiveCare() {
        return inMoveToIntensiveCare;
    }

    public void setInMoveToIntensiveCare(boolean inMoveToIntensiveCare) {
        this.inMoveToIntensiveCare = inMoveToIntensiveCare;
    }

    public int getPatientNumber() {
        return patientNumber;
    }
}
