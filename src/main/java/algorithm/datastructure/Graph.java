package algorithm.datastructure;

import java.util.List;

public class Graph {
    public final List<Node> nodes;
    public final List<Edge> edges;

    public Graph(List<Node> nodes, List<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public String toString() {
        return nodes.toString() + "\n" + edges.toString();
    }

    public void rescale(int width, int height, int pad) {
        var offset = Math.min(nodes.stream().mapToDouble(n -> n.x).min().orElse(0),
                nodes.stream().mapToDouble(n -> n.y).min().orElse(0)) + pad;
        var scale = Math.min(width / (nodes.stream().mapToDouble(n -> n.x).max().orElse(width) + 2 * offset),
                height / (nodes.stream().mapToDouble(n -> n.y).max().orElse(height) + 2 * offset));

        for (var node : nodes) {
            node.rescale(-offset, scale);
        }
    }
}
