import common.Timer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private static int SERVICE_PORT = 49994;
    private static final String SERVICE_NAME = "/knapsackProblem";
    public static void main(String[] args) {
        // First, create the real object which will do the requested function.
        ProblemServiceImpl implementation = new ProblemServiceImpl();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--port")) {
                i++;
                SERVICE_PORT = Integer.parseInt(args[i]);
            }
        }

        try {
            // Export the object.
            String serviceHost = getExternalIPAddress();
            ProblemService<Solution> stub = (ProblemService<Solution>) UnicastRemoteObject.exportObject(implementation, 0);
            Registry registry = LocateRegistry.createRegistry(SERVICE_PORT);
            registry.rebind("//" + serviceHost + SERVICE_NAME, stub);

        } catch (RemoteException | UnknownHostException ex) {
            ex.printStackTrace();
            return;
        }
        System.out.println( "Bound!" );
        System.out.println( "Server will wait forever for messages." );

    }
    private static String getExternalIPAddress() throws UnknownHostException {
        String ipa = "localhost";
        ipa = InetAddress.getLocalHost().getHostAddress().toString();
        return ipa;
    }
}

