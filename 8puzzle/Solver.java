import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;

public class Solver {
    private boolean isSolvable;
    private List<Board> solution = new ArrayList<Board>();
    private int moves = -1;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        // to keep track of the smallest priority in the original board MinPriorityQueue, and the one with the tiles switched, respectively
        Node minOriginal, minInverted;
        if (initial == null) throw new IllegalArgumentException("Null argument provided");
        Board initialInverted = initial.twin();

        // instantiate 2 separate MinPQs, that we will be checking side-by-side
        MinPQ<Node> nodesOriginal = new MinPQ<Node>();
        MinPQ<Node> nodesInverted = new MinPQ<Node>();
        nodesOriginal.insert(new Node(initial, null));
        nodesInverted.insert(new Node(initialInverted, null));

        // the lowest priority nodes
        minOriginal = nodesOriginal.delMin();
        minInverted = nodesInverted.delMin();
        // while neither of the lowest priority nodes are the goal board
        while (!(minOriginal.currentBoard.isGoal() || minInverted.currentBoard.isGoal())) {
            // iterate through the neighbours of the lowest priority node in the original board MinPQ
            for (Board neighbourOriginal : minOriginal.currentBoard.neighbors()) {
                if (minOriginal.prevNode != null) {
                    // if it is not the starting board, we check if the neighbor is the minimum priority node's parent,
                    // if so we don't add it in order to avoid duplicates
                    if (!(neighbourOriginal.equals(minOriginal.prevNode.currentBoard)))
                        nodesOriginal.insert(new Node(neighbourOriginal, minOriginal));
                }
                // if the previous node is null, this means this is the starting board and we add it without more checks
                else
                    nodesOriginal.insert(new Node(neighbourOriginal, minOriginal));
            }
            // after adding the neighbours, we save the lowest priority node once again
            minOriginal = nodesOriginal.delMin();

            // iterate through the neighbours of the lowest priority node in the swapped board MinPQ, same operations as above
            for (Board neighbourInverted : minInverted.currentBoard.neighbors()) {
                if (minInverted.prevNode != null) {
                    if (!(neighbourInverted.equals(minInverted.prevNode.currentBoard)))
                        nodesInverted.insert(new Node(neighbourInverted, minInverted));
                }
                else
                    nodesInverted.insert(new Node(neighbourInverted, minInverted));
            }
            minInverted = nodesInverted.delMin();
        }
        // if we exited the loop, it means we have found a goal board, now we check which one is it
        if (minInverted.currentBoard.isGoal())
            isSolvable = false;
        else { // minOriginal.currentBoard.isGoal() == TRUE
            isSolvable = true;
            moves = minOriginal.numMoves;
            addRecursively(minOriginal);
        }
    }

    // Private method that adds the nodes from the starting board to the goal board
    private void addRecursively(Node node) {
        if (node.prevNode != null) {
            addRecursively(node.prevNode);
        }
        solution.add(node.currentBoard);
    }

    // Node class offers some more data about the state of the board, like its parent, priority and numMoves needed to reach it
    private class Node implements Comparable<Node> {
        private Node prevNode; // or parent node
        private Board currentBoard;
        // priority determines what node sits at the top of the MinPQ
        private int priority;
        // numMoves is used to calculate the priority, together with the manhattan distance
        private int numMoves;


        public Node(Board current, Node parent) {
            currentBoard = current;
            prevNode = parent;
            priority = current.manhattan();
            if (parent == null) {
                numMoves = 0;
            }
            else {
                numMoves = parent.numMoves + 1;
                priority += numMoves;
            }
        }

        // we make the node comparable, and ensure that it is being compared with another node with respect to their priority
        public int compareTo(Node other) {
            return Integer.compare(priority(), other.priority());
        }

        public int priority() {
            return priority;
        }

    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (isSolvable())
            return solution;
        return null;
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
