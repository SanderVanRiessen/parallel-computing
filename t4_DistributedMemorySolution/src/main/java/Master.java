import common.Timer;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class Master extends UnicastRemoteObject implements MasterInterface<Solution> {

    private List<Book> books;
    private KnapSack knapSack;
    private int start;
    private int tasks;
    private int growthSize;


    private int numTasksGiven;
    private Solution accumulatedResult = new Solution(0, new ArrayList<>(), new int[10001]);

    public Master(List<Book> books, KnapSack knapSack, int tasks) throws RemoteException {
        super();
        this.books = books;
        this.knapSack = knapSack;
        this.start = 0;
        this.tasks = tasks;
        this.growthSize = books.size() / tasks;
    }

    public Solution getAccumulatedResult() {
        return accumulatedResult;
    }

    @Override
    synchronized public Supplier<Solution> getExecution(int workerId) {
        int remainingTasksToGive = this.tasks - this.numTasksGiven;
        if (remainingTasksToGive > 0){
            int end = start + growthSize;
            List<Book> subList = books.subList(start, end);
            Timer.echo(2, "Gave task-%d with %d to %d items to worker-%d\n", this.tasks, start, end, workerId);
            Solution resultSequential = knapSack.solveSequential(subList);

            start = end;
            this.numTasksGiven++;

            return (Supplier<Solution> & Serializable) () -> resultSequential;
        }
        return null;
    }

    @Override
    synchronized public void processResults(int workerId, int taskNr, Solution result) {
        Timer.echo(2, "Worker-%d has submitted result %d of task-%d\n", workerId, result.maxValue, taskNr);
        this.accumulatedResult = combineSolutions(accumulatedResult, result);
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
