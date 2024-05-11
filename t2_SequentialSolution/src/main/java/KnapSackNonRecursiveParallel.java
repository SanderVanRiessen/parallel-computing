import java.util.Arrays;
import java.util.concurrent.*;

public class KnapSackNonRecursiveParallel {
    static int bestValue;
    static volatile boolean[] bestSelection;
    static Item[] items;
    static int[] dp;

    public static void knapsackDP(int start, int end, int itemIndex) {
        Item item = items[itemIndex];
        for (int w = end - 1; w >= start; w--) {
            if (w >= item.weight) {
                int newValue = dp[w - item.weight] + item.value;
                if (newValue > dp[w]) {
                    dp[w] = newValue;
                }
            }
        }
    }

    public static void parallelKnapsack(Item[] items, int capacity, int numThreads) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        dp = new int[capacity + 1];

        for (int i = 0; i < items.length; i++) {
            int finalI = i;
            CountDownLatch latch = new CountDownLatch(numThreads);
            for (int t = 0; t < numThreads; t++) {
                final int start = t * (capacity + 1) / numThreads;
                final int end = (t + 1) * (capacity + 1) / numThreads;
                executor.submit(() -> {
                    knapsackDP(start, end, finalI);
                    latch.countDown();
                });
            }
            latch.await();
        }
        executor.shutdown();
        bestValue = dp[capacity];
    }

    public static void main(String[] args) throws Exception {
        items = Item.items; // Assume items are properly initialized elsewhere
        int capacity = 40;
        int numThreads = 8; // Example, can be adjusted

        long start = System.nanoTime();
        parallelKnapsack(items, capacity, numThreads);
        System.out.println("Maximum value using DP: " + bestValue);
        System.out.println(((System.nanoTime() - start) / 1E9) + " sec.");

        // Backtracking to find the selected items - simplified assumption that backtracking is sequential
        bestSelection = new boolean[items.length];
        for (int w = capacity; w > 0;) {
            for (int i = 0; i < items.length; i++) {
                if (w >= items[i].weight && dp[w] == dp[w - items[i].weight] + items[i].value) {
                    bestSelection[i] = true;
                    w -= items[i].weight;
                    break;
                }
            }
        }

        System.out.println("Selected items in DP approach:");
        for (int i = 0; i < bestSelection.length; i++) {
            if (bestSelection[i]) {
                System.out.println("Item " + (i + 1) + ": Weight = " + items[i].weight + ", Value = " + items[i].value);
            }
        }
    }
}
