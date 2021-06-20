/* *****************************************************************************
 Problem Specification: Write a data type to represent a set of points in the unit square (all points have x- and y-coordinates between 0 and 1) using 
 a 2d-tree to support efficient range search (find all of the points contained in a query rectangle) and 
 nearest-neighbor search (find a closest point to a query point). (the 2D version of a k-dimensional tree, where k=2)
 2d-trees have numerous applications, ranging from classifying astronomical objects to computer animation to speeding up neural networks to mining data to image retrieval.
 
 To get started, use the following geometric primitives for points and axis-aligned rectangles in the plane:
 * The immutable data type Point2D (part of algs4.jar) represents points in the plane. 
 * The immutable data type RectHV (part of algs4.jar) represents axis-aligned rectangles.
 
 My API in a nutshell:
 public class KdTree {
   public           KdTree()                               // construct an empty set of points 
   public           boolean isEmpty()                      // is the set empty? 
   public               int size()                         // number of points in the set 
   public              void insert(Point2D p)              // add the point to the set (if it is not already in the set)
   public           boolean contains(Point2D p)            // does the set contain point p? 
   public              void draw()                         // draw all points to standard draw 
   public Iterable<Point2D> range(RectHV rect)             // all points that are inside the rectangle (or on the boundary) 
   public           Point2D nearest(Point2D p)             // a nearest neighbor in the set to point p; null if the set is empty 

   public static void main(String[] args)                  // unit testing of the methods (optional) 
}
 
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;

public class KdTree {
    private KdNode root;
    private int size = 0;

    private SET<Point2D> ptsInside = null; // range of points inside the rectangle
    private Point2D nearest = null; // current nearest point to query point

    private class KdNode {
        private boolean dimension; // true is vertical, false is horizontal
        private Point2D point;
        private KdNode left = null;
        private KdNode right = null;

        public KdNode(boolean dimension, Point2D point) {
            this.dimension = dimension;
            this.point = point;
        }
    }

    public KdTree() { // construct an empty set of points
        root = null;
    }

    public boolean isEmpty() { // is the set empty?
        return root == null;
    }

    public int size() { // number of points in the set
        return size;
    }

    public void insert(Point2D p) { // add the point to the set (if it is not already in the set)
        if (p == null) throw new IllegalArgumentException("Attempted to insert null argument");
        addTo(root, p);
        size++;
    }

    private void addTo(KdNode kdNode, Point2D p) {
        int comparison;
        // if the set is empty, we just add the node to the root
        if (kdNode == null) {
            root = new KdNode(true, p);
        }
        else {
            if (kdNode.dimension) { // if this KdNode is vertical (root.dimension == true)
                comparison = Double.compare(p.x(), kdNode.point.x());
                // if their x coordinate is the same, we use the other coordinate in order to compare which node is "larger"
                if (comparison == 0) comparison = Double.compare(p.y(), kdNode.point.y());
            }
            else { // if this KdNode is horizontal (root.dimension == false)
                comparison = Double.compare(p.y(), kdNode.point.y());
                if (comparison == 0) comparison = Double.compare(p.x(), kdNode.point.x());
            }
            // if comparison = 0 on both coordinates, it means that we have a duplicate point, which we ignore
            if (comparison == 0) {
                size--; // we decrement size by 1 in order to offset the automatic incrementing in insert(Point2D p)
                return;
            }
            if (comparison > 0) {
                // if the right node is null, add the point to it
                if (kdNode.right == null) {
                    kdNode.right = new KdNode(!(kdNode.dimension), p);
                }
                // otherwise instruct that node to add the point to one of its children
                else {
                    addTo(kdNode.right, p);
                }
            }
            else { // comparison < 0
                if (kdNode.left == null) {
                    kdNode.left = new KdNode(!(kdNode.dimension), p);
                }
                else {
                    addTo(kdNode.left, p);
                }
            }
        }
    }

    public boolean contains(Point2D p) { // does the set contain point p?
        if (p == null) throw new IllegalArgumentException("Attempted to search for null query");
        return isItThisNode(root, p);
    }

    // check if this node contains the point that we're searching for, otherwise keep searching
    // down the path where it should be, up until we find it or hit a null pointer (return null in the latter case)
    private boolean isItThisNode(KdNode kdNode, Point2D p) {
        int comparison;

        if (kdNode == null) return false;
        if (kdNode.point.equals(p))
            return true;

        if (kdNode.dimension) { // if this KdNode is vertical (root.dimension == true)
            comparison = Double.compare(p.x(), kdNode.point.x());
            if (comparison == 0) comparison = Double.compare(p.y(), kdNode.point.y());
        }
        else { // if this KdNode is horizontal (root.dimension == false)
            comparison = Double.compare(p.y(), kdNode.point.y());
            if (comparison == 0) comparison = Double.compare(p.x(), kdNode.point.x());
        }

        if (comparison > 0) {
            if (kdNode.right == null) {
                // dead end when searching for where point should be, it is not in the set
                return false;
            }
            else {
                return isItThisNode(kdNode.right, p);
            }
        }
        else { // comparison < 0
            if (kdNode.left == null) {
                // dead end when searching for where point should be, it is not in the set
                return false;
            }
            else {
                return isItThisNode(kdNode.left, p);
            }
        }
    }

    public void draw() { // draw all points to standard draw
        // drawLine(root, 0.0, 1.0, 0.0, 1.0); //used for testing/visualization purposes
        drawNode(root);
    }

    // draw the point contained in this node, and recursively call this method again for its children, if present
    private void drawNode(KdNode kdNode) {
        StdDraw.setPenColor(); // sets color to black
        kdNode.point.draw();
        if (kdNode.left != null) drawNode(kdNode.left);
        if (kdNode.right != null) drawNode(kdNode.right);
    }

    // draw the separation line that the point generates
    // we need to pass in the coordinates of the rectangular space in which the point will draw the lines
    private void drawLine(KdNode kdNode, double x0, double x1, double y0, double y1) {
        if (kdNode.dimension) { // True, or node is vertical
            StdDraw.setPenColor(Color.red);
            StdDraw.line(kdNode.point.x(), y0, kdNode.point.x(), y1);
            if (kdNode.left != null) drawLine(kdNode.left, x0, kdNode.point.x(), y0, y1);
            if (kdNode.right != null) drawLine(kdNode.right, kdNode.point.x(), x1, y0, y1);
        }
        else { // False, or node is horizontal
            StdDraw.setPenColor(Color.blue);
            StdDraw.line(x0, kdNode.point.y(), x1, kdNode.point.y());
            if (kdNode.left != null) drawLine(kdNode.left, x0, x1, y0, kdNode.point.y());
            if (kdNode.right != null) drawLine(kdNode.right, x0, x1, kdNode.point.y(), y1);
        }

    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException(
                "Null pointer provided instead of a query rectangle");
        ptsInside = new SET<Point2D>();
        searchForPoints(root, rect);
        return ptsInside;
    }

    private void searchForPoints(KdNode kdNode, RectHV rect) {
        if (kdNode == null) return;
        // if the point is inside the rectangle, then we need to search both regions for further points
        if (rect.contains(kdNode.point)) {
            ptsInside.add(kdNode.point);
            searchForPoints(kdNode.left, rect);
            searchForPoints(kdNode.right, rect);
        }
        // if not, the query rectangle sits either to the left/right,
        // or above/below the query point, thus cutting on one search path
        else {
            if (kdNode.dimension) { // if dimension = true, this node containing a vertical point,
                // is the rectangle to the right of the node? then start searching in the points to the right
                if (kdNode.point.x() < rect.xmin()) {
                    searchForPoints(kdNode.right, rect);
                }
                // is the rectangle to the left of the node? then start searching in the points to the left
                else if (rect.xmax() < kdNode.point.x()) {
                    searchForPoints(kdNode.left, rect);
                }
                else { // rectangle sits on the line that the vertical point traces, above or below it, without containing it
                    searchForPoints(kdNode.left, rect);
                    searchForPoints(kdNode.right, rect);
                }
            }
            else { // if dimension = false, this node containing a horizontal point,
                // is the rectangle above the node? then start searching in the the points above
                if (kdNode.point.y() < rect.ymin()) {
                    searchForPoints(kdNode.right, rect);
                }
                // is the rectangle below the node? then start searching in the the points below
                else if (rect.ymax() < kdNode.point.y()) {
                    searchForPoints(kdNode.left, rect);
                }
                else { // rectangle sits on the line that the vertical point traces, above or below it, without containing it
                    searchForPoints(kdNode.left, rect);
                    searchForPoints(kdNode.right, rect);
                }
            }
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (root == null) return null;
        nearest = root.point;
        searchForNearest(root, p, new RectHV(0.0, 0.0, 1.0, 1.0));
        return nearest;
    }

    // we search for a nearest neighbor to p in kdNode, whose point is contained in rectangle
    private void searchForNearest(KdNode kdNode, Point2D p, RectHV rectangle) {
        int comparison;
        // 2 rectangles that will store the coordinates of the rectangles that are delimited by kdNode's children,
        // used in order to calculate the distance to them and see if it's worth looking down the path inside them as well
        // (i.e. if the distance from point p to the rectangle is less than the distance to an already established nearest)
        RectHV rectLeftBelow, rectRightAbove;

        if (kdNode.point.equals(p)) {
            // if the point we're looking at actually equals the query point, we return here,
            // as we can no longer find a distance lower than 0
            nearest = kdNode.point;
            return;
        }

        // if the point in the current node is closer to the searched node, we update nearest
        if (p.distanceSquaredTo(kdNode.point) < p.distanceSquaredTo(nearest))
            nearest = kdNode.point;

        if (kdNode.dimension) { // if this KdNode is vertical (root.dimension == true), then we have left/right rectangles
            rectLeftBelow = new RectHV(rectangle.xmin(), rectangle.ymin(),
                                       kdNode.point.x(), rectangle.ymax());
            rectRightAbove = new RectHV(kdNode.point.x(), rectangle.ymin(),
                                        rectangle.xmax(), rectangle.ymax());
        }
        else { // if this KdNode is horizontal (root.dimension == false), then we have below/above rectangles
            rectLeftBelow = new RectHV(rectangle.xmin(), rectangle.ymin(),
                                       rectangle.xmax(), kdNode.point.y());
            rectRightAbove = new RectHV(rectangle.xmin(), kdNode.point.y(),
                                        rectangle.xmax(), rectangle.ymax());
        }
        // we look at which rectangle is nearest to the query point, in order to go down that path first
        comparison = Double.compare(rectLeftBelow.distanceSquaredTo(p),
                                    rectRightAbove.distanceSquaredTo(p));

        if (comparison < 0) { // if distance to left/lower rectangle is lower than to right/upper
            if (kdNode.left != null) {
                if (rectLeftBelow.distanceSquaredTo(p) < p.distanceSquaredTo(nearest)) {
                    searchForNearest(kdNode.left, p, rectLeftBelow);
                }
                // if it's larger than to nearest, the distance to the other
                // rectangle will certainly be even larger, so we return here
                else return;
            }
            if (kdNode.right != null) {
                if (rectRightAbove.distanceSquaredTo(p) < p.distanceSquaredTo(nearest)) {
                    searchForNearest(kdNode.right, p, rectRightAbove);
                }
                // else return; // return statement not needed, if (comparison < 0) statement terminates here anyway
            }
        }
        else { // comparison < 0, the opposite situation holds: distance to right/upper rectangle is lower than to left/lower
            if (kdNode.right != null) {
                if (rectRightAbove.distanceSquaredTo(p) < p.distanceSquaredTo(nearest)) {
                    searchForNearest(kdNode.right, p, rectRightAbove);
                }
                else return;
            }
            if (kdNode.left != null) {
                if (rectLeftBelow.distanceSquaredTo(p) < p.distanceSquaredTo(nearest)) {
                    searchForNearest(kdNode.left, p, rectLeftBelow);
                }
            }

        }
    }
    
    public static void main(String[] args) {
        KdTree kdPointSet = new KdTree();
        RectHV rectangle = new RectHV(0.0, 0.0, 0.75, 0.75);

        In in = new In(args[0]);
        while (!(in.isEmpty())) {
            kdPointSet.insert(new Point2D(in.readDouble(), in.readDouble()));
        }
        rectangle.draw();
        System.out.println(rectangle.toString());

        kdPointSet.draw();
        new Point2D(0.5, 0.806).draw();
        System.out.println(kdPointSet.nearest(new Point2D(0.5, 0.806)).toString());
        /* for (Point2D point : kdPointSet.range(rectangle))
            System.out.println(point.toString());*/
        // System.out.println(kdPointSet.contains(new Point2D(0.5, 0.5)));
    }
}
