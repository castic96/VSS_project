public class Patient {

    private double timeOfCreation;
    private double timeOfRequestToBasicCare;

    public Patient(double timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public void setTimeOfRequestToBasicCare(double timeOfRequestToBasicCare) {
        this.timeOfRequestToBasicCare = timeOfRequestToBasicCare;
    }

    public double getTimeOfCreation() {
        return timeOfCreation;
    }

    public double getTimeOfRequestToBasicCare() {
        return timeOfRequestToBasicCare;
    }
}
