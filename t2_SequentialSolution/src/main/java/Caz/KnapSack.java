package Caz;

import java.util.ArrayList;
import java.util.List;

class KnapSack {
    int capacity;

    public KnapSack(int capacity) {
        this.capacity = capacity;
    }

    public Solution solveSequential(List<Book> books) {
        int[] dp = new int[capacity + 1];
        List<Book> selectedBooks = new ArrayList<>();

        boolean[][] isPartOfSolution = new boolean[books.size() + 1][capacity + 1];

        for (int i = 1; i <= books.size(); i++) {
            Book book = books.get(i - 1);
            for (int w = capacity; w >= book.getWeight(); w--) {
                if (dp[w] < dp[w - book.getWeight()] + book.getValue()) {
                    dp[w] = dp[w - book.getWeight()] + book.getValue();
                    isPartOfSolution[i][w] = true;
                }
            }
        }

        int w = capacity;
        for (int i = books.size(); i > 0; i--) {
            if (isPartOfSolution[i][w]) {
                Book book = books.get(i - 1);
                selectedBooks.add(book);
                w -= book.getWeight();
            }
        }

        return new Solution(dp[capacity], selectedBooks, dp);
    }
}