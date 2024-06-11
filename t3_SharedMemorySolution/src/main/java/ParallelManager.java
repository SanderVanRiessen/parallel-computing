import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ParallelManager {
    private KnapSack knapSack;
    private List<Book> books;
    private ForkJoinPool pool;
    private static final int FACTOR = 2;

    public ParallelManager(KnapSack knapSack, List<Book> books) {
        this.knapSack = knapSack;
        this.books = books;
        this.pool = ForkJoinPool.commonPool();
    }

    public ParallelManager(KnapSack knapSack, List<Book> books, int threads) {
        this.knapSack = knapSack;
        this.books = books;
        this.pool = new ForkJoinPool(threads);
    }

    public Solution execute() {
        KnapSackTask task = new KnapSackTask(0, books.size());
        return pool.invoke(task);
    }

    private class KnapSackTask extends RecursiveTask<Solution> {
        private int start;
        private int end;
        private int idealGranularity;

        KnapSackTask(int start, int end) {
            this.start = start;
            this.end = end;
            this.idealGranularity = Math.max(books.size() / (ForkJoinPool.getCommonPoolParallelism() * FACTOR), 10);
        }

        @Override
        protected Solution compute() {
            int length = end - start;
            if (length <= idealGranularity) {
                return knapSack.solveSequential(books.subList(start, end));
            } else {
                int mid = start + length / 2;
                KnapSackTask leftTask = new KnapSackTask(start, mid);
                KnapSackTask rightTask = new KnapSackTask(mid, end);

                leftTask.fork();
                Solution rightSolution = rightTask.compute();
                Solution leftSolution = leftTask.join();

                return combineSolutions(leftSolution, rightSolution);
            }
        }

        private Solution combineSolutions(Solution left, Solution right) {
            int[] dp = new int[knapSack.capacity + 1];
            System.arraycopy(left.dp, 0, dp, 0, left.dp.length);

            for (int i = 0; i <= knapSack.capacity; i++) {
                for (int j = 0; j <= knapSack.capacity - i; j++) {
                    if (i + j <= knapSack.capacity) {
                        dp[i + j] = Math.max(dp[i + j], left.dp[i] + right.dp[j]);
                    }
                }
            }

            List<Book> selectedBooks = new ArrayList<>(left.selectedBooks);
            selectedBooks.addAll(right.selectedBooks);

            int maxValue = 0;
            for (int value : dp) {
                if (value > maxValue) {
                    maxValue = value;
                }
            }

            return new Solution(maxValue, selectedBooks, dp);
        }

    }
}
