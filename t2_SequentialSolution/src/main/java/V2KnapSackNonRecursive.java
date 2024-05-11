import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class V2KnapSackNonRecursive {
    static int bestValue;
    static boolean[] bestSelection;
    static Item[] items;

    public static int knapsackDP(Item[] items, int capacity) {
        int[] dp = new int[capacity + 1];
        int[][] itemIndexUsed = new int[capacity + 1][];

        for (int i = 0; i < items.length; i++) {
            for (int w = capacity; w >= items[i].weight; w--) {
                int new_val = dp[w - items[i].weight] + items[i].value;
                if (new_val > dp[w]) {
                    dp[w] = new_val;
                    // Initialize or copy the array for this weight
                    if (itemIndexUsed[w - items[i].weight] != null) {
                        itemIndexUsed[w] = Arrays.copyOf(itemIndexUsed[w - items[i].weight], items.length);
                    } else {
                        itemIndexUsed[w] = new int[items.length];
                    }
                    itemIndexUsed[w][i]++;
                }
            }
        }

        bestValue = dp[capacity];
        bestSelection = new boolean[items.length];
        if (itemIndexUsed[capacity] != null) {
            for (int i = 0; i < items.length; i++) {
                if (itemIndexUsed[capacity][i] > 0) {
                    bestSelection[i] = true;
                }
            }
        }

        return dp[capacity];
    }

    public static void main(String[] args) {
        items = Item.items; // Directly use the static items from the Item class
        int capacity = 500; // Set the capacity of the knapsack
        List<Double> times = new ArrayList<>();

        for (int run = 0; run < 10; run++) {
            long start = System.nanoTime();
            knapsackDP(items, capacity);
            double elapsedTime = (System.nanoTime() - start) / 1E9;
            times.add(elapsedTime);
            System.out.println("Maximum value using DP: " + bestValue);
            System.out.println("Run " + (run + 1) + ": " + elapsedTime + " sec.");
        }

        System.out.println("Times: " + times);
    }
}
