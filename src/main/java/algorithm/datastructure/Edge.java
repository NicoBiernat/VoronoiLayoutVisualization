package algorithm.datastructure;

public class Edge {
    public Node from;
    public Node to;

    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    public String toString() {
        return from.id + " -> " + to.id;
    }

    public boolean equals(Edge edge, boolean directed) {
        if (directed) {
            return this.from == edge.from && this.to == edge.to;
        }
        return this.from == edge.from && this.to == edge.to || this.from == edge.to && this.to == edge.from;
    }
}
