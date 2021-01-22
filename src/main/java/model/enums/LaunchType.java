package model.enums;

/**
 * Launch types.
 */
public enum LaunchType {
    STEP_BY_STEP("Step by step"),
    RUN_BY_TIME("Run by time");

    private final String value;

    LaunchType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
