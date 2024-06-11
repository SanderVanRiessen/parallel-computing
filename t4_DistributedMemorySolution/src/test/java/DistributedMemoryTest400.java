import common.Timer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class DistributedMemoryTest400 {

    private List<Book> books;
    private static final int RUNS = 10;
    private static List<String> results;
    private static int servicePort = 50050;
    static String serviceName = "/knapsackProblem2";

    @Before
    public void setUp() {
        books = Book.generateRandomBooks();
        if (results == null) {
            results = new ArrayList<>();
            results.add("KnapsackSize,Nodes,AverageTime(ms),MaxProfit");
        }
    }

    @Test
    public void testKnapSackSize400() throws Exception {
        runDistributedMemoryTest(400);
    }

    @After
    public void cleanRegistry() {
        servicePort += 20;
        try {
            Registry registry = LocateRegistry.getRegistry(servicePort);
            registry.unbind(serviceName);
            UnicastRemoteObject.unexportObject(registry, true);
        } catch (Exception e) {
            // Handle any exceptions or cleanup failures
        }
    }

    private void runDistributedMemoryTest(int size) throws Exception {
        KnapSack knapSack = new KnapSack(size);
        String serviceHost = RmiMain.getExternalIPAddress();
        System.out.println(servicePort);
        System.out.println(serviceName);

        int nodes = 10;

        Master master = new Master(books, knapSack, nodes);
        registerMaster(master, serviceHost);
        Timer.echo(-1, "\nLaunching %d workers at %s to solve the knapsack problem\n",
                nodes, serviceHost);

        Process[] workers = launchWorkersAtLocalHost(nodes, serviceHost);
        System.out.println("1");
        shutdownWorkers(workers);
        System.out.println("1");
        System.out.printf("%d workers have completed %d tasks with a maximum value of %d\n",
                nodes,
                master.getCompletedTasks(),
                master.getAccumulatedResult().maxValue);

    }


    static Process[] launchWorkersAtLocalHost(int numWorkers, String serviceHost) throws IOException {
        Timer.start();

        Process[] workers = new Process[numWorkers];
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classPath = System.getProperty("java.class.path");
        // launch the worker processes
        for (int childId = 0; childId < numWorkers; childId++) {

            // restart the current main with child worker command line arguments
            ProcessBuilder child = new ProcessBuilder(
                    javaBin, "-classpath", classPath, RmiMain.class.getCanonicalName(),
                    "--verbosityLevel", String.valueOf(Timer.verbosityLevel),
                    "--serviceHost", serviceHost,
                    "--workerId", String.valueOf(childId)
            );

            workers[childId] = child.inheritIO().start();
        }
        Timer.measure(1, "%d worker processes have been launched\n", numWorkers);
        return workers;
    }

    static void registerMaster(MasterInterface master, String serviceHost) throws RemoteException {
        Timer.start();

        Registry registry = LocateRegistry.createRegistry(servicePort);
        registry.rebind("//" + serviceHost + serviceName, master);

        Timer.measure(2, "Creation of registry has completed\n");
    }

    static String getExternalIPAddress() throws UnknownHostException {
        String ipa = "localhost";
        ipa = InetAddress.getLocalHost().getHostAddress().toString();
        return ipa;
    }

    static void shutdownWorkers(Process[] workers) throws InterruptedException {
        System.out.println("workers shutdown");
        Timer.echo(1, "Waiting for %d workers to complete\n", workers.length);
        for (int childId = 0; childId < workers.length; childId++) {
            workers[childId].waitFor();
        }
        Timer.measure(-1, "All worker processes have finished\n");
    }
}
