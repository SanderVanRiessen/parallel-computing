package main.java;

import java.io.Serializable;
import java.util.List;

class Solution implements Serializable {
    int maxValue;
    List<Book> selectedBooks;
    int[] dp;

    Solution(int maxValue, List<Book> selectedBooks, int[] dp) {
        this.maxValue = maxValue;
        this.selectedBooks = selectedBooks;
        this.dp = dp;
    }

    public void printSolution(){
        for(Book book : selectedBooks){
           book.printBook();
        }
    }
}