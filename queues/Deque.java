import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private Node first = null, last = null;
    private int n = 0;

    private class Node {
        private Item item;
        private Node previous;
        private Node next;
    }

    // construct an empty deque
    public Deque() {
        // no creation? CAREFUL
    }

    // is the deque empty?
    public boolean isEmpty() {
        return first == null;
        // CAREFUL
    }

    // return the number of items on the deque
    public int size() {
        return n;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException(
                "Null argument entered for addFirst, please provide an appropriate Item");
        if (n == 0) { // similar code to addLast when N = 0;
            first = new Node();
            first.item = item;
            first.previous = null;
            first.next = null;
            last = first;
        }
        else {
            Node oldfirst = first;
            first = new Node();

            first.item = item;
            first.next = oldfirst;
            first.previous = null;
            oldfirst.previous = first;
        }
        n++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException(
                "Null argument entered for addLast, please provide an appropriate Item");
        if (n == 0) { // similar code to addLast when N = 0;
            first = new Node();
            first.item = item;
            first.previous = null;
            first.next = null;
            last = first;
        }
        else {
            Node oldlast = last;
            last = new Node();

            last.item = item;
            last.next = null;
            last.previous = oldlast;
            oldlast.next = last;
        }
        n++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (n == 0) {
            throw new NoSuchElementException(
                    "Attempted to call removeFirst while Deque is empty, size must be > 0");
        }
        else if (n == 1) {
            Item item = first.item;
            first = null;
            last = null;
            n--;
            return item;
        }
        Item item = first.item;
        first = first.next;
        first.previous = null;
        n--;
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (n == 0) {
            throw new NoSuchElementException(
                    "Attempted to call removeLast while Deque is empty, size must be > 0");
        }
        else if (n == 1) {
            Item item = last.item;
            last = null;
            first = null;
            n--;
            return item;
        }
        Item item = last.item;
        last = last.previous;
        last.next = null;
        n--;
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeListIterator();
    }

    private class DequeListIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException("remove() not supported for this iterator");
        }

        public Item next() {
            if (hasNext()) {
                Item item = current.item;
                current = current.next;
                return item;
            }
            else throw new NoSuchElementException("No more elements in iterator");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<String> myList = new Deque<String>();
        while (!StdIn.isEmpty()) {
            myList.addFirst(StdIn.readString());
        }
        myList.removeFirst();
        myList.removeLast();
        myList.addFirst("car");
        myList.addLast("motorcycle");
        Iterator<String> myIterator = myList.iterator();
        while (myIterator.hasNext()) {
            String s = myIterator.next();
            StdOut.println(s);
        }
    }

}
