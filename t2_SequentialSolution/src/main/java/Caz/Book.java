package Caz;

import java.util.ArrayList;
import java.util.List;

class Book {
    private int weight;
    private int value;

    public Book(int weight, int value) {
        this.weight = weight;
        this.value = value;
    }

    public int getWeight() {
        return weight;
    }

    public int getValue() {
        return value;
    }

    public static List<Book> generateRandomBooks(int numberOfBooks, int maxWeight, int maxValue) {
        List<Book> books = new ArrayList<>();
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < numberOfBooks; i++) {
            int weight = random.nextInt(maxWeight) + 1;
            int value = random.nextInt(maxValue) + 1;
            books.add(new Book(weight, value));
        }
        return books;
    }
}