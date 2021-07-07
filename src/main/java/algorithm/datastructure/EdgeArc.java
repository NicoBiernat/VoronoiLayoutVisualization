package algorithm.datastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EdgeArc {
    public List<Edge> edges = new ArrayList<>();

    public List<Node> getNodes() {
        var nodes = new ArrayList<Node>();
        for (var edge : edges) {
            if (!nodes.contains(edge.from))
                nodes.add(edge.from);
            if (!nodes.contains(edge.to))
                nodes.add(edge.to);
        }
        return nodes;
    }

    private List<Node> findAdjacent(Node node) {
        var adjacentEdges = this.edges.stream().filter(edge -> edge.to == node || edge.from == node)
                .collect(Collectors.toList());
        var nodes = new ArrayList<Node>();
        for (var edge : adjacentEdges) {
            if (!nodes.contains(edge.to) && edge.to != node)
                nodes.add(edge.to);
            if (!nodes.contains(edge.from) && edge.from != node)
                nodes.add(edge.from);
        }
        return nodes;
    }

    public List<Node> getNodesInOrder() {
        var ends = new ArrayList<Node>();
        var inputNodes = getNodes();
        if (inputNodes.size() == 0) {
            return ends;
        }
        for (var node : inputNodes)
            if (findAdjacent(node).size() == 1)
                ends.add(node);
        var nodesInOrder = new ArrayList<Node>();
        Node lastNode = null;
        Node currentNode;
        if (ends.size() > 0)
            currentNode = ends.get(0);
        else
            currentNode = inputNodes.get(0);

        while (nodesInOrder.size() < inputNodes.size()) {
            Optional<Edge> currentEdge;
            nodesInOrder.add(currentNode);
            var adjacent = findAdjacent(currentNode).stream().filter(node -> !nodesInOrder.contains(node))
                    .collect(Collectors.toList());
            if (adjacent.size() == 0)
                break;
            currentNode = adjacent.get(0);
        }

        return nodesInOrder;
    }

    private Node centroid;

    public Node getCentroid() {
        // cache result so obj equality holds
        if (centroid != null)
            return centroid;
        // source:
        // https://www.geeksforgeeks.org/find-the-centroid-of-a-non-self-intersecting-closed-polygon/
        double x = 0, y = 0;
        double signedArea = 0;
        var v = getNodesInOrder();
        double ansX = 0, ansY = 0;
        // For all vertices
        for (int i = 0; i < v.size(); i++) {
            double x0 = v.get(i).x, y0 = v.get(i).y;
            double x1 = v.get((i + 1) % v.size()).x, y1 = v.get((i + 1) % v.size()).y;
            // Calculate value of A
            // using shoelace formula
            double A = (x0 * y1) - (x1 * y0);
            signedArea += A;
            // Calculating coordinates of
            // centroid of polygon
            ansX += (x0 + x1) * A;
            ansY += (y0 + y1) * A;
        }
        signedArea *= 0.5;
        ansX = (ansX) / (6 * signedArea);
        ansY = (ansY) / (6 * signedArea);
        centroid = new Node(ansX, ansY);
        return centroid;
    }

    public Node getNodesCentroid() {
        double ansX = 0, ansY = 0;
        var v = getNodes();
        for (var node : v) {
            ansX += node.x;
            ansY += node.y;
        }
        return new Node(ansX / v.size(), ansY / v.size());
    }
}
