import java.rmi.RemoteException;

public class MessengerServiceImpl implements MessengerService {

    @Override
    public int echoMessage(String str) throws RemoteException {
        System.out.println( "Got a message from the client: " + str );

        return str.length();
    }
}
