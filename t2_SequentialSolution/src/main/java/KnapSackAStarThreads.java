import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Arrays.asList;

public class KnapSackAStarThreads {

    static int capacity;
    static volatile int bestValue;
    static boolean[] bestSelection;
    static Item[] items;
    static Lock lock = new ReentrantLock();
    static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static int knapsackAStar(Item[] items, int capacity) {
        KnapSackAStarThreads.capacity = capacity;
        bestValue = Integer.MIN_VALUE;
        bestSelection = new boolean[items.length];
        KnapSackAStarThreads.items = items;
        List<Item> remainingItems = new ArrayList<>(asList(items));
        List<Item> currentItems = new ArrayList<>();
        int currentWeight = 0;
        int currentValue = 0;

        AtomicInteger tasksCounter = new AtomicInteger(0);
        astarSearch(currentItems, currentWeight, currentValue, remainingItems, tasksCounter);

        while (tasksCounter.get() > 0) {
            // Wait until all tasks are completed
        }
        threadPool.shutdown();

        return bestValue;
    }

    public static void astarSearch(List<Item> currentItems, int currentWeight, int currentValue, List<Item> remainingItems, AtomicInteger tasksCounter) {
        if (remainingItems.isEmpty() || currentWeight == capacity) {
            synchronized (lock) {
                if (currentValue > bestValue) {
                    bestValue = currentValue;
                    updateBestSelection(currentItems);
                }
            }
            return;
        }

        int maxPossibleValue = currentValue + heuristic(remainingItems);
        if (maxPossibleValue < bestValue) {
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
                tasksCounter.incrementAndGet();
                threadPool.submit(() -> {
                    astarSearch(nextItems, nextWeight, nextValue, nextRemainingItems, tasksCounter);
                    tasksCounter.decrementAndGet();
                });
            }
        }
    }

    public static int heuristic(List<Item> remainingItems) {
        int heuristicValue = 0;
        for (Item item : remainingItems) {
            heuristicValue += item.value;
        }
        return heuristicValue;
    }

    public static void updateBestSelection(List<Item> selectedItems) {
        synchronized (lock) {
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
