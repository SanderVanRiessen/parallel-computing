package common;

public class Timer {

    public static int verbosityLevel = -1;
    public static void echo(int level, String format, Object ...args) {
        if (level <= verbosityLevel) {
            System.out.printf(format, args);
        }
    }

    private static long startTime = System.currentTimeMillis();

    public static long start(int level, String format, Object ...args) {
        echo(level, format, args);
        startTime = System.currentTimeMillis();
        return startTime;
    }
    public static long start(String format, Object ...args) {
        return start(-1, format, args);
    }
    public static long start() {
        return start(9999, "");
    }

    public static long measure(int level, String format, Object ...args) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        startTime = System.currentTimeMillis();
        echo(level, String.format("[%d msec]: %s", elapsedTime, format), args);
        return elapsedTime;
    }
    public static long measure(String format, Object ...args) {
        return measure(-1, format, args);
    }
    public static long measure() {
        return measure(9999, "");
    }
}
