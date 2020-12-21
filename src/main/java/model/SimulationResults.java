package model;

import java.util.Arrays;

/**
 * Stores simulation results.
 */
public class SimulationResults {

    private static final String DOUBLE_FORMAT = "%.3f";

    /** Total number of patients who came to hospital. */
    private final int incomingPatients;

    /** Total number of patients who were moved from basic care unit to ICU. */
    private final int patientsMovedToICU;
    /** Total number of patients who were moved back from ICU to basic care unit. */
    private final int patientsMovedBackFromICU;

    /** Total number of patients who died in queue. */
    private final int patientsDiedInQueue;

    /** Total number of patients who died in basic care unit. */
    private final int patientsDiedInBasicCare;
    /** Total number of patients who died in basic care unit because there was no free bed in ICU. */
    private final int patientsDiedInBasicCareNoFreeBedInICU;
    /** Total number of patients who died in intensive care unit. */
    private final int patientsDiedInIntensiveCare;

    /** Total number of patients who are still lying in basic care unit. */
    private final int patientsStillInBasicCare;
    /** Total number of patients who are still lying in intensive care unit. */
    private final int patientsStillInIntensiveCare;

    /** Total number of patients who left hospital healthy. */
    private final int healedPatients;
    /** Total number of patients who died in hospital (including deaths in queue). */
    private final int deadPatients;
    /** Total number of patients who left hospital (dead or alive). */
    private final int leavingPatients;

    /** Rho for each bed in basic care unit. */
    private final double[] basicCareRhos;
    /** Rho for each bed in intensive care unit. */
    private final double[] intensiveCareRhos;

    /** Rho (load) in basic care. */
    private final double basicCareRho;
    /** Rho (load) in intensive care. */
    private final double intensiveCareRho;
    /** Rho (load) in whole system. */
    private final double totalRho;

    /** Tq - mean time spent in basic care. */
    private final double basicCareAverage;
    /** Tq - mean time spent in intensive care. */
    private final double intensiveCareAverage;
    /** Tq - mean time spent in whole system. */
    private final double totalAverage;

    /** Lw - mean length of queue. */
    private final double lw;
    /** Tw - mean waiting time in queue. */
    private final double tw;
    /** Tw - mean waiting time in queue for all elements. */
    private final double twForAllLinks;

    /**
     * Creates new simulation results.
     *
     * @param incomingPatients total number of patients who came to hospital
     * @param patientsMovedToICU total number of patients who were moved from basic care unit to ICU
     * @param patientsMovedBackFromICU total number of patients who were moved back from ICU to basic care unit
     * @param patientsDiedInQueue total number of patients who died in queue
     * @param patientsDiedInBasicCare total number of patients who died in basic care unit
     * @param patientsDiedInBasicCareNoFreeBedInICU total number of patients who died in basic care unit because there was no free bed in ICU
     * @param patientsDiedInIntensiveCare total number of patients who died in intensive care unit
     * @param patientsStillInBasicCare total number of patients who are still lying in basic care unit
     * @param patientsStillInIntensiveCare total number of patients who are still lying in intensive care unit
     * @param healedPatients total number of patients who left hospital healthy
     * @param deadPatients total number of patients who died in hospital (including deaths in queue)
     * @param leavingPatients total number of patients who left hospital (dead or alive)
     * @param basicCareRhos rho for each bed in basic care unit
     * @param intensiveCareRhos rho for each bed in basic care unit
     * @param basicCareRho rho (load) in basic care
     * @param intensiveCareRho rho (load) in intensive care
     * @param totalRho rho (load) in whole system
     * @param basicCareAverage Tq - mean time spent in basic care
     * @param intensiveCareAverage Tq - mean time spent in intensive care
     * @param totalAverage Tq - mean time spent in whole system
     * @param lw Lw - mean length of queue
     * @param tw Tw - mean waiting time in queue
     * @param twForAllLinks Tw - mean waiting time in queue for all elements
     */
    public SimulationResults(int incomingPatients, int patientsMovedToICU, int patientsMovedBackFromICU, int patientsDiedInQueue, int patientsDiedInBasicCare, int patientsDiedInBasicCareNoFreeBedInICU, int patientsDiedInIntensiveCare,
                             int patientsStillInBasicCare, int patientsStillInIntensiveCare,
                             int healedPatients, int deadPatients, int leavingPatients,
                             double[] basicCareRhos, double[] intensiveCareRhos,
                             double basicCareRho, double intensiveCareRho, double totalRho, double basicCareAverage, double intensiveCareAverage, double totalAverage, double lw, double tw, double twForAllLinks) {
        this.incomingPatients = incomingPatients;
        this.patientsMovedToICU = patientsMovedToICU;
        this.patientsMovedBackFromICU = patientsMovedBackFromICU;
        this.patientsDiedInQueue = patientsDiedInQueue;
        this.patientsDiedInBasicCare = patientsDiedInBasicCare;
        this.patientsDiedInBasicCareNoFreeBedInICU = patientsDiedInBasicCareNoFreeBedInICU;
        this.patientsDiedInIntensiveCare = patientsDiedInIntensiveCare;
        this.patientsStillInBasicCare = patientsStillInBasicCare;
        this.patientsStillInIntensiveCare = patientsStillInIntensiveCare;
        this.healedPatients = healedPatients;
        this.deadPatients = deadPatients;
        this.leavingPatients = leavingPatients;
        this.basicCareRhos = basicCareRhos;
        this.intensiveCareRhos = intensiveCareRhos;
        this.basicCareRho = basicCareRho;
        this.intensiveCareRho = intensiveCareRho;
        this.totalRho = totalRho;
        this.basicCareAverage = basicCareAverage;
        this.intensiveCareAverage = intensiveCareAverage;
        this.totalAverage = totalAverage;
        this.lw = lw;
        this.tw = tw;
        this.twForAllLinks = twForAllLinks;
    }

    @Override
    public String toString() {
        return "---- RESULTS ----" +
                "\npatients coming to hospital (to queue) = " + incomingPatients +
                "\nhealed patients = " + healedPatients + " (" + getHealedPatientsPercent()  + " %)" +
                "\ndead patients = " + deadPatients + " (" + getDeadPatientsPercent() + " %)" +
                "\npatients leaving hospital (dead or alive) = " + leavingPatients +
                "\npatients still lying in hospital = " + getPatientsInHospital() +

                "\n\npatients moved to ICU = " + patientsMovedToICU + " (" + getPatientsMovedToICUPercent() + " %)" +
                "\npatients moved back from ICU = " + patientsMovedBackFromICU +

                "\n\npatients died in queue = " + patientsDiedInQueue +
                "\npatients died in basic care = " + patientsDiedInBasicCare + " (" + getDeadPatientsInBasicCarePercent() + " %)" +
                "\npatients died in basic care (no free bed in ICU) = " + patientsDiedInBasicCareNoFreeBedInICU + " (" + getDeadPatientsInBasicCareNoFreeBedPercent() + " %)" +
                "\npatients died in intensive care = " + patientsDiedInIntensiveCare + " (" + getDeadPatientsInICUPercent() + " %)" +

                "\n\nrhos (basic care) = " + Arrays.toString(basicCareRhos) +
                "\nrhos (intensive care) = " + Arrays.toString(intensiveCareRhos) +

                "\n\nrho (basic care) = " + basicCareRho +
                "\nrho (intensive care) = " + intensiveCareRho +
                "\nrho (system) = " + totalRho +
                "\nTq (basic care) = " + basicCareAverage +
                "\nTq (intensive care) = " + intensiveCareAverage +
                "\nTq (system) = " + totalAverage +
                "\nQueue Lw = " + lw +
                "\nQueue Tw = " + tw +
                "\nQueue Tw all = " + twForAllLinks
                ;
    }

