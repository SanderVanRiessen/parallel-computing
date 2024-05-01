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
                astarSearch(nextItems, nextWeight, nextValue, nextRemainingItems);
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
                System.out.println(items[i].value);
            }
        }
    }
}
