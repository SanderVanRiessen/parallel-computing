import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadKnapSackAStartest {
    static int capacity;
    static AtomicInteger bestValue = new AtomicInteger(Integer.MIN_VALUE);
    static boolean[] bestSelection;
    static Item[] items;
    static final int NUM_THREADS = 4;  // Adjust based on your system capabilities

    public static int knapsackAStar(Item[] items, int capacity) {
        MultiThreadKnapSackAStartest.capacity = capacity;
        bestSelection = new boolean[items.length];
        MultiThreadKnapSackAStartest.items = items;

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < NUM_THREADS; i++) {
            tasks.add(() -> {
                astarSearch(new ArrayList<>(), 0, 0, new ArrayList<>(Arrays.asList(items)), 0);
                return null;
            });
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);  // wait for all tasks to finish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Threads interrupted");
        }

        return bestValue.get();
    }

    public static void astarSearch(List<Item> currentItems, int currentWeight, int currentValue, List<Item> remainingItems, int depth) {
        if (remainingItems.isEmpty() || currentWeight == capacity) {
            if (currentValue > bestValue.get()) {
                synchronized (MultiThreadKnapSackAStartest.class) {
                    if (currentValue > bestValue.get()) {
                        bestValue.set(currentValue);
                        updateBestSelection(currentItems);
                    }
                }
            }
            return;
        }

        if (depth > 20) {  // This may be adjusted or removed based on problem size
            return;
        }

        int maxPossibleValue = currentValue + heuristic(remainingItems, currentWeight);
        if (maxPossibleValue <= bestValue.get()) {
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
                astarSearch(nextItems, nextWeight, nextValue, nextRemainingItems, depth + 1);
            }
        }
    }

    public static int heuristic(List<Item> remainingItems, int currentWeight) {
        int estimatedValue = 0;
        int remainingCapacity = capacity - currentWeight;
        for (Item item : remainingItems) {
            if (item.weight <= remainingCapacity) {
                estimatedValue += item.value;
                remainingCapacity -= item.weight;
            } else {
                estimatedValue += (int) ((double) item.value / item.weight * remainingCapacity);
                break;
            }
        }
        return estimatedValue;
    }

    public static void updateBestSelection(List<Item> selectedItems) {
        synchronized (MultiThreadKnapSackAStartest.class) {
            Arrays.fill(bestSelection, false);
            for (Item item : selectedItems) {
                for (int i = 0; i < items.length; i++) {
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
