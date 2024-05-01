import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;

public class OptimizedKnapSackAStar {
    static int capacity;
    static AtomicInteger bestValue;
    static boolean[] bestSelection;
    static Item[] items;
    static final int NUM_THREADS = Runtime.getRuntime().availableProcessors(); // Utilize available CPU cores

    public static int knapsackAStar(Item[] items, int capacity) {
        OptimizedKnapSackAStar.capacity = capacity;
        bestValue = new AtomicInteger(Integer.MIN_VALUE);
        bestSelection = new boolean[items.length];
        OptimizedKnapSackAStar.items = items;
        List<Item> remainingItems = new ArrayList<>(asList(items));
        List<Item> currentItems = new ArrayList<>();
        int currentWeight = 0;
        int currentValue = 0;

        // Create thread pool
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        // Submit tasks to thread pool
        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(() -> astarSearch(currentItems, currentWeight, currentValue, remainingItems));
        }

        // Shutdown the executor after all tasks are completed
        executor.shutdown();

        // Wait until all tasks are completed
        while (!executor.isTerminated()) {
            // Do nothing
        }

        return bestValue.get();
    }

    public static void astarSearch(List<Item> currentItems, int currentWeight, int currentValue, List<Item> remainingItems) {
        if (remainingItems.isEmpty() || currentWeight == capacity) {
            int currentBestValue = bestValue.get();
            int newValue = currentValue;
            while (newValue > currentBestValue) {
                if (bestValue.compareAndSet(currentBestValue, newValue)) {
                    updateBestSelection(currentItems);
                    break;
                }
                currentBestValue = bestValue.get();
            }
            return;
        }

        int maxPossibleValue = currentValue + heuristic(remainingItems, currentWeight, currentValue, capacity);
        if (maxPossibleValue < bestValue.get()) {
            return;
        }

        for (int i = 0; i < remainingItems.size(); i++) {
            Item item = remainingItems.get(i);
            if (currentWeight + item.weight <= capacity) {
                List<Item> nextItems = new ArrayList<>(currentItems);
                nextItems.add(item);
                int nextWeight = currentWeight + item.weight;
                int nextValue = currentValue + item.value;
                List<Item> nextRemainingItems = new ArrayList<>(remainingItems.subList(i + 1, remainingItems.size()));
                astarSearch(nextItems, nextWeight, nextValue, nextRemainingItems);
            }
        }
    }

    public static int heuristic(List<Item> remainingItems, int currentWeight, int currentValue, int capacity) {
        remainingItems.sort((a, b) -> Double.compare((double) b.value / b.weight, (double) a.value / a.weight));

        int estimatedValue = currentValue;
        int remainingCapacity = capacity - currentWeight;

        for (Item item : remainingItems) {
            if (item.weight <= remainingCapacity) {
                estimatedValue += item.value;
                remainingCapacity -= item.weight;
            } else {
                // Add fraction of value based on remaining capacity
                estimatedValue += (int) ((double) item.value / item.weight * remainingCapacity);
                break;
            }
        }

        return estimatedValue;
    }

    public static void updateBestSelection(List<Item> selectedItems) {
        Arrays.fill(bestSelection, false);
        for (Item item : selectedItems) {
            for (int i = 0; i < bestSelection.length; i++) {
                if (item.equals(items[i])) {
                    bestSelection[i] = true;
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        Item[] items = Item.items;
        int capacity = 40;
        long start = System.nanoTime();
        System.out.println("Maximum value: " + knapsackAStar(items, capacity));
        System.out.println(((System.nanoTime() - start) / 1E9) + " sec.");
        System.out.print("Selected items: ");
        for (int i = 0; i < bestSelection.length; i++) {
            if (bestSelection[i]) {
                System.out.print((i + 1) + " ");
            }
        }
    }
}
