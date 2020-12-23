package model;

import controller.SimulationWindowController;
import cz.zcu.fav.kiv.jsim.*;
import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Input data (patients) generator.
 */
public class InputGenerator extends JSimProcess {

    /** Counter for incoming patients. */
    private static final AtomicInteger incomingPatientsCounter = new AtomicInteger(0);

    /** queue */
    private final BasicCareUnitQueue queue;

    private SimulationWindowController simulationWindowController;

    /**
     * Creates new generator.
     *
     * @param name generator name
     * @param program program
     * @param queue queue
     * @throws JSimSimulationAlreadyTerminatedException if simulation is already terminated
     * @throws JSimInvalidParametersException parent (simulation) is invalid parameter
     * @throws JSimTooManyProcessesException process cannot be added to simulation
     */
    public InputGenerator(String name, Program program, BasicCareUnitQueue queue) throws JSimSimulationAlreadyTerminatedException, JSimInvalidParametersException, JSimTooManyProcessesException
    {
        super(name, program.getSimulation());
        this.queue = queue;
        this.simulationWindowController = program.getSimulationWindowController();
    }

    /**
     * Generating.
     */
    protected void life()
    {
        JSimLink link;

        try
        {
            while (true)
            {
                // generate patient and put him into queue
                link = new JSimLink(new Patient(myParent.getCurrentTime()));
                link.into(queue);
                Patient patient = (Patient) link.getData();
                message("Added patient to queue, patient: " + patient.getPatientNumber());
                Platform.runLater(() -> simulationWindowController.appendLineTextAreaQueue(patient.toString()));
                incomingPatientsCounter.incrementAndGet();

                // find free bed in basic care for patient
                for (JSimProcess currentServer : queue.getServerList()) {
                    if (currentServer.isIdle()) {
                        currentServer.activate(myParent.getCurrentTime());
                        break;
                    }
                }

                // wait before generating another patient
                hold(JSimSystem.negExp(Constants.INPUT_LAMBDA));
            }
        }
        catch (JSimException e)
        {
            e.printStackTrace();
            e.printComment(System.err);
        }
    }

    public static int getIncomingPatientsCounter() {
        return incomingPatientsCounter.get();
    }
}
