
import java.io.File;
import java.io.IOException;

public class Main {
    private static final int[] PORTS = new int[]{49990, 49991, 49992, 49993};

    public static void main(String[] args) throws IOException {
        int numServers = PORTS.length;
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String classPath = System.getProperty("java.class.path");
        for (int i = 0; i < numServers; i++) {
            ProcessBuilder child = new ProcessBuilder(
                    javaBin, "-classpath", classPath, Server.class.getCanonicalName(),
                    "--port", String.valueOf(PORTS[i])
            );
            child.inheritIO().start();
        }
    }
}