import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    public void printBook(){
        System.out.println("Book weight: " + getWeight() + ", value: " + getValue());
    }

    public static List<Book> generateRandomBooks() {
        List<Book> books = new ArrayList<>();

        try {
            String filePath = System.getProperty("user.dir") + "/resources/books_data100k.txt";
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.substring(line.indexOf('(')+ 1, line.indexOf(')')).split(", ");
                int firstArg = Integer.parseInt(parts[0]);
                int secondArg = Integer.parseInt(parts[1]);

                books.add(new Book(firstArg, secondArg));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return books;
    }

}