package main.java;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ProblemServiceImpl implements ProblemService<Solution> {
    @Override
    public Solution executeProblem(KnapSack knapSack, List<Book> books) throws RemoteException {
        ParallelManager manager = new ParallelManager(knapSack, books);
        System.out.println("start");
        System.out.println(new SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis()));
        Solution maxProfitParallel = knapSack.solveSequential(books);
//        main.java.Solution maxProfitParallel = manager.execute();
        System.out.println(new SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis()));
        System.out.println("end");

        return maxProfitParallel;
    }
}
