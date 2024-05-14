package Caz;

import java.util.List;

class Solution {
    int maxValue;
    List<Book> selectedBooks;
    int[] dp;

    Solution(int maxValue, List<Book> selectedBooks, int[] dp) {
        this.maxValue = maxValue;
        this.selectedBooks = selectedBooks;
        this.dp = dp;
    }
}