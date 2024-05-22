import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class Benchmark {

    @Test
    public void testKnapSack1000000Capacity() {
        Main main = new Main();
        Solution result = main.runKnapSackProblem(5000, 100, 50, 10000);
    }

    @Test
    public void testKnapSack10000000Capacity() {
        Main main = new Main();
        Solution result = main.runKnapSackProblem(5000, 100, 50, 100000);
    }

    @Test
    public void testKnapSack100000000Capacity() {
        Main main = new Main();
        Solution result = main.runKnapSackProblem(5000, 100, 50, 1000000);
    }
}
