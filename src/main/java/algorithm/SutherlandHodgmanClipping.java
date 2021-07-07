package algorithm;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Implementation of the Sutherland-Hodgman Algorithm for rectangular polygon clipping.
 */
public class SutherlandHodgmanClipping {

    public static void clip(EdgeArc polygon, double x, double y, double width, double height) {
        List<Node> nodes = polygon.getNodesInOrder();
        List<Node> resultList = new ArrayList<>();

        Predicate<Node> insideTop = n -> n.y >= y;
        Predicate<Node> outsideTop = n -> n.y < y;

        Predicate<Node> insideRight = n -> n.x <= x + width;
        Predicate<Node> outsideRight = n -> n.x > x + width;

        Predicate<Node> insideBottom = n -> n.y <= y + height;
        Predicate<Node> outsideBottom = n -> n.y > y + height;

        Predicate<Node> insideLeft = n -> n.x >= x;
        Predicate<Node> outsideLeft = n -> n.x < x;

        clipSide(nodes, resultList, insideTop, outsideTop, ClippingDirection.HORIZONTAL, y);
        nodes = resultList;
        resultList = new ArrayList<>();
        clipSide(nodes, resultList, insideRight, outsideRight, ClippingDirection.VERTICAL, x + width);
        nodes = resultList;
        resultList = new ArrayList<>();
        clipSide(nodes, resultList, insideBottom, outsideBottom, ClippingDirection.HORIZONTAL, y + height);
        nodes = resultList;
        resultList = new ArrayList<>();
        clipSide(nodes, resultList, insideLeft, outsideLeft, ClippingDirection.VERTICAL, x);

        List<Edge> newEdges = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            newEdges.add(new Edge(resultList.get(i), resultList.get((i+1) % resultList.size())));
        }
        polygon.edges = newEdges;
    }

    private enum ClippingDirection {
        HORIZONTAL,
        VERTICAL
    }

    private static void clipSide(List<Node> nodes, List<Node> resultList,
                                 Predicate<Node> inside, Predicate<Node> outside,
                                 ClippingDirection clippingDirection, double clippingEdgePos) {
        for (int i = 0; i < nodes.size(); i++) {
            var from = nodes.get(i);
            var to = nodes.get((i+1) % nodes.size());

            if (inside.test(from) && inside.test(to)) { // both inside
                resultList.add(to);
            } else if (inside.test(from) && outside.test(to)) { // "from" inside, "to" outside
                double intersectionPos = calculateIntersection(clippingDirection, clippingEdgePos, from, to);
                switch (clippingDirection) {
                    case HORIZONTAL:
                        resultList.add(new Node("S(" + from.id + "," + to.id + ")", intersectionPos, clippingEdgePos));
                        break;
                    case VERTICAL:
                        resultList.add(new Node("S(" + from.id + "," + to.id + ")", clippingEdgePos, intersectionPos));
                        break;
                }
            } else if (outside.test(from) && inside.test(to)) { // "from" outside, "to" inside
                double intersectionPos = calculateIntersection(clippingDirection, clippingEdgePos, from, to);
                switch (clippingDirection) {
                    case HORIZONTAL:
                        resultList.add(new Node("S(" + from.id + "," + to.id + ")", intersectionPos, clippingEdgePos));
                        break;
                    case VERTICAL:
                        resultList.add(new Node("S(" + from.id + "," + to.id + ")", clippingEdgePos, intersectionPos));
                        break;
                }
                resultList.add(to);
            } // both outside -> add nothing
        }
    }

    private static double calculateIntersection(ClippingDirection clippingDirection, double fixedCoord, Node from, Node to) {
        if ((to.x - from.x) == 0) { // infinite slope
            if (clippingDirection.equals(ClippingDirection.HORIZONTAL)) {
                return from.x;
            } else {
                return from.y;
            }
        }

        double m = (to.y - from.y) / (to.x - from.x);
        if (m == 0) { // zero slope
            if (clippingDirection.equals(ClippingDirection.HORIZONTAL)) {
                return from.x;
            } else {
                return from.y;
            }
        }

        double b = from.y - m * from.x;

        switch (clippingDirection) {
            case HORIZONTAL:
                double y = fixedCoord;
                return (y - b) / m;
            case VERTICAL:
                double x = fixedCoord;
                return m * x + b;
        }
        throw new IllegalStateException("Non-exhaustive switch statement");
    }

    /*
        TEST AREA
        (it's really difficult to test clipping without some visual feedback)
     */

    public static void test() { // just call this in main
        JFrame window = new JFrame();
        window.setSize(1000, 1000);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JFrame.setDefaultLookAndFeelDecorated(true);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        JPanel canvas = new TestCanvas();
        canvas.setSize(1000, 1000);
        window.add(canvas);
    }

    private static class TestCanvas extends JPanel {

        private final double x = 100;
        private final double y = 100;
        private final double width = 500;
        private final double height = 500;
        private final EdgeArc unclipped;
        private final EdgeArc clipped;

        private final Node[] nodes = {
                new Node("n1", 120, 120),
                new Node("n2", 160, 80),
                new Node("n3", 650, 300),
                new Node("n4", 300, 650),
                new Node("n5", 80, 300),
        };

        public TestCanvas() {
            List<Edge> edges = new ArrayList<>();
            for (int i = 0; i < nodes.length; i++) {
                edges.add(new Edge(nodes[i], nodes[(i+1) % nodes.length]));
            }
            List<Edge> edgesCopy = new ArrayList<>(edges);

            unclipped = new EdgeArc();
            unclipped.edges = edgesCopy;
            clipped = new EdgeArc();
            clipped.edges = edges;

            SutherlandHodgmanClipping.clip(clipped, x, y, width, height);
            for (var edge : clipped.edges) {
                System.out.println("Edge " + edge.from.id + " -> " + edge.to.id);
//                System.out.println("(" + edge.from.x + ", " + edge.from.y + ") -> (" + edge.to.x + ", " + edge.to.y + ")");
            }
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2d = (Graphics2D) g;

            // clipping rectangle
            g2d.setColor(Color.BLACK);
            g2d.drawLine((int) x, (int) y, (int) x, (int) (y + height));
            g2d.drawLine((int) x, (int) y, (int) (x + width), (int) y);
            g2d.drawLine((int) x, (int) (x + height), (int) (x + width), (int) (y + height));
            g2d.drawLine((int) (x + width), (int) y, (int) (x + width), (int) (y + height));

            // unclipped polygon
            g2d.setColor(Color.RED);
            draw(g2d, unclipped);
            g2d.setColor(Color.GREEN);
            // clipped polygon
            draw(g2d, clipped);
            // nodes
            draw(g2d, nodes);
        }

        private void draw(Graphics2D g2d, EdgeArc edgeArc) {
            for (var edge : edgeArc.edges) {
                g2d.drawLine((int) edge.from.x, (int) edge.from.y, (int) edge.to.x, (int) edge.to.y);
            }
        }

        private void draw(Graphics2D g2d, Node[] nodes) {
            for (var node : nodes) {
                drawOval(Color.BLACK, node.x, node.y, 5, g2d);
                g2d.drawString(node.id, (int) node.x, (int) node.y);
            }
        }

        private void drawOval(java.awt.Color c, double x, double y, double size, Graphics2D g2d){
            g2d.setColor(c);
            g2d.fillOval((int) (x - size/2), (int) (y - size/2), (int) size, (int) size);
        }
    }

}
