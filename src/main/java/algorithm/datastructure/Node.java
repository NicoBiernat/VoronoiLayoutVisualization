package algorithm.datastructure;

public class Node {
    public double x = 0;
    public double y = 0;
    public final String id;

    public Node(String id) {
        this.id = id;
    }

    public Node(double x, double y) {
        this("", x, y);
    }

    public Node(String id, double x, double y) {
        this(id);
        this.x = x;
        this.y = y;
    }

    public void rescale(double offset, double scale) {
        x = (x - offset) * scale;
        y = (y - offset) * scale;
    }

    public Node() {
        this("");
    }

    public String toString() {
        return id; //"(" + id + ": " + x + ", " + y + ")";
    }
}
