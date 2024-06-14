import common.Timer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class DistributedBenchmarkTest {

    private List<Book> books;
    private static List<String> results;
    private static int servicePort = 49991;
    static String serviceName = "/knapsackProblem";

    private static final String fileName = "Benchmarking";


    @Before
    public void setUp() {
        books = Book.generateRandomBooks();
        if (results == null) {
            results = new ArrayList<>();
            if (!isFileHeaderPresent("knapsack_benchmark_distributed1k.csv")) {
                results.add("KnapsackSize,Nodes,AverageTime(ms),MaxProfit");
            }
        }
    }

    private boolean isFileHeaderPresent(String filename) {
        File file = new File(System.getProperty("user.dir") + File.separator + filename);
        return file.exists() && file.length() > 0;
    }

    @Test
    public void testKnapSackSize100() throws Exception {
        runDistributedMemoryTest(100);
    }

    @Test
    public void testKnapSackSize200() throws Exception {
        runDistributedMemoryTest(200);
    }

    @Test
    public void testKnapSackSize400() throws Exception {
        runDistributedMemoryTest(400);
    }

    @Test
    public void testKnapSackSize800() throws Exception {
        runDistributedMemoryTest(800);
    }

    @Test
    public void testKnapSackSize1600() throws Exception {
        runDistributedMemoryTest(1600);
    }

    @Test
    public void testKnapSackSize3200() throws Exception {
        runDistributedMemoryTest(3200);
    }

    @Test
    public void testKnapSackSize6400() throws Exception {
        runDistributedMemoryTest(6400);
    }

    @Test
    public void testKnapSackSize12800() throws Exception {
        runDistributedMemoryTest(12800);
    }

    @Test
    public void testKnapSackSize25600() throws Exception {
        runDistributedMemoryTest(25600);
    }
    private static final int NODES = 3;
    @Test
    public void testKnapSackSize51200() throws Exception {
        runDistributedMemoryTest(51200);
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
        String serviceHost = getExternalIPAddress();
        System.out.println(servicePort);
        System.out.println(serviceName);

        Master master = new Master(books, knapSack, NODES);
        registerMaster(master, serviceHost);
        Timer.echo(-1, "\nLaunching %d workers at %s to solve the knapsack problem\n",
                NODES, serviceHost);

        Process[] workers = launchWorkersAtLocalHost(NODES, serviceHost);
        long time = shutdownWorkers(workers);
        System.out.printf("%d workers have completed %d tasks with a maximum value of %d\n",
                NODES,
                master.getCompletedTasks(),
                master.getAccumulatedResult().maxValue);
        results.add(size + "," + NODES + "," + time + "," + master.getAccumulatedResult().maxValue);
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

    static long shutdownWorkers(Process[] workers) throws InterruptedException {
        System.out.println("workers shutdown");
        Timer.echo(1, "Waiting for %d workers to complete\n", workers.length);
        for (int childId = 0; childId < workers.length; childId++) {
            workers[childId].waitFor();
        }
        return Timer.measure(-1, "All worker processes have finished\n");
    }

    @After
    public void tearDown() throws IOException {
        String projectRoot = System.getProperty("user.dir");
        try (FileWriter writer = new FileWriter(projectRoot + "/" + fileName +".csv", true)) {
            for (String result : results) {
                writer.write(result + "\n");
            }
        }
    }
}
