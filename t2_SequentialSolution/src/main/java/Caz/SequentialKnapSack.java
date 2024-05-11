import java.util.*;

public static void Main(String[] args) {
    // Create array of books
    Book[] books = Book.generateRandomBooks(20);
    // Create knapsack
    KnapSack knapSack = new KnapSack(100);

    // Programmatically dynamic solve knapsack problem
    // Create an array of all possible combinations of books

    List<List<Book>> allCombinations = new ArrayList<>();
    for (int i = 0; i < books.length; i++) {
        List<Book> combination = new ArrayList<>();
        combination.add(books[i]);
        allCombinations.add(combination);
        for (int j = i + 1; j < books.length; j++) {
            List<Book> newCombination = new ArrayList<>(combination);
            newCombination.add(books[j]);
            allCombinations.add(newCombination);
        }
    }

    // Find the best combination
    Book[] bestCombination = new Book[];
    int bestValue = 0;
    for (List<Book> combination : allCombinations) {
        int weight = 0;
        int value = 0;
        for (Book book : combination) {
            weight += book.getWeight();
            value += book.getValue();
        }
        if (weight <= knapSack.getCapacity() && value > bestValue) {
            bestValue = value;
            bestCombination = combination;
        }
    }
    knapSack.setBooks(bestCombination);

    // Print the best combination
    System.out.println("Best combination:");
    knapSack.printBooks();
}