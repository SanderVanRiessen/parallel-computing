import java.util.List;

public class Main {
    public static void main(String[] args) {
 //       List<Book> books = Book.generateRandomBooks(100000, 100, 100);
        List<Book> books = Book.generateRandomBooks();
        KnapSack knapSack = new KnapSack(200000);

        System.out.println("Starting Sequential Solve...");
        long startTime = System.currentTimeMillis();
        Solution resultSequential = knapSack.solveSequential(books);
        long endTime = System.currentTimeMillis();
        System.out.println("Sequential solve took " + (endTime - startTime) + " ms");
        System.out.println("Maximum Profit (Sequential): " + resultSequential.maxValue);
//        resultSequential.printSolution();
    }
}
