public class ItemCalculator {
    public static void main(String[] args) {
        int totalWeight = 0;
        int totalValue = 0;

        // Assuming 'items' is initialized and populated as in your previous KnapSackNonRecursive class
        for (Item item : Item.items) {
            totalWeight += item.weight;
            totalValue += item.value;
        }

        System.out.println("Total Weight of All Items: " + totalWeight);
        System.out.println("Total Value of All Items: " + totalValue);
    }
}
