import java.util.*;
import static java.util.Arrays.asList;

public class KnapsackAStar {
    static int capacity;
    static int bestValue;
    static boolean[] bestSelection;
    static Item[] items;

    public static int knapsackAStar(Item[] items, int capacity) {
        KnapsackAStar.capacity = capacity;
        bestValue = Integer.MIN_VALUE;
        bestSelection = new boolean[items.length];
        KnapsackAStar.items = items;
        List<Item> remainingItems = new ArrayList<>(asList(items));
        List<Item> currentItems = new ArrayList<>();
        int currentWeight = 0;
        int currentValue = 0;

        astarSearch(currentItems, currentWeight, currentValue, remainingItems);

        return bestValue;
    }

    public static void astarSearch(List<Item> currentItems, int currentWeight, int currentValue, List<Item> remainingItems) {
        if (remainingItems.isEmpty() || currentWeight == capacity) {
            if (currentValue > bestValue) {
                bestValue = currentValue;
                updateBestSelection(currentItems);
            }
            return;
        }

        int maxPossibleValue = currentValue + heuristic(remainingItems, currentWeight, currentValue, capacity);
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
