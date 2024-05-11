import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class KnapSackNonRecursiveParallell {
    static int bestValue;
    static boolean[] bestSelection;
    static Item[] items;
    static AtomicIntegerArray dp;
    static int[][] itemIndexUsed;
    static final Object lock = new Object();

    public static int knapsackDPParallel(Item[] items, int capacity, int numThreads) {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        dp = new AtomicIntegerArray(capacity + 1);
        itemIndexUsed = new int[capacity + 1][];
        CyclicBarrier barrier = new CyclicBarrier(numThreads, () -> System.out.println("Barrier reached by all threads"));

        for (int i = 0; i < items.length; i++) {
            int item = i;
            for (int w = capacity; w >= items[item].weight; w--) {
                int weight = w;
                executor.execute(() -> {
                    try {
                        int newVal = dp.get(weight - items[item].weight) + items[item].value;
                        if (newVal > dp.get(weight)) {
                            synchronized (lock) {
                                if (newVal > dp.get(weight)) {
                                    dp.set(weight, newVal);
                                    if (itemIndexUsed[weight - items[item].weight] != null) {
                                        itemIndexUsed[weight] = Arrays.copyOf(itemIndexUsed[weight - items[item].weight], items.length);
                                    } else {
                                        itemIndexUsed[weight] = new int[items.length];
                                    }
                                    itemIndexUsed[weight][item]++;
                                }
                            }
                        }
                        System.out.println("Thread " + Thread.currentThread().getId() + " reaching barrier.");
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        System.out.println("Barrier issue in thread " + Thread.currentThread().getId());
                    }
                });
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        bestValue = dp.get(capacity);
        bestSelection = new boolean[items.length];
        for (int i = 0; i < items.length; i++) {
            if (itemIndexUsed[capacity] != null && itemIndexUsed[capacity][i] > 0) {
                bestSelection[i] = true;
            }
        }
        return bestValue;
    }

    public static void main(String[] args) {
        items = Item.items; // Assuming items are predefined in the Item class
        int capacity = 40; // Set the capacity of the knapsack
        int numThreads = 4; // Define the number of threads
        long start = System.nanoTime();
        System.out.println("Maximum value using Parallel DP: " + knapsackDPParallel(items, capacity, numThreads));
        System.out.println(((System.nanoTime() - start) / 1E9) + " sec.");

        System.out.println("Selected items in DP approach:");
        for (int i = 0; i < items.length; i++) {
            if (bestSelection[i]) {
                System.out.println("Item " + (i + 1) + ": Weight = " + items[i].weight + ", Value = " + items[i].value);
            }
        }
    }
}
