import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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
        KnapSackTask task = new KnapSackTask(0, books.size(), books);
        return pool.invoke(task);
    }

    private class KnapSackTask extends RecursiveTask<Solution> {
        private int start;
        private int end;
        private List<Book> books;
        private int idealGranularity;

        KnapSackTask(int start, int end, List<Book> books) {
            this.start = start;
            this.end = end;
            this.books = books;
            this.idealGranularity = Math.max(books.size() / (ForkJoinPool.getCommonPoolParallelism() * FACTOR), 10);
        }

        @Override
        protected Solution compute() {
            Deque<KnapSackTask> taskStack = new ArrayDeque<>();
            Deque<Solution> solutionStack = new ArrayDeque<>();

            taskStack.push(this);

            while (!taskStack.isEmpty()) {
                KnapSackTask currentTask = taskStack.pop();
                int length = currentTask.end - currentTask.start;

                if (length <= currentTask.idealGranularity) {
                    Solution result = knapSack.solveSequential(currentTask.books.subList(currentTask.start, currentTask.end));
                    solutionStack.push(result);
                } else {
                    int mid = currentTask.start + length / 2;
                    KnapSackTask leftTask = new KnapSackTask(currentTask.start, mid, currentTask.books);
                    KnapSackTask rightTask = new KnapSackTask(mid, currentTask.end, currentTask.books);

                    // Push right task first so that left task is processed first (LIFO order)
                    taskStack.push(rightTask);
                    taskStack.push(leftTask);
                }

                // Combine solutions if the stack contains more than one solution
                while (solutionStack.size() > 1) {
                    Solution rightSolution = solutionStack.pop();
                    Solution leftSolution = solutionStack.pop();
                    Solution combinedSolution = combineSolutions(leftSolution, rightSolution);
                    solutionStack.push(combinedSolution);
                }
            }

            return solutionStack.pop();
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
