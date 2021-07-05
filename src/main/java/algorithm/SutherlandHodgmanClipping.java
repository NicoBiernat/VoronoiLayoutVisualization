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

    public static void clip(LloydStep.EdgeArc polygon, double x, double y, double width, double height) {
        List<LloydStep.Node> nodes = polygon.getNodesInOrder();
        List<LloydStep.Node> resultList = new ArrayList<>();

        Predicate<LloydStep.Node> insideTop = n -> n.y >= y;
        Predicate<LloydStep.Node> outsideTop = n -> n.y < y;

        Predicate<LloydStep.Node> insideRight = n -> n.x <= x + width;
        Predicate<LloydStep.Node> outsideRight = n -> n.x > x + width;

        Predicate<LloydStep.Node> insideBottom = n -> n.y <= y + height;
        Predicate<LloydStep.Node> outsideBottom = n -> n.y > y + height;

        Predicate<LloydStep.Node> insideLeft = n -> n.x >= x;
        Predicate<LloydStep.Node> outsideLeft = n -> n.x < x;

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

        List<LloydStep.Edge> newEdges = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            newEdges.add(new LloydStep.Edge(resultList.get(i), resultList.get((i+1) % resultList.size())));
        }
        polygon.edges = newEdges;
    }

    private enum ClippingDirection {
        HORIZONTAL,
        VERTICAL
    }

    private static void clipSide(List<LloydStep.Node> nodes, List<LloydStep.Node> resultList,
                                 Predicate<LloydStep.Node> inside, Predicate<LloydStep.Node> outside,
                                 ClippingDirection clippingDirection,  double clippingEdgePos) {
        for (int i = 0; i < nodes.size(); i++) {
            var from = nodes.get(i);
            var to = nodes.get((i+1) % nodes.size());

            if (inside.test(from) && inside.test(to)) { // both inside
                resultList.add(to);
            } else if (inside.test(from) && outside.test(to)) { // "from" inside, "to" outside
                double intersectionPos = calculateIntersection(clippingDirection, clippingEdgePos, from, to);
                switch (clippingDirection) {
                    case HORIZONTAL:
                        resultList.add(new LloydStep.Node("S(" + from.id + "," + to.id + ")", intersectionPos, clippingEdgePos));
                        break;
                    case VERTICAL:
                        resultList.add(new LloydStep.Node("S(" + from.id + "," + to.id + ")", clippingEdgePos, intersectionPos));
                        break;
                }
            } else if (outside.test(from) && inside.test(to)) { // "from" outside, "to" inside
                double intersectionPos = calculateIntersection(clippingDirection, clippingEdgePos, from, to);
                switch (clippingDirection) {
                    case HORIZONTAL:
                        resultList.add(new LloydStep.Node("S(" + from.id + "," + to.id + ")", intersectionPos, clippingEdgePos));
                        break;
                    case VERTICAL:
                        resultList.add(new LloydStep.Node("S(" + from.id + "," + to.id + ")", clippingEdgePos, intersectionPos));
                        break;
                }
                resultList.add(to);
            } // both outside -> add nothing
        }
    }

    private static double calculateIntersection(ClippingDirection clippingDirection, double fixedCoord, LloydStep.Node from, LloydStep.Node to) {
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
        private final LloydStep.EdgeArc unclipped;
        private final LloydStep.EdgeArc clipped;

        private final LloydStep.Node[] nodes = {
                new LloydStep.Node("n1", 120, 120),
                new LloydStep.Node("n2", 160, 80),
                new LloydStep.Node("n3", 650, 300),
                new LloydStep.Node("n4", 300, 650),
                new LloydStep.Node("n5", 80, 300),
        };

        public TestCanvas() {
            List<LloydStep.Edge> edges = new ArrayList<>();
            for (int i = 0; i < nodes.length; i++) {
                edges.add(new LloydStep.Edge(nodes[i], nodes[(i+1) % nodes.length]));
            }
            List<LloydStep.Edge> edgesCopy = new ArrayList<>(edges);

            unclipped = new LloydStep.EdgeArc();
            unclipped.edges = edgesCopy;
            clipped = new LloydStep.EdgeArc();
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

        private void draw(Graphics2D g2d, LloydStep.EdgeArc edgeArc) {
            for (var edge : edgeArc.edges) {
                g2d.drawLine((int) edge.from.x, (int) edge.from.y, (int) edge.to.x, (int) edge.to.y);
            }
        }

        private void draw(Graphics2D g2d, LloydStep.Node[] nodes) {
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
