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

        for (Book book : books) {
            for (int w = capacity; w >= book.getWeight(); w--) {
                if (dp[w] < dp[w - book.getWeight()] + book.getValue()) {
                    dp[w] = dp[w - book.getWeight()] + book.getValue();
                }
            }
        }
        return new Solution(dp[capacity], selectedBooks, dp);
    }
}