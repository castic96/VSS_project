package model.enums;

/**
 * Types of input parameters (name is the same as in configuration file).
 */
public enum InputParams {

    NUMBER_OF_BED_BASIC_UNIT,
    NUMBER_OF_BED_INTENSIVE_CARE_UNIT,

    INPUT_LAMBDA,

    BASIC_CARE_UNIT_MU,
    BASIC_CARE_UNIT_SIGMA,
    INTENSIVE_CARE_UNIT_MU,

    P_FROM_BASIC_TO_INTENSIVE,
    P_DEATH_BASIC_CARE_UNIT,
    P_DEATH_INTENSIVE_CARE_UNIT,

    MAX_TIME_IN_QUEUE

}
