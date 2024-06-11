import common.Timer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class DistributedMemoryTest {

    private List<Book> books;
    private static final int RUNS = 10;
    private static List<String> results;
    private static final int BASE_PORT = 49990;

    @Before
    public void setUp() {
        books = Book.generateRandomBooks();
        if (results == null) {
            results = new ArrayList<>();
            results.add("KnapsackSize,Nodes,AverageTime(ms),MaxProfit");
        }
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
        runDistributedMemoryTest(10000);
    }

    @Test
    public void testKnapSackSize25600() throws Exception {
        runDistributedMemoryTest(25600);
    }

    @Test
    public void testKnapSackSize51200() throws Exception {
        runDistributedMemoryTest(51200);
    }

    private void runDistributedMemoryTest(int size) throws Exception {
        int[] nodeCounts = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        for (int nodes : nodeCounts) {
            long totalTime = 0;
            int maxProfit = 0;
            for (int i = 0; i < RUNS; i++) {
                int port = BASE_PORT + nodes;
                KnapSack knapSack = new KnapSack(size);
                Master master = new Master(books, knapSack, nodes);

                long startTime = System.currentTimeMillis();

                try {
                    // Register the master in the RMI registry
                    String serviceHost = RmiMain.getExternalIPAddress();
                    Registry registry = LocateRegistry.createRegistry(port);
                    registry.rebind("//" + serviceHost + RmiMain.SERVICE_NAME, master);

                    // Launch worker processes locally
                    Process[] workers = launchWorkers(nodes, serviceHost, port);

                    // Wait for workers to complete
                    shutdownWorkers(workers);

                    long endTime = System.currentTimeMillis();
                    totalTime += (endTime - startTime);
                    maxProfit = master.getAccumulatedResult().maxValue;

                    // Cleanup
                    UnicastRemoteObject.unexportObject(master, true);
                    UnicastRemoteObject.unexportObject(registry, true);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    throw new Exception("Failed during RMI operation", e);
                } finally {
                    System.gc();
                }
            }
            long averageTime = totalTime / RUNS;
            results.add(size + "," + nodes + "," + averageTime + "," + maxProfit);
            System.out.println("Knapsack size: " + size + ", Nodes: " + nodes);
            System.out.println("Average Time taken: " + averageTime + " ms, Max Profit: " + maxProfit);
        }
    }

    private Process[] launchWorkers(int numWorkers, String serviceHost, int servicePort) throws IOException {
        Timer.start();

        Process[] workers = new Process[numWorkers];
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classPath = System.getProperty("java.class.path");
        // Launch the worker processes
        for (int childId = 0; childId < numWorkers; childId++) {

            // Restart the current main with child worker command line arguments
            ProcessBuilder child = new ProcessBuilder(
                    javaBin, "-classpath", classPath, RmiMain.class.getCanonicalName(),
                    "--verbosityLevel", String.valueOf(Timer.verbosityLevel),
                    "--serviceHost", serviceHost,
                    "--servicePort", String.valueOf(servicePort),
                    "--workerId", String.valueOf(childId)
            );

            workers[childId] = child.inheritIO().start();
        }
        Timer.measure(1, "%d worker processes have been launched\n", numWorkers);
        return workers;
    }

    private void shutdownWorkers(Process[] workers) throws InterruptedException {
        Timer.echo(1, "Waiting for %d workers to complete\n", workers.length);
        for (int childId = 0; childId < workers.length; childId++) {
            workers[childId].waitFor();
        }
        Timer.measure(-1, "All worker processes have finished\n");
    }

    @AfterClass
    public static void tearDown() throws IOException {
        String projectRoot = System.getProperty("user.dir");
        try (FileWriter writer = new FileWriter(projectRoot + "/knapsack_benchmark_distributed.csv")) {
            for (String result : results) {
                writer.write(result + "\n");
            }
        }
    }
}
