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

    private boolean dead = false;
    private boolean inMoveToIntensiveCare = false;
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

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
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

    @Override
    public String toString() {
        return "Patient:" + patientNumber;
    }
}
