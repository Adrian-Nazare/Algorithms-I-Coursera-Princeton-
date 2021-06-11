import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastCollinearPoints {
    // we use a list to add the segments instead of an array, as we don't yet know how many segments we will find
    private List<LineSegment> segmentList;

    public FastCollinearPoints(Point[] points) { // finds all line segments containing 4 or more points
        if (points == null) throw new IllegalArgumentException("Null argument for point array not permitted");

        // we make a copy of the points array, as we need to keep a reference to each point
        // relative to which we are sorting the points array in ascending order of the slopes to it
        Point[] pointsCopy = new Point[points.length];

        // copy the elements into the new array, while simultaneously checking that we don't have null values
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) throw new IllegalArgumentException("Null value for Point in array not permitted");
            pointsCopy[i] = points[i];
        }

        // we sort the pointCopy Array, so that we can easily check if there are duplicatees
        Arrays.sort(pointsCopy);
        for (int i = 0; i < points.length - 1; i++) {
            if (pointsCopy[i].compareTo(pointsCopy[i + 1]) == 0) throw new IllegalArgumentException(
                    String.format("Duplicate point input not allowed: found: %s", pointsCopy[i].toString()));
        }

        segmentList = new ArrayList<>();
        if (points.length < 4) {
            System.out.println(
                    "Warning, less than 4 points provided, 0 segments of 4 collinear points found");
            // if we have fewer than 4 points provided, there can't be any segments found, so we end here
            return;
        }

        for (Point p : points) {
            // for each point p in points, we sort pointsCopy in ascending order of slopes relative to it
            Arrays.sort(pointsCopy, p.slopeOrder());

            // while having pointsCopy[0] be the reference point, we check its slope relative to
            // each group of 3 nearby points in sorted slope order, in order to find 3 identical slopes
            for (int i = 1; i < pointsCopy.length - 2; i++) {
                if ((pointsCopy[0].slopeTo(pointsCopy[i]) == pointsCopy[0].slopeTo(pointsCopy[i + 1])) && (
                        pointsCopy[0].slopeTo(pointsCopy[i + 1]) == pointsCopy[0].slopeTo(pointsCopy[i + 2]))) {
                    // we will keep a reference to the min/max point in order to guarantee tha longest segments
                    Point min = pointsCopy[0];
                    Point max = pointsCopy[0];
                    // if we have a hit, and optionally if there are more equal slopes following, we keep checking (while incrementing i inside the loop),
                    // and keep track if the new points that make the same slope are the new min/max (concerning the y and x coordinate)
                    while ((i < pointsCopy.length - 1) && (pointsCopy[0].slopeTo(pointsCopy[i]) == pointsCopy[0].slopeTo(pointsCopy[i + 1]))) {
                        if (pointsCopy[i].compareTo(max) > 0) max = pointsCopy[i];
                        if (pointsCopy[i].compareTo(min) < 0) min = pointsCopy[i];
                        i++;
                    }
                    // we check again for the last point, since doing that inside the loop would eventually cause an IndexOutOfBoundsException()
                    if (pointsCopy[i].compareTo(max) > 0) max = pointsCopy[i];
                    if (pointsCopy[i].compareTo(min) < 0) min = pointsCopy[i];

                    // we add the new point only if the reference point was the smallest one to begin with,
                    // in order to avoid duplicate segments and only add the longest ones
                    if (pointsCopy[0].compareTo(min) == 0)
                        segmentList.add(new LineSegment(min, max));
                }
            }
        }
    }

    public int numberOfSegments() { // the number of line segments
        return segmentList.size();
    }

    public LineSegment[] segments() { // the line segments
        // we create an array in which we copy to list to be returned by the segments() function
        LineSegment[] segmentArr = new LineSegment[segmentList.size()];
        int index = 0;
        for (LineSegment l : segmentList) {
            segmentArr[index] = l;
            index++;
        }
        return segmentArr;
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
