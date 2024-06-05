import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.function.Supplier;

public interface MasterInterface<R> extends Remote {
    Supplier<R> getExecution(int workerId) throws RemoteException;

    void processResults(int workerId, int taskNr, R result) throws RemoteException;

}
