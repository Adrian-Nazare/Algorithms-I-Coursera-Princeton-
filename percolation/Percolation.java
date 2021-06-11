import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/* System percolates after we encounter a current node whose parent has both the connected-to-top and bottom flags on,
 *  after which we set the percolates boolean flag to be true */
public class Percolation {
    private static final byte OPEN = 0b001, CONNECTED_TO_BOTTOM = 0b010, CONNECTED_TO_TOP = 0b100,
            OPEN_CONNECTED_TO_TOP = 0b101,
            CONNECTED_TO_BOTH = 0b111;

    private byte[] siteState;
    private boolean percolates;
    private final WeightedQuickUnionUF siteTree;
    private final int n;
    private int numOpen; // number of open sites

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int number) {
        if (number < 1)
            throw new IllegalArgumentException(
                    "Invalid n argument provided, must be an integer > 0");
        numOpen = 0;
        n = number;
        int nsq = n * n;
        siteTree = new WeightedQuickUnionUF(
                nsq + 1); // we leave entry 0 unused in order to simplify calculations
        percolates = false;
        siteState = new byte[nsq + 1];
        for (int i = 1; i <= n; i++) {
            siteState[i] |= CONNECTED_TO_TOP;
            siteState[nsq + 1 - i] |= CONNECTED_TO_BOTTOM;
        }

    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        checkException(row, col);
        if (!isOpen(row, col)) {
            siteState[(row - 1) * n + col] |= OPEN;
            numOpen += 1;
            if (row > 1) // if there exists a lower index row above, check for a neighbor above
                updateNodes(row, -1, col, 0);
            if (row < n) // if there exists a larger index row below, check for a neighbor below
                updateNodes(row, 1, col, 0);
            if (col > 1) // if there exists a column to the left, check for a neighbor to the left
                updateNodes(row, 0, col, -1);
            if (col < n) // if there exists a column to the right, check for a neighbor to the right
                updateNodes(row, 0, col, 1);
        }
        if (siteState[siteTree.find((row - 1) * n + col)] == CONNECTED_TO_BOTH)
            percolates = true;
    }

    private void updateNodes(int row, int rowOffset, int col, int colOffset) {
        int parentCurr, parentAdj;
        byte temp;
        if (isOpen(row + rowOffset, col + colOffset)) {
            parentCurr = siteTree.find((row - 1) * n + col);
            parentAdj = siteTree.find((row - 1 + rowOffset) * n + col + colOffset);
            temp = (byte) (siteState[parentCurr] | siteState[parentAdj]);
            siteState[parentCurr] = temp;
            siteState[parentAdj] = temp;
            siteTree.union(parentCurr, parentAdj);
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        checkException(row, col);
        if ((siteState[(row - 1) * n + col] & OPEN) == OPEN)
            return true;
        return false;
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        checkException(row, col);
        if (((siteState[(row - 1) * n + col] & OPEN) == OPEN) // if the current site is open
                && (siteState[siteTree.find((row - 1) * n + col)] & OPEN_CONNECTED_TO_TOP)
                // and its parent is connected to top
                == OPEN_CONNECTED_TO_TOP)
            return true;
        return false;
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return numOpen;
    }

    // does the system percolate?
    public boolean percolates() {
        return percolates;
    }

    private void checkException(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n)
            throw new IllegalArgumentException(
                    String.format("Row or col outside allowable bounds [1,%d]: col = %d, row = %d",
                                  n, row, col));
    }

    // test client (optional)
    public static void main(String[] args) {
        Stopwatch timer;
        int n = Integer.parseInt(args[0]);
        int repeats = Integer.parseInt(args[1]);
        if (n < 0 || n > 46340) {
            System.out
                    .println(String.format(
                            "Provided number outside allowable bounds [0, sqrt(2^31)]: n = %d", n));
            return;
        }
        if (repeats < 1 || repeats > 1000) {
            System.out.println(
                    "please provide between 1 and 1000 repeats as the 2nd command line argument");
            return;
        }
        timer = new Stopwatch();
        do {
            Percolation test = new Percolation(n);
            int nsq = n * n, i = 0;
            while (!(test.percolates() || test.numberOfOpenSites() == nsq || i > 2147483646)) {
                test.open(1, 6);
                test.isFull(1, 6);
                System.out.println(test.isFull(1, 6));

                test.open(StdRandom.uniform(1, n + 1), StdRandom.uniform(1, n + 1));
                i++;
            }
            if (test.percolates())
                System.out.println(String.format(
                        "%dx%d system percolates after %d opened sites, and after %d attempts at opening sites",
                        n, n, test.numberOfOpenSites(), i));
            else System.out.println(String.format(
                    "%dx%d system does NOT percolate after %d opened sites, and after %d attempts at opening sites",
                    n, n, test.numberOfOpenSites(), i));
            for (int j = 1; j <= n; j++) {
                for (int k = 1; k <= n; k++) {
                    if (test.isOpen(j, k))
                        if (test.isFull(j, k))
                            System.out.print('1');
                        else System.out.print('.');
                    else System.out.print(' ');
                }
                System.out.println();
            }
            repeats -= 1;
        } while (repeats > 0);
        System.out.println(timer.elapsedTime());
    }
}
