import java.util.concurrent.locks.LockSupport;
public class TimerTester
{
    private static final long testDurationSec = 20;
    private static final long singleParkDurationNs = 10 * 1000;

    public static void main(String[] args)
    {
        printHello();

        long testDurationNs = testDurationSec * 1000 * 1000 * 1000;

        long start = System.nanoTime();
        long cnt = 0;

        while (System.nanoTime() - start < testDurationNs)
        {
            LockSupport.parkNanos(singleParkDurationNs);
            cnt++;
        }

        double realTimeMs = (System.nanoTime() - start) / 1000.0 / 1000;
        double expectedTimeMs = cnt * singleParkDurationNs / 1000.0 / 1000;

        printResults(realTimeMs, expectedTimeMs);
    }

    private static void printHello()
    {
        System.out.println("I'll test precision of your system timer and tell you whether it's ok to use Cassandra on this host or not.");
        System.out.println("The test duration is about " + testDurationSec + " seconds");
        System.out.println("Running test...");
    }

    private static void printResults(double realTimeMs, double expectedTimeMs)
    {
        double ratio = realTimeMs / expectedTimeMs;
        System.out.println("Real time: " + realTimeMs + " ms");
        System.out.println("Expected time: " + expectedTimeMs + " ms");
        System.out.println("Ratio: " + ratio);
        if (ratio < 10)
            System.out.println(ANSI_GREEN + "OK: Timer resolution is fine, feel free to use Cassandra on this host" + ANSI_RESET);
        else if (ratio < 50)
            System.out.println(ANSI_YELLOW + "WARN: Timer resolution is a bit suspicious, think twice before using Cassandra on this host" + ANSI_RESET);
        else
            System.out.println(ANSI_RED + "ALARM: Timer resolution is unacceptable, you definitely should patch Cassandra before using it (or just use better host)" + ANSI_RESET);
    }

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
}