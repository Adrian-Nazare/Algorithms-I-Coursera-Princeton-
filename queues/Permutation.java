import edu.princeton.cs.algs4.StdIn;

import java.util.Iterator;

public class Permutation {
    public static void main(String[] args) {
        RandomizedQueue<String> myList = new RandomizedQueue<String>();
        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "Please provide a numeric argument > 0 at the command line");
        }
        int k = Integer.parseInt(args[0]);
        if (k < 0)
            throw new IllegalArgumentException(
                    "Please provide a numeric argument > 0 at the command line");

        // Uses a RandomizedQueue of n elements:
        while (!StdIn.isEmpty()) {
            myList.enqueue(StdIn.readString());
        }
        Iterator<String> iter = myList.iterator();
        while (iter.hasNext() && k > 0) {
            String s = iter.next();
            k--;
            System.out.println(s);
        }
    }
}
