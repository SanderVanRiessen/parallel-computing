public class Book {
    private int weight;
    private int value;
    private static final int maxWeight = 500;
    private static final int maxValue = 1000;

    public Book(int weight, int value) {
        this.weight = weight;
        this.value = value;
    }

    private static Book generateRandomBook(int maxWeight, int maxValue) {
        return new Book((int) (Math.random() * maxWeight) + 1, (int) (Math.random() * maxValue) + 1);
    }

    public static Book[] generateRandomBooks(int n) {
        Book[] books = new Book[n];
        for (int i = 0; i < n; i++) {
            books[i] = new Book().generateRandomBook(maxWeight, maxValue);
        }
        return books;
    }

    public int getWeight() {
        return weight;
    }

    public int getValue() {
        return value;
    }
}