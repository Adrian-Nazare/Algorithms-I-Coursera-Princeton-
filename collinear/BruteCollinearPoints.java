import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteCollinearPoints {
    // we use a list to add the segments instead of an array, as we don't yet know how many segments we will find
    private List<LineSegment> segmentList;

    public BruteCollinearPoints(Point[] points) { // finds all line segments containing 4 points
        if (points == null) throw new IllegalArgumentException("Null argument for point array not permitted");
        Point[] pointsCopy = new Point[points.length];

        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) throw new IllegalArgumentException("Null value for Point in array not permitted");
            pointsCopy[i] = points[i];
        }
        Arrays.sort(pointsCopy);

        for (int i = 0; i < points.length - 1; i++) {
            if (pointsCopy[i].compareTo(pointsCopy[i + 1]) == 0) throw new IllegalArgumentException(
                    String.format("Duplicate point input not allowed: found: %s", pointsCopy[i].toString()));
        }
        segmentList = new ArrayList<>();
        if (points.length < 4) {
            System.out.println(
                    "Warning, less than 4 points provided, 0 segments of 4 collinear points found");
            return;
        }
        // we copy the values into another array so as to not mutate the constructor argument, then sort it
        // in order to guarantee that pointsCopy[i] and pointsCopy[l] are the widest edges of the segment
        Arrays.sort(pointsCopy);

        for (int i = 0; i < pointsCopy.length - 3; i++)
            for (int j = i + 1; j < pointsCopy.length - 2; j++)
                for (int k = j + 1; k < pointsCopy.length - 1; k++)
                    for (int l = k + 1; l < pointsCopy.length; l++) {
                        if ((pointsCopy[i].slopeTo(pointsCopy[j]) == pointsCopy[i].slopeTo(pointsCopy[k])) &&
                                (pointsCopy[i].slopeTo(pointsCopy[k]) == pointsCopy[i].slopeTo(pointsCopy[l]))) { // if we find 4 collinear points
                            Point min = pointsCopy[i];
                            Point max = pointsCopy[l];
                            double referenceSLope = pointsCopy[i].slopeTo(pointsCopy[l]); // we save the slope
                            // we make sure that pointsCopy[i] is the smallest Point, searching all indices until i since the pointsCopy array is sorted
                            for (int iterate = 0; iterate < i; iterate++) {
                                if (pointsCopy[i].slopeTo(pointsCopy[iterate]) == referenceSLope) {
                                    min = pointsCopy[iterate];
                                    iterate = i;
                                }
                            }
                            if (pointsCopy[i].compareTo(min) == 0) {
                                // if it is the smallest, we search for possible greater points on the same segment by iterating l until the end
                                while (l < pointsCopy.length) {
                                    if (pointsCopy[i].slopeTo(pointsCopy[l]) == referenceSLope) {
                                        max = pointsCopy[l];
                                    }
                                    l++;
                                }
                                segmentList.add(new LineSegment(min, max));
                            }
                            // l would have reached the end, we end k here as well since it has the same slope, and we let j go to the next value
                            l = pointsCopy.length;
                            k = pointsCopy.length;
                        }
                    }
    }

    public int numberOfSegments() { // the number of line segments
        return segmentList.size();
    }

    public LineSegment[] segments() { // the line segments
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
