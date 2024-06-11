import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private static final int[] PORTS = new int[]{49990, 49991, 49992, 49993};

    private static final String SERVICE_NAME = "/knapsackProblem";

    public static void main(String[] args) {
        List<Book> books = Book.generateRandomBooks();
        KnapSack knapSack = new KnapSack(10000);
        try {
            String serviceHost = getExternalIPAddress();
            // split up the problem over the servers
            int numServers = PORTS.length;
            int numBooks = books.size();
            int booksPerServer = numBooks / numServers;
            int start = 0;
            int end = booksPerServer;
            List<Solution> solutions = new ArrayList<>();
            for (int i = 0; i < numServers; i++) {
                if (i == numServers - 1) {
                    end = numBooks;
                }
                List<Book> subList = new ArrayList<>(books.subList(start, end));
                start = end;
                end += booksPerServer;
                System.out.println("Connecting to server " + i + " with " + subList.size() + " books");
                Solution subSolution = connectToServer(serviceHost, PORTS[i], subList, knapSack);
                solutions.add(subSolution);
            }

            // combine the solutions
            Solution combineSubSolution = new Solution(0, new ArrayList<>(), new int[]{});
            for (Solution solution : solutions) {
                combineSubSolution.maxValue += solution.maxValue;
                combineSubSolution.selectedBooks.addAll(solution.selectedBooks);
            }
            System.out.println("Combined solution: " + combineSubSolution.maxValue);

            Solution finalSolution = connectToServer(serviceHost, PORTS[1], combineSubSolution.selectedBooks, knapSack);
            System.out.println("final solution: " + finalSolution.maxValue);

            System.out.println( "The return value from the server is: " + finalSolution.maxValue );
        } catch (Exception e) {
            System.err.println( "Exception while trying to echo:" );
            e.printStackTrace();
        }
    }

    private static String getExternalIPAddress() throws UnknownHostException {
        String ipa = "localhost";
        ipa = InetAddress.getLocalHost().getHostAddress().toString();
        return ipa;
    }

    private static Solution connectToServer(String serviceHost, int port, List<Book> books, KnapSack knapSack) {
        try {
            Registry registry = LocateRegistry.getRegistry(serviceHost, port);
            ProblemService<Solution> comp = (ProblemService<Solution>) registry.lookup("//" + serviceHost + SERVICE_NAME);
            System.out.println("Connected to server " + serviceHost + " on port " + port);
            Solution returnVal = comp.executeProblem(knapSack, books);
            System.out.println("The return value from the server is: " + returnVal.maxValue);
            return returnVal;
        } catch (Exception e) {
            System.err.println("Exception while trying to echo:");
            e.printStackTrace();
        }
        return null;
    }
}
