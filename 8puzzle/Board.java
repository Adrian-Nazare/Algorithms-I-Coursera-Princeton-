/* Data type that models an n-by-n board with sliding tiles, with the following API:
public class Board {
    public Board(int[][] tiles)  // create a board from an n-by-n array of tiles, where tiles[row][col] = tile at (row, col)
    public String toString() // string representation of this board
    public int dimension() // board dimension n
    public int hamming() // number of tiles out of place
    public int manhattan() // sum of Manhattan distances between tiles and goal
    public boolean isGoal() // is this board the goal board? (Goal: when all tiles are in order, with 0 at the end)
    public boolean equals(Object y) // does this board equal y?
    public Iterable<Board> neighbors() // all neighboring boards
    public Board twin() // a board that is obtained by exchanging any pair of tiles
    public static void main(String[] args) // unit testing (not graded)
    }
*/

import edu.princeton.cs.algs4.In;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Board {
    private final int[][] tiles;
    private final int dimension;
    private int manhattan, hamming;
    private int blankAtRow, blankAtCol;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        int zeroTileFlag = 0; // used to check how many 0 tiles were found
        dimension = tiles.length;
        int dimensionSq = dimension * dimension;
        int maxLen = dimensionSq - 1;

        this.tiles = new int[dimension][dimension];

        // we use 2 uni-dimensional arrays to be used for easier calculation of hamming/manhattan distances
        int[] tiles1dUnsorted = new int[dimension * dimension];
        int[] tiles1dSorted = new int[dimension * dimension];
        int index1D = 0; // a separate "i" used to index into the 1D arrays
        manhattan = 0;
        hamming = 0;

        // verify input
        if (dimension > 1) {
            for (int i = 0; i < dimension; i++) { // for each row
                if (tiles[i].length != dimension) {
                    throw new IllegalArgumentException(
                            "N x N grid must be of equal number of rows and columns");
                }
                for (int j = 0; j < dimension; j++) { // for each column
                    if (tiles[i][j] < 0 || tiles[i][j] > maxLen) {
                        throw new IllegalArgumentException(
                                "Values cannot be less than 0 or larger than the ((dimension squared) - 1)");
                    }
                    // start copying the items into the 1D arrays, except for 0/the blank tile, whose position we save
                    if (tiles[i][j] == 0) {
                        zeroTileFlag++;
                        blankAtRow = i;
                        blankAtCol = j;
                    }
                    // we do not copy the zeros into the sorted array, because it will be put at the
                    // beginning after sorting, and the zero needs to be at the end of the goal board
                    else {
                        tiles1dSorted[index1D] = tiles[i][j];
                    }
                    // the blank tile is however copied into the unsorted array without this check
                    tiles1dUnsorted[index1D] = tiles[i][j];
                    index1D++;

                    // start copying the input into Board after all checks have passed
                    this.tiles[i][j] = tiles[i][j];
                }
            }
        }
        else // if the dimension is NOT > 1
            throw new IllegalArgumentException("Must provide an N x N grid of size at least 1");

        if (zeroTileFlag != 1)
            throw new IllegalArgumentException("Must provide only one 0 tile");

        // sort tiles1dSorted and then move the 0 at the end; this is now the Goal array
        Arrays.sort(tiles1dSorted);
        for (int i = 0; i < dimensionSq - 1; i++)
            tiles1dSorted[i] = tiles1dSorted[i + 1];
        tiles1dSorted[dimensionSq - 1] = 0;

        // calculate hamming & manhattan distances
        int whereValueShouldBe;
        for (int i = 0; i < dimensionSq; i++)
            // not calculated for the 0/blank tile
            if ((tiles1dUnsorted[i] != 0) && (tiles1dUnsorted[i] != tiles1dSorted[i])) {
                hamming++;
                whereValueShouldBe = Arrays.binarySearch(tiles1dSorted, tiles1dUnsorted[i]);
                // no. of rows    separating i from whereValueShouldBe: Positive value of ( (whereValueShouldBe / dimension) - (i / dimension) )
                // no. of columns separating i from whereValueShouldBe: Positive value of ( (whereValueShouldBe % dimension) - (i % dimension) )
                // DO NOT try to simplify this sum into (Positive value of difference / dimension) and (Positive value of difference % dimension), learned that the hard way...
                manhattan += (Math.abs((whereValueShouldBe / dimension) - (i / dimension)) +
                        Math.abs((whereValueShouldBe % dimension) - (i % dimension)));
            }
    }

    // string representation of this board
    public String toString() {
        // keep track of num of digits for each number, so we know how much padding we need
        int numDigits;
        StringBuilder str = new StringBuilder();
        str.append(dimension); // 1st row is always the size n for the array: n x n
        str.append("\n");
        for (int i = 0; i < dimension; i++) { // for each row
            for (int j = 0; j < dimension; j++) { // for each column
                // Math.log10(value) returns negative infinity for value = 0, so we check this case separately
                if (tiles[i][j] == 0)
                    numDigits = 1;
                else numDigits = (int) Math.log10(tiles[i][j]) + 1;
                for (int k = 3 - numDigits; k > 0; k--)
                    str.append(" ");
                str.append(tiles[i][j]);
                str.append(" ");
            }
            str.append("\n");
        }
        return str.toString();
    }

    // board dimension n
    public int dimension() {
        return dimension;
    }

    // number of tiles out of place
    public int hamming() {
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return manhattan;
    }

    // is this board the goal board?
    public boolean isGoal() {
        if ((hamming == 0) || (manhattan == 0)) {
            if (hamming == manhattan)
                return true;
            else
                throw new RuntimeException(String.format(
                        "Hamming distance is %d, Manhattan distance is %d, one cannot be 0 while the other does not equal 0, please check the function code",
                        hamming, manhattan));
        }
        else return false;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        // return false immediately if object is null
        if (y == null)
            return false;
        // return false immediately if object is not of Board type
        if (y.getClass() != this.getClass())
            return false;

        // return false immediately if dimensions differ
        Board that = (Board) y;
        if (this.dimension() != that.dimension())
            return false;

        // return false immediately if one value differs
        for (int i = 0; i < dimension; i++) { // for each row
            for (int j = 0; j < dimension; j++) { // for each column
                if (this.tiles[i][j] != that.tiles[i][j])
                    return false;
            }
        }
        return true;
    }

    // all neighbouring boards
    public Iterable<Board> neighbors() {
        return new NeighboringBoards();
    }

    // creates and holds a list of the board's neighbouring boards
    private class NeighboringBoards implements Iterable<Board> {
        private Board[] neighbours;

        // constructor
        public NeighboringBoards() {
            // at least 2 neighboring boards must exist, up to 4 depending on where the blank tile resides
            int numBoards = 2;
            // if it's not on the 1st/last row
            if ((blankAtRow < dimension - 1) && (blankAtRow > 0))
                numBoards++;
            // if it's not on the 1st/last column
            if ((blankAtCol < dimension - 1) && (blankAtCol > 0))
                numBoards++;
            neighbours = new Board[numBoards];

            int index = 0; // used to index into neighbours[]

            // a copy of the array inside the original Board,
            // which will be modified in order to pass the tiles for the neighboring boards
            int[][] toBePassed = new int[dimension][dimension];
            for (int i = 0; i < dimension; i++)
                for (int j = 0; j < dimension; j++)
                    toBePassed[i][j] = tiles[i][j];

            // if there is a row above it, build a new board with the blank tile switched up
            if (blankAtRow > 0) {
                newBoardWithSwitchBlank(toBePassed, index, -1, 0);
                index++;
            }
            // if there is a row below it, build a new board with the blank tile switched down
            if (blankAtRow < dimension - 1) {
                newBoardWithSwitchBlank(toBePassed, index, 1, 0);
                index++;
            }
            // if there is a col to the left of it, build a new board with the blank tile switched left
            if (blankAtCol > 0) {
                newBoardWithSwitchBlank(toBePassed, index, 0, -1);
                index++;
            }
            // if there is a col to the right of it, build a new board with the blank tile switched right
            if (blankAtCol < dimension - 1)
                newBoardWithSwitchBlank(toBePassed, index, 0, 1);
        }

        // private method that creates a new Board with blank switched up(-1, 0), down(+1, 0), left(0, -1) or right(0, +1)
        private void newBoardWithSwitchBlank(int[][] toBePassed, int index, int rowOffset,
                                             int colOffset) {
            // we switch the tile with the value above/below it, or to the left/right of it, and pass the new 2D array into a new Board
            toBePassed[blankAtRow + rowOffset][blankAtCol + colOffset] = 0;
            // we don't use a temp value, since we can get the replaced value from the original tiles
            // in order to put back in the place of the blank tile that was moved
            toBePassed[blankAtRow][blankAtCol] =
                    tiles[blankAtRow + rowOffset][blankAtCol + colOffset];

            neighbours[index] = new Board(toBePassed);

            // we undo the change we made in toBePassed, so that we can reuse it later without having to recreate all of the 2D array
            toBePassed[blankAtRow + rowOffset][blankAtCol + colOffset] =
                    tiles[blankAtRow + rowOffset][blankAtCol + colOffset];
            toBePassed[blankAtRow][blankAtCol] = 0;
        }

        // since the NeighboringBoards class implements (is) an Iterable<Board>, it must contain a method that returns a Board iterator
        public Iterator<Board> iterator() {
            return new BoardIterator();
        }

        // since BoardIterator implements (is) an Iterator<Board>, it must implement hasNext() and a next() method that returns a Board
        private class BoardIterator implements Iterator<Board> {
            private int index = 0;

            public boolean hasNext() {
                if (index < neighbours.length)
                    return true;
                return false;
            }

            public Board next() {
                if (index >= neighbours.length)
                    throw new NoSuchElementException(String.format(
                            "There are max %d neighbouring boards, attempted to access the one at index %d",
                            neighbours.length, index));
                // we can't increment the index after returning the value, so we do it before and return the value at [(incremented)index - 1]
                index++;
                return neighbours[index - 1];
            }
        }
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        // copy the values into a new array, in order to not manipulate the final tiles[][]
        int[][] toBePassed = new int[dimension][dimension];
        for (int i = 0; i < dimension; i++) { // for each row
            for (int j = 0; j < dimension; j++) { // for each column
                toBePassed[i][j] = tiles[i][j];
            }
        }
        // In my implementation: I chose to switch the first two non-zero tiles encountered
        int[] rowNum = new int[2];
        int[] colNum = new int[2];
        int index = 0;
        for (int i = 0; (i < dimension && index < 2); i++) { // for each row
            for (int j = 0; (j < dimension && index < 2); j++) { // for each column
                if (tiles[i][j] != 0) {
                    rowNum[index] = i;
                    colNum[index] = j;
                    index++;
                }
            }
        }
        toBePassed[rowNum[0]][colNum[0]] = toBePassed[rowNum[1]][colNum[1]];
        toBePassed[rowNum[1]][colNum[1]] = tiles[rowNum[0]][colNum[0]];
        return new Board(toBePassed);
    }

    // unit testing (not graded)
    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles2dArray = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles2dArray[i][j] = in.readInt();

        Board thisBoard = new Board(tiles2dArray);
        Iterable<Board> neighbours = thisBoard.neighbors();

        System.out.println(thisBoard.toString());
        System.out
                .println(String.format("Hamming distance is: %d\nManhattan distance is: %d",
                                       thisBoard.hamming(), thisBoard.manhattan()));
        for (Board b : neighbours)
            System.out.println(b.toString());
    }
}