    private int getPatientsInHospital() {
        return patientsStillInBasicCare + patientsStillInIntensiveCare;
    }

    private String getHealedPatientsPercent() {
        double p = ((double) healedPatients / incomingPatients);
        return String.format(DOUBLE_FORMAT, p);
    }

    private String getDeadPatientsPercent() {
        double p = ((double) deadPatients / incomingPatients);
        return String.format(DOUBLE_FORMAT, p);
    }

    private String getPatientsMovedToICUPercent() {
        double p = ((double) patientsMovedToICU / incomingPatients);
        return String.format(DOUBLE_FORMAT, p);
    }

    private String getDeadPatientsInBasicCarePercent() {
        double p = ((double) patientsDiedInBasicCare / incomingPatients);
        return String.format(DOUBLE_FORMAT, p);
    }

    private String getDeadPatientsInBasicCareNoFreeBedPercent() {
        double p = ((double) patientsDiedInBasicCareNoFreeBedInICU / incomingPatients);
        return String.format(DOUBLE_FORMAT, p);
    }

    private String getDeadPatientsInICUPercent() {
        double p = ((double) patientsDiedInIntensiveCare / patientsMovedToICU);
        return String.format(DOUBLE_FORMAT, p);
    }

    public int getIncomingPatients() {
        return incomingPatients;
    }

    public int getPatientsMovedToICU() {
        return patientsMovedToICU;
    }

    public int getPatientsMovedBackFromICU() {
        return patientsMovedBackFromICU;
    }

    public int getPatientsDiedInQueue() {
        return patientsDiedInQueue;
    }

    public int getPatientsDiedInBasicCare() {
        return patientsDiedInBasicCare;
    }

    public int getPatientsDiedInBasicCareNoFreeBedInICU() {
        return patientsDiedInBasicCareNoFreeBedInICU;
    }

    public int getPatientsDiedInIntensiveCare() {
        return patientsDiedInIntensiveCare;
    }

    public int getPatientsStillInBasicCare() {
        return patientsStillInBasicCare;
    }

    public int getPatientsStillInIntensiveCare() {
        return patientsStillInIntensiveCare;
    }

    public int getHealedPatients() {
        return healedPatients;
    }

    public int getDeadPatients() {
        return deadPatients;
    }

    public int getLeavingPatients() {
        return leavingPatients;
    }

    /**
     * Returns rho (load) in basic care.
     *
     * @return rho (load) in basic care
     */
    public double getBasicCareRho() {
        return basicCareRho;
    }

    /**
     * Returns rho (load) in intensive care.
     *
     * @return rho (load) in intensive care
     */
    public double getIntensiveCareRho() {
        return intensiveCareRho;
    }

    /**
     * Returns rho (load) in whole system.
     *
     * @return rho (load) in whole system
     */
    public double getTotalRho() {
        return totalRho;
    }

    /**
     * Returns Tq - mean time spent in basic care.
     *
     * @return Tq - mean time spent in basic care
     */
    public double getBasicCareAverage() {
        return basicCareAverage;
    }

    /**
     * Returns Tq - mean time spent in intensive care.
     *
     * @return Tq - mean time spent in intensive care
     */
    public double getIntensiveCareAverage() {
        return intensiveCareAverage;
    }

    /**
     * Returns Tq - mean time spent in whole system.
     *
     * @return Tq - mean time spent in whole system
     */
    public double getTotalAverage() {
        return totalAverage;
    }

    /**
     * Returns Lw - mean length of queue.
     *
     * @return Lw - mean length of queue
     */
    public double getLw() {
        return lw;
    }

    /**
     * Returns Tw - mean waiting time in queue.
     *
     * @return Tw - mean waiting time in queue
     */
    public double getTw() {
        return tw;
    }

    /**
     * Returns Tw - mean waiting time in queue for all elements.
     *
     * @return Tw - mean waiting time in queue for all elements
     */
    public double getTwForAllLinks() {
        return twForAllLinks;
    }
}
