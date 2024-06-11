package main.java;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessengerService extends Remote {
    public int echoMessage( String str ) throws RemoteException;
}
