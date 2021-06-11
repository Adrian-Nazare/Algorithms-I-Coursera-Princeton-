/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

public class PointSET {
    private SET<Point2D> set;

    public PointSET() { // construct an empty set of points
        set = new SET<Point2D>();
    }

    public boolean isEmpty() { // is the set empty?
        return set.isEmpty();
    }

    public int size() { // number of points in the set
        return set.size();
    }

    public void insert(Point2D p) { // add the point to the set (if it is not already in the set)
        set.add(p);
    }

    public boolean contains(Point2D p) { // does the set contain point p?
        return set.contains(p);
    }

    public void draw() { // draw all points to standard draw
        for (Point2D point : set) {
            point.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("null rectangle provided in range(RectHV rect)");

        SET<Point2D> ptsInside = new SET<Point2D>();
        for (Point2D point : set) {
            if (rect.contains(point)) {
                ptsInside.add(point);
            }
        }
        return ptsInside;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Null point provided for nearest()");
        if (set.isEmpty()) return null;

        Point2D nearest = null;
        double minDistance = Double.POSITIVE_INFINITY;

        double distance;

        for (Point2D point : set) {
            distance = point.distanceTo(p);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = point;
            }
        }
        if (nearest == null)
            throw new RuntimeException(
                    "Likely that a distance lower than Double.POSITIVE_INFINITY was not found, nearest point is null");

        return nearest;
    }

    public static void main(String[] args) {
        PointSET pointSet = new PointSET();
        RectHV rectangle = new RectHV(0.0, 0.0, 0.75, 0.75);

        In in = new In(args[0]);
        while (!(in.isEmpty())) {
            pointSet.insert(new Point2D(in.readDouble(), in.readDouble()));
        }
        rectangle.draw();
        System.out.println(rectangle.toString());

        // pointSet.draw();
        for (Point2D point : pointSet.range(rectangle))
            point.draw();
    }
}
