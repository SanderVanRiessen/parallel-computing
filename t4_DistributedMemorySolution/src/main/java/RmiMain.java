import common.Timer;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

public class RmiMain {
    private static final int SERVICE_PORT = 49991;
    private static final String SERVICE_NAME = "/knapsackProblem";

    public static void main(String[] args) throws IOException, InterruptedException, NotBoundException {
        List<Book> books = Book.generateRandomBooks();
        KnapSack knapSack = new KnapSack(10000);
        String serviceHost = getExternalIPAddress();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--serviceHost")) {
                i++;
                serviceHost = args[i];
            } else if (args[i].equals("--verbosityLevel")) {
                i++;
                Timer.verbosityLevel = Integer.valueOf(args[i]);
            }
            else if (args[i].equals("--workerId")) {
                i++;
                int workerId = Integer.valueOf(args[i]);

                // launch the worker
                workerMain(workerId, serviceHost);
                return;
            }
        }

        Scanner input = new Scanner(System.in);
        Timer.verbosityLevel = 2;
        int numWorkers = 10;
        int numTasks = 10;
        System.out.print("Please provide the number of worker processes: ");
        numWorkers = input.nextInt();
        System.out.print("Please provide the total number of tasks: ");
        numTasks = input.nextInt();

        Master master = new Master(books, knapSack, numTasks);
        registerMaster(master, serviceHost);
        Timer.echo(-1, "\nLaunching %d workers at %s to solve the knapsack problem\n",
                numWorkers, serviceHost);

        Process[] workers = launchWorkersAtLocalHost(numWorkers, serviceHost);

        shutdownWorkers(workers);

        System.out.printf("%d workers have completed %d tasks with a maximum value of %d\n",
                numWorkers,
                master.getCompletedTasks(),
                master.getAccumulatedResult().maxValue);

        UnicastRemoteObject.unexportObject(master, true);
    }

    private static void workerMain(int workerId, String serviceHost) throws RemoteException, NotBoundException {
        Timer.echo(2, "Worker-%d is up and running\n", workerId);
        Registry registry = LocateRegistry.getRegistry(serviceHost, SERVICE_PORT);
        MasterInterface<Solution> masterService = (MasterInterface) registry.lookup("//" + serviceHost + SERVICE_NAME);

        int tasksCompleted = 0;

        Supplier<Solution> solutionSupplier = masterService.getExecution(workerId);

        while (solutionSupplier != null) {
            Solution solution = solutionSupplier.get();
            Timer.echo(2, "Worker-%d has completed task-%d with result %d\n", workerId, tasksCompleted, solution.maxValue);
            masterService.processResults(workerId, tasksCompleted, solution);
            tasksCompleted++;
            solutionSupplier = masterService.getExecution(workerId);
        }
        Timer.echo(-1, "Worker-%d has completed %d tasks\n", workerId, tasksCompleted);
    }

    private static Process[] launchWorkersAtLocalHost(int numWorkers, String serviceHost) throws IOException {
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
        Timer.measure(1,"%d worker processes have been launched\n", numWorkers);
        return workers;
    }

    private static void registerMaster(MasterInterface master, String serviceHost) throws RemoteException {
        Timer.start();

        Registry registry = LocateRegistry.createRegistry(SERVICE_PORT);
        registry.rebind("//" + serviceHost + SERVICE_NAME, master);

        Timer.measure(2, "Creation of registry has completed\n");
    }

    private static String getExternalIPAddress() throws UnknownHostException {
        String ipa = "localhost";
        ipa = InetAddress.getLocalHost().getHostAddress().toString();
        return ipa;
    }

    private static void shutdownWorkers(Process[] workers) throws InterruptedException {
        Timer.echo(1, "Waiting for %d workers to complete\n", workers.length);
        for (int childId = 0; childId < workers.length; childId++) {
            workers[childId].waitFor();
        }
        Timer.measure(-1, "All worker processes have finished\n");
    }
}
