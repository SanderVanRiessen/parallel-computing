import java.util.List;

class Main {
    public static void main(String[] args) {
        List<Book> books = Book.generateRandomBooks(100000, 100, 100);

        KnapSack knapSack = new KnapSack(10000);
        System.out.println("Starting Sequential Solve...");
        long startTime = System.currentTimeMillis();
        Solution resultSequential = knapSack.solveSequential(books);
        long endTime = System.currentTimeMillis();
        System.out.println("Sequential solve took " + (endTime - startTime) + " ms");
        System.out.println("Maximum Profit (Sequential): " + resultSequential.maxValue);

        System.out.println("Starting Parallel Solve...");
        startTime = System.currentTimeMillis();
        ParallelManager manager = new ParallelManager(knapSack, books);
        Solution maxProfitParallel = manager.execute();
        endTime = System.currentTimeMillis();
        System.out.println("Parallel solve took " + (endTime - startTime) + " ms");
        System.out.println("Maximum Profit (Parallel): " + maxProfitParallel.maxValue);
        maxProfitParallel.printSolution();
    }
}