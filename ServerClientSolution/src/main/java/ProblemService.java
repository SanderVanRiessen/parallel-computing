package main.java;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.function.Supplier;

public interface ProblemService<R> extends Remote {

    R executeProblem(KnapSack knapSack, List<Book> books) throws RemoteException;
    R processResults() throws RemoteException;
}
