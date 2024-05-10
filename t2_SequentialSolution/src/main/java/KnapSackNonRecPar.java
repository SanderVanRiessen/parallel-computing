import java.util.Arrays;

public class KnapSackNonRecPar {
    static int bestValue;
    static boolean[] bestSelection;
    static Item[] items;
    static int[] dp;
    static int[][] itemIndexUsed;

    public static void knapsackDP(Item[] items, int capacity, int numberOfThreads) {
        dp = new int[capacity + 1];
        itemIndexUsed = new int[capacity + 1][];

        int itemsPerThread = items.length / numberOfThreads;
        Thread[] threads = new Thread[numberOfThreads];

        for (int t = 0; t < numberOfThreads; t++) {
            final int start = t * itemsPerThread;
            final int end = (t == numberOfThreads - 1) ? items.length : (t + 1) * itemsPerThread;
            threads[t] = new Thread(() -> {
                for (int i = start; i < end; i++) {
                    for (int w = capacity; w >= items[i].weight; w--) {
                        synchronized (dp) {
                            int new_val = dp[w - items[i].weight] + items[i].value;
                            if (new_val > dp[w]) {
                                dp[w] = new_val;
                                if (itemIndexUsed[w - items[i].weight] != null) {
                                    itemIndexUsed[w] = Arrays.copyOf(itemIndexUsed[w - items[i].weight], items.length);
                                } else {
                                    itemIndexUsed[w] = new int[items.length];
                                }
                                itemIndexUsed[w][i]++;
                            }
                        }
                    }
                }
            });
            threads[t].start();
        }

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
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
    }

    public static void main(String[] args) {
        items = Item.items; // Directly use the static items from the Item class
        int numberOfThreads = 6; // You can set the number of threads here
        int capacity = 6500; // Set the capacity of the knapsack
        long start = System.nanoTime();
        knapsackDP(items, capacity, numberOfThreads);
        System.out.println("Maximum value using DP: " + bestValue);
        System.out.println(((System.nanoTime() - start) / 1E9) + " sec.");

        System.out.println("Selected items in DP approach:");
        for (int i = 0; i < items.length; i++) {
            if (bestSelection[i]) {
                System.out.println("Item " + (i + 1) + ": Weight = " + items[i].weight + ", Value = " + items[i].value);
            }
        }
    }
}
