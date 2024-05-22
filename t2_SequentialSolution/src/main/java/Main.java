import java.util.List;

public class Main {

    public Solution runKnapSackProblem(int numOfBooks, int maxWeight, int maxPrice, int knapSackCapacity) {
        List<Book> books = Book.generateRandomBooks(numOfBooks, maxPrice, maxWeight);
        KnapSack knapSack = new KnapSack(knapSackCapacity);

        System.out.println("Starting Sequential Solve...");
        long startTime = System.currentTimeMillis();
        Solution resultSequential = knapSack.solveSequential(books);
        long endTime = System.currentTimeMillis();
        System.out.println("Sequential solve took " + (endTime - startTime) + " ms");
        System.out.println("Maximum Profit (Sequential): " + resultSequential.maxValue);

        return resultSequential;
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.runKnapSackProblem(100000, 100, 100, 10000);
    }
}
