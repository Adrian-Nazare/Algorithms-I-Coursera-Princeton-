import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] s = (Item[]) new Object[1];
    private int n = 0;

    // construct an empty randomized queue
    public RandomizedQueue() {
        // no creation? CAREFUL
    }

    // resize the array if need be
    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < n; i++)
            copy[i] = s[i];
        s = copy;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return (n == 0);
    }

    // return the number of items on the randomized queue
    public int size() {
        return n;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null)
            throw new IllegalArgumentException("No null inputs accepted");
        if (n == s.length)
            resize(2 * n);
        s[n++] = item;
    }

    // remove and return a random item
    /* We return a random element from the array, after which we replace it with the array's last item*/
    public Item dequeue() {
        if (n == 0)
            throw new NoSuchElementException("List is empty, no items to return");

        int randomIndex = StdRandom.uniform(0, n); // temporarily save a random index
        Item item = s[randomIndex]; // temporarily save the item from that random index
        s[randomIndex] = s[--n]; // replace item from randomIndex with last item, then decrement n
        s[n] = null; // remove the last reference to prevent loitering

        if (n > 0 && n == s.length / 4)
            resize(s.length / 2);

        return item; // return the saved random item
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (n == 0)
            throw new NoSuchElementException("List is empty, no items to return");

        return s[StdRandom.uniform(0, n)];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private Item[] items = (Item[]) new Object[n];
        private int itemsLeft = n;

        private RandomizedQueueIterator() {
            for (int i = 0; i < n; i++)
                items[i] = s[i];
        }

        public boolean hasNext() {
            return (itemsLeft > 0);
        }

        public void remove() {
            throw new UnsupportedOperationException("remove() not supported for this iterator");
        }

        public Item next() {
            if (hasNext()) {
                int randomIndex = StdRandom.uniform(0, itemsLeft);
                Item item = items[randomIndex];
                items[randomIndex] = items[--itemsLeft];
                items[itemsLeft] = null;
                return item;
            }
            else throw new NoSuchElementException("No more elements in iterator");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<String> myList = new RandomizedQueue<String>();
        while (!StdIn.isEmpty()) {
            myList.enqueue(StdIn.readString());
        }
        StdOut.print("First dequeue: ");
        StdOut.println(myList.dequeue());
        StdOut.print("Second dequeue: ");
        StdOut.println(myList.dequeue());

        myList.enqueue("car");
        myList.enqueue("motorcycle");

        StdOut.print("Primul sample: ");
        StdOut.println(myList.sample());
        StdOut.print("Al doilea sample: ");
        StdOut.println(myList.sample());

        for (String s : myList) {
            StdOut.println(s);
        }
    }

}
