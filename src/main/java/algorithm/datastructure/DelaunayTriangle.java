package algorithm.datastructure;

public class DelaunayTriangle extends EdgeArc {

    public DelaunayTriangle(Node n1, Node n2, Node n3) {
        edges.add(new Edge(n1, n2));
        edges.add(new Edge(n2, n3));
        edges.add(new Edge(n3, n1));
    }

    public boolean contains(Node node) {
        return edges.stream().anyMatch(edge -> edge.from == node || edge.to == node);
    }

    public String toString() {
        return this.getNodes().toString();
    }

    private Circle circumCircle;

    public Circle getCircumCircle() {
        if (circumCircle != null)
            return circumCircle;
        // source:
        // https://en.wikipedia.org/wiki/Circumscribed_circle#Cartesian_coordinates_2
        var triNodes = this.getNodes();
        var A = triNodes.get(0);
        var B = triNodes.get(1);
        var C = triNodes.get(2);
        var B_ = new Node(B.x - A.x, B.y - A.y);
        var C_ = new Node(C.x - A.x, C.y - A.y);
        var D_ = 2 * (B_.x * C_.y - B_.y * C_.x);
        var Ux_ = 1 / D_
                * (C_.y * (Math.pow(B_.x, 2) + Math.pow(B_.y, 2)) - B_.y * (Math.pow(C_.x, 2) + Math.pow(C_.y, 2)));
        var Uy_ = -1 / D_
                * (C_.x * (Math.pow(B_.x, 2) + Math.pow(B_.y, 2)) - B_.x * (Math.pow(C_.x, 2) + Math.pow(C_.y, 2)));
        var circumCenter = new Node(this.toString(), Ux_ + A.x, Uy_ + A.y);
        var circumRadius = Math.sqrt(Math.pow(Ux_, 2) + Math.pow(Uy_, 2));
        circumCircle = new Circle(circumCenter, circumRadius);
        return circumCircle;
    }

    public static class Circle {
        public final Node center;
        public final double radius;

        public Circle(Node center, double radius) {
            this.center = center;
            this.radius = radius;
        }
    }
}
