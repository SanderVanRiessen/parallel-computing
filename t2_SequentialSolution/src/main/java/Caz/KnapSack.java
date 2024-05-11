public class KnapSack {
    private int capacity;
    private Book[] books;

    public KnapSack(int capacity) {
        this.capacity = capacity;
        this.books = new Book[];
    }

    public int getTotalValue() {
        int totalValue = 0;
        for (Book book : this.books) {
            totalValue += book.getValue();
        }
        return totalValue;
    }

    public Book[] getBooks() {
        return this.books;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getTotalWeight() {
        int totalWeight = 0;
        for (Book book : this.books) {
            totalWeight += book.getWeight();
        }
        return totalWeight;
    }

    public void setBooks(Book[] books) {
        this.books = books;
    }

    public void printBooks() {
        for (Book book : this.books) {
            System.out.println(book);
        }
    }
}