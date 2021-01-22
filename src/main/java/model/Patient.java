package model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents patient.
 */
public class Patient {

    /** Patients counter. */
    private static final AtomicInteger patientsCounter = new AtomicInteger(0);

    /** Time when patient arrived to hospital (to queue). */
    private final double timeOfCreation;
    /** Time when patient requested to transfer back to basic care unit. */
    private double timeOfRequestToBasicCare;

    /** If patient is dead. */
    private boolean dead = false;
    /** If patient is moving to intensive care unit. */
    private boolean inMoveToIntensiveCare = false;
    /** Patient's number. */
    private final int patientNumber;

    /**
     * Creates new patient with arrival time.
     *
     * @param timeOfCreation arrival time
     */
    public Patient(double timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
        patientNumber = patientsCounter.incrementAndGet();
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

    /**
     * Returns whether patient is dead.
     *
     * @return true if patient is dead; false otherwise
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Sets whether is patient dead.
     *
     * @param dead new death status
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * Returns if patient is moving to intensive care unit.
     *
     * @return if patient is moving to ICU
     */
    public boolean isInMoveToIntensiveCare() {
        return inMoveToIntensiveCare;
    }

    /**
     * Sets whether patient is moving to intensive care unit.
     *
     * @return sets if patient is moving to ICU
     */
    public void setInMoveToIntensiveCare(boolean inMoveToIntensiveCare) {
        this.inMoveToIntensiveCare = inMoveToIntensiveCare;
    }

    /**
     * Returns patient's number.
     *
     * @return number
     */
    public int getPatientNumber() {
        return patientNumber;
    }

    /**
     * Returns string representation of patient.
     *
     * @return patient as string
     */
    @Override
    public String toString() {
        return "Patient: " + patientNumber;
    }

    /**
     * Sets new value to patients counter.
     *
     * @param value new value
     */
    public static void setPatientsCounter(int value) {
        patientsCounter.set(value);
    }
}
