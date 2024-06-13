import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParallelKnapSackTest {

    private List<Book> books;
    private static final int RUNS = 10;
    private static List<String> results;


    @Before
    public void setUp() {
        books = Book.generateRandomBooks();
        if (results == null) {
            results = new ArrayList<>();
            if (!isFileHeaderPresent("knapsack_benchmark_parallel100kbooks.csv")) {
                results.add("KnapsackSize,threads,AverageTime(ms),MaxProfit");
            }
        }
    }

    private boolean isFileHeaderPresent(String filename) {
        File file = new File(System.getProperty("user.dir") + File.separator + filename);
        return file.exists() && file.length() > 0;
    }


    @Test
    public void testKnapSackSize100() {
        runKnapSackTest(100);
    }

    @Test
    public void testKnapSackSize200() {
        runKnapSackTest(200);
    }

    @Test
    public void testKnapSackSize400() {
        runKnapSackTest(400);
    }

    @Test
    public void testKnapSackSize800() {
        runKnapSackTest(800);
    }

    @Test
    public void testKnapSackSize1600() {
        runKnapSackTest(1600);
    }

    @Test
    public void testKnapSackSize3200() {
        runKnapSackTest(3200);
    }

    @Test
    public void testKnapSackSize6400() {
        runKnapSackTest(6400);
    }

    @Test
    public void testKnapSackSize12800() {
        runKnapSackTest(12800);
    }

    @Test
    public void testKnapSackSize25600() {
        runKnapSackTest(25600);
    }

    @Test
    public void testKnapSackSize51200() {
        runKnapSackTest(51200);
    }

    private void runKnapSackTest(int size) {
        int[] threadCounts = {1, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20};
        for (int threads : threadCounts) {
            long totalTime = 0;
            int maxProfit = 0;
            for (int i = 0; i < RUNS; i++) {
                KnapSack knapSack = new KnapSack(size);
                ParallelManager manager = new ParallelManager(knapSack, books, threads);

                long startTime = System.currentTimeMillis();
                Solution solution = manager.execute();
                long endTime = System.currentTimeMillis();
                totalTime += (endTime - startTime);
                maxProfit = solution.maxValue;

                System.gc();
            }
            long averageTime = totalTime / RUNS;
            results.add(size + "," + threads + "," + averageTime + "," + maxProfit);
            System.out.println("Knapsack size: " + size + ", Threads: " + threads);
            System.out.println("Average Time taken: " + averageTime + " ms, Max Profit: " + maxProfit);
        }
    }

    @After
    public void tearDown() throws IOException {
        String projectRoot = System.getProperty("user.dir");
        try (FileWriter writer = new FileWriter(projectRoot + "/knapsack_benchmark_parallel100kbooks.csv", true)) {
            for (String result : results) {
                writer.write(result + "\n");
            }
        }
    }
}
