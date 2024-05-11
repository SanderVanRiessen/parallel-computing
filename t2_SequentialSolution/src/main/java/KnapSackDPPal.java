import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class KnapSackDPPal {
    static int[] dp;
    static Item[] items;
    static CyclicBarrier barrier;

    public static void knapsackDP(Item[] items, int capacity, int numberOfThreads) {
        dp = new int[capacity + 1];  // The DP array for storing maximum values at each capacity
        barrier = new CyclicBarrier(numberOfThreads);

        Thread[] threads = new Thread[numberOfThreads];
        for (int t = 0; t < numberOfThreads; t++) {
            final int threadNum = t;
            threads[t] = new Thread(() -> {
                try {
                    int start = capacity * threadNum / numberOfThreads;
                    int end = capacity * (threadNum + 1) / numberOfThreads;
                    for (int i = 0; i < items.length; i++) {
                        int[] localDp = new int[capacity + 1];  // Local copy to store updates for this item
                        System.arraycopy(dp, 0, localDp, 0, dp.length);
                        for (int w = start; w < end; w++) {
                            if (w >= items[i].weight) {
                                int newValue = localDp[w - items[i].weight] + items[i].value;
                                localDp[w] = Math.max(localDp[w], newValue);
                            }
                        }
                        synchronized (dp) {
                            for (int w = start; w < end; w++) {
                                dp[w] = Math.max(dp[w], localDp[w]);  // Update global dp from local copy
                            }
                        }
                        barrier.await();  // Ensure all threads have processed this item before moving to the next
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threads[t].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        items = Item.items;
        int capacity = 500; // Example capacity
        int numberOfThreads = 4; // Number of threads

        knapsackDP(items, capacity, numberOfThreads);
        System.out.println("Maximum value using DP: " + dp[capacity]);
    }
}

