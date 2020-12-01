import cz.zcu.fav.kiv.jsim.JSimException;
import cz.zcu.fav.kiv.jsim.JSimSimulation;

public class Main {

    public static void main(String[] args) {
        JSimSimulation simulation = null;

        QueueWithServer hospitalQueue = null;
        HospitalServer hospitalServer = null;
        InputGenerator inputGenerator = null;

        double hospitalMu = 1.0;
        double inputLambda = 0.4;

        double pICU = 0.2;
        double pDeath = 0.1;
        double pICUDeath = 0.3;

        // init
        try {
            System.out.println("Simulation init.");

            simulation = new JSimSimulation("Hospital");

            hospitalQueue = new QueueWithServer("Hospital Queue", simulation, null);
            hospitalServer = new HospitalServer("Hospital Server", simulation, hospitalMu, pICU, pDeath, hospitalQueue);

            inputGenerator = new InputGenerator("Input generator", simulation, inputLambda, hospitalQueue);

            hospitalQueue.setServer(hospitalServer);

            // activate generators
            simulation.message("Activating generators...");

            inputGenerator.activate(0.0);

            simulation.message("Running the simulation, please wait.");

            while ((simulation.getCurrentTime() < 10000.0) && (simulation.step() == true))
                ;


            // Now, some boring numbers.
            simulation.message("Simulation interrupted at time " + simulation.getCurrentTime());
            simulation.message("Queues' statistics:");
            simulation.message("Queue 1: Lw = " + hospitalQueue.getLw() + ", Tw = " + hospitalQueue.getTw() + ", Tw all = " + hospitalQueue.getTwForAllLinks());
            simulation.message("Servers' statistics:");
            simulation.message("example02.Server 1: Counter = " + hospitalServer.getCounter() + ", sum of Tq (for transactions thrown away by this server) = " + hospitalServer.getTransTq());
            //simulation.message("Mean response time = " + ((server1.getTransTq() + server2.getTransTq()) / (server1.getCounter() + server2.getCounter())));
        } catch (JSimException e) {
            e.printStackTrace();
        }



    }


}
