import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private double mean, stddev;

    private final double[] means;
    private final int t;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n < 1 || trials < 1)
            throw new IllegalArgumentException(
                    "Invalid arguments provided, length of grid and number of trials must be positive integers");
        means = new double[trials];
        t = trials;
        int repeats = trials;
        int nsq = n * n;
        // Stopwatch timer = new Stopwatch();
        do {
            Percolation test = new Percolation(n);
            while (!test.percolates())
                test.open(StdRandom.uniform(1, n + 1), StdRandom.uniform(1, n + 1));
            repeats -= 1;
            means[repeats] = (double) test.numberOfOpenSites() / nsq;
        } while (repeats > 0);

        /* System.out.print(timer.elapsedTime());
        System.out.println(String.format(
                " seconds elapsed by running: %dx%d system simulated %d times",
                n, n, t)); */
        mean = StdStats.mean(means);
        stddev = StdStats.stddev(means);
    }

    // sample mean of percolation threshold
    public double mean() {
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        stddev = 0;
        for (int i = 0; i < t; i++)
            stddev += Math.pow(means[i] - mean, 2);
        stddev = Math.sqrt(stddev / (t - 1));
        return stddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean - ((1.96 * stddev) / Math.sqrt(t));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean + ((1.96 * stddev) / Math.sqrt(t));
    }

    // test client (see below)
    public static void main(String[] args) {
        if (args.length != 2)
            throw new IllegalArgumentException(
                    "Invalid number of arguments provided, please provide two integers for the n*n grid and number of repeats for the test");
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        if (n <= 0 || t <= 0) {
            throw new IllegalArgumentException(
                    String.format("Grid side length n = %d or test repeats t = %d must be positive",
                                  n, t));
        }
        PercolationStats stats = new PercolationStats(n, t);
        System.out.println(String.format("mean                    = %f", stats.mean()));
        System.out.println(String.format("stddev                  = %f", stats.stddev()));
        System.out.println(
                String.format("95%% confidence interval = [%f, %f]", stats.confidenceLo(),
                              stats.confidenceHi()));
    }

}
