import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KnapSackNonRecursiveParallel {
    static int bestValue;
    static boolean[] bestSelection;
    static Item[] items;

    public static int knapsackDP(Item[] items, int capacity) {
        int[] dp = new int[capacity + 1];
        bestSelection = new boolean[items.length];

        // Using a fixed thread pool sized to the number of available processors
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (Item item : items) {
            executor.execute(() -> {
                // Update the dp array in a synchronized manner
                for (int w = capacity; w >= item.weight; w--) {
                    synchronized (dp) {
                        if (dp[w - item.weight] + item.value > dp[w]) {
                            dp[w] = dp[w - item.weight] + item.value;
                        }
                    }
                }
            });
        }

        executor.shutdown(); // No new tasks will be accepted
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); // Wait until all tasks are finished
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Set the interrupt flag
            System.out.println("Thread was interrupted, failed to complete operation");
        }

        // Store the result of the knapsack
        bestValue = dp[capacity];

        // Reconstruct the solution
        int w = capacity;
        for (int i = items.length - 1; i >= 0; i--) {
            if (bestSelection[i] && w >= items[i].weight) {
                bestSelection[i] = true;
                w -= items[i].weight;
            }
        }

        return dp[capacity];
    }

    public static void main(String[] args) {
        Item[] items = Item.items; // Assume Item.items has been initialized somewhere as static
        int capacity = 160;
        long start = System.nanoTime();
        System.out.println("Maximum value using Parallel DP: " + knapsackDP(items, capacity));
        System.out.println(((System.nanoTime() - start) / 1E9) + " sec.");
        System.out.print("Selected items in DP approach: ");
        for (int i = 0; i < bestSelection.length; i++) {
            if (bestSelection[i]) {
                System.out.print((i + 1) + " ");
            }
        }
    }
}
