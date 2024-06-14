package main.java;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;

public class Server {
    private static int SERVICE_PORT = 49994;
    private static final String SERVICE_NAME = "/knapsackProblem";

    public static void main(String[] args) {
        ProblemServiceImpl implementation = new ProblemServiceImpl();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--port")) {
                i++;
                SERVICE_PORT = Integer.parseInt(args[i]);
            }
        }

        try {
            String serviceHost = getExternalIPAddress();
            System.setProperty("java.rmi.server.hostname", serviceHost);
            ProblemService<Solution> stub = (ProblemService<Solution>) UnicastRemoteObject.exportObject(implementation, 0);
            Registry registry = LocateRegistry.createRegistry(SERVICE_PORT);
            registry.rebind(SERVICE_NAME, stub); // Ensure service name is consistent
            System.out.println("Bound to " + serviceHost + ":" + SERVICE_PORT);
            System.out.println("Server will wait forever for messages.");
            printRegistryInformation(registry);
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return;
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getExternalIPAddress() throws UnknownHostException, SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            // Filters out 127.0.0.1 and inactive interfaces
            if (iface.isLoopback() || !iface.isUp()) {
                continue;
            }

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                // Filters out IPv6 addresses
                if (addr.isLoopbackAddress() || addr.getHostAddress().contains(":")) {
                    continue;
                }
                return addr.getHostAddress();
            }
        }
        throw new UnknownHostException("No non-loopback address found");
    }

    private static void printRegistryInformation(Registry registry) {
        try {
            String[] boundNames = registry.list();
            System.out.println("Currently bound names in the registry:");
            for (String name : boundNames) {
                System.out.println(name);
            }
        } catch (RemoteException e) {
            System.err.println("Failed to retrieve registry information:");
            e.printStackTrace();
        }
    }
}
