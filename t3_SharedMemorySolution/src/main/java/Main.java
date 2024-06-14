import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) {
//        List<Book> books = Book.generateRandomBooks(100000, 100, 100);
        List<Book> books = Book.generateRandomBooks();
        KnapSack knapSack = new KnapSack(12800);

        int threads = 10;

        System.out.println("Starting Parallel Solve");
        long startTime = System.currentTimeMillis();
        ParallelManager manager = new ParallelManager(knapSack, books);
        Solution maxProfitParallel = manager.execute();
        long endTime = System.currentTimeMillis();
        System.out.println("Parallel solve took " + (endTime - startTime) + " ms");
        System.out.println("Maximum Profit (Parallel): " + maxProfitParallel.maxValue);
//        maxProfitParallel.printSolution();
    }
}
