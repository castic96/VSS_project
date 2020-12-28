package model;

import controller.SimulationWindowController;
import cz.zcu.fav.kiv.jsim.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Input data (patients) generator.
 */
public class InputGenerator extends JSimProcess {

    /** Counter for incoming patients. */
    private static final AtomicInteger incomingPatientsCounter = new AtomicInteger(0);

    private static final List<Double> incomingPatientsTimes = new LinkedList<>();

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
                // generate patient
                Patient patient = new Patient(myParent.getCurrentTime());
                link = new JSimLink(patient);

                // put him into queue
                link.into(queue);
                incomingPatientsTimes.add(myParent.getCurrentTime());
                incomingPatientsCounter.incrementAndGet();
                message("Added patient to queue, patient: " + patient.getPatientNumber());

                if (Constants.IS_STEP_BY_STEP) {
                    simulationWindowController.appendLineTextAreaQueue(patient.toString());
                }

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

    public static void setIncomingPatientsCounter(int value) {
        incomingPatientsCounter.set(value);
    }

    public static List<Double> getIncomingPatientsTimes() {
        return incomingPatientsTimes;
    }

    public static void clearIncomingPatientsTimes() {
        incomingPatientsTimes.clear();
    }
}
