package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DelaunayTriangle extends EdgeArc{
	public DelaunayTriangle(Edge e1, Edge e2, Edge e3) {
		edges.add(e1);
		edges.add(e2);
		edges.add(e3);
	}
	public DelaunayTriangle(Node n1, Node n2, Node n3) {
		edges.add(new Edge(n1,n2));
		edges.add(new Edge(n2,n3));
		edges.add(new Edge(n3,n1));
	}

	public boolean contains(Node node){
		return edges.stream().anyMatch(edge->edge.from==node || edge.to == node);
	}

	public String toString(){
		return this.getNodes().toString();
	}
}

class VoronoiCell extends EdgeArc{
	public final Node node;
	public VoronoiCell(Node node) {
		this.node=node;
	}
}

class EdgeArc{
	List<Edge> edges = new ArrayList<>();
	
	public List<Node> getNodes() {
		var nodes = new ArrayList<Node>();
		for (var edge: edges) {
			if (!nodes.contains(edge.from)) nodes.add(edge.from);
			if (!nodes.contains(edge.to)) nodes.add(edge.to);
		}
		return nodes;
	}
	public Node getCentroid() {
		// TODO: to be implemented
		return null;
	}
}

class DelaunayEdge extends Edge{
	public DelaunayEdge(Node from, Node to) {
		super(from, to);
	}
}
class VoronoiEdge extends Edge{
	public VoronoiEdge(Node from, Node to) {
		super(from, to);
	}
}
class Edge{	
	public Node from;
	public final Node to;
	public Edge(Node from, Node to) {
		this.from=from;
		this.to=to;
	}
	public String toString(){
		return from.id +" -> "+to.id;
	}
	public boolean equals(Edge edge, boolean directed){
		if (directed){
			return this.from==edge.from && this.to== edge.to;
		}
		return this.from==edge.from && this.to== edge.to || this.from==edge.to && this.to== edge.from ;
	}
}
class Node{
	public double x = 0;
	public double y = 0;
	public final String id;
	public Node(String id) {
		this.id=id;
	}
	public Node(double x, double y) {
		this("",x,y);
	}
	public Node(String id,double x,double y) {
		this(id);
		this.x=x;
		this.y=y;
	}
	public Node() {
		this("");
	}

	public String toString(){
		return id;//"("+id+": "+x+", "+y+")";
	}
}

class Graph{
	public final List<Node> nodes;
	public final List<Edge> edges;
	public Graph(List<Node> nodes,List<Edge> edges) {
		this.nodes=nodes;
		this.edges=edges;
	}
	public String toString(){
		return nodes.toString()+"\n"+edges.toString();
	}
}
public class LloydStep {
	public final Graph inputGraph;
	public final List<DelaunayTriangle> delaunayTriangles;
	public final List<VoronoiCell> voronoiCells = new ArrayList<>();
	public final Map<DelaunayEdge,VoronoiEdge> delaunayVoronoiEdgeMap = new HashMap<>();
	
	public LloydStep(Graph inputGraph) {
		this.inputGraph=inputGraph;

		System.out.println("LloydStep");
		System.out.println(inputGraph);

		delaunayTriangles = computeDelaunayTriangulation(inputGraph.nodes);

		System.out.println(delaunayTriangles);
		//compute voronoiCells
		for (var delaunayEdge : getDelaunayEdges()) {
			//TODO: compute centroids for adjacent triangles / frame crossing if no 2nd triangle
			//TODO: add edge from adjacent centroids/framecrossings to the following lists:
			getVoronoiCellForNode(delaunayEdge.from).edges.add(null);
			getVoronoiCellForNode(delaunayEdge.to).edges.add(null);
		}
	}
	
	public ArrayList<Node> getVoronoiCentroids() {
		var nodes = new ArrayList<Node>();
		for (var cell: voronoiCells)
			nodes.add(cell.getCentroid());
		return nodes;
	}
	
	public VoronoiCell getVoronoiCellForNode(Node node) {
		for (var cell: voronoiCells)
			if (cell.node==node) return cell;
		 
		var cell = new VoronoiCell(node);
		voronoiCells.add(cell);
		return cell;
	}
	
	public ArrayList<Edge> getDelaunayEdges() {
		var edges = new ArrayList<Edge>();
		for (var triangle: delaunayTriangles) 
			for (var edge: triangle.edges) 
				if (!edges.contains(edge)) edges.add(edge);
		return edges;
	}

	private List<DelaunayTriangle> computeDelaunayTriangulation(List<Node> inputNodes) {

		List<DelaunayTriangle> triangleList = new ArrayList<>();

		//create copy of input nodes
		List<Node> nodes = new ArrayList<>(inputNodes);

		//construct triangle containing all input nodes
		var maxX = inputNodes.stream().mapToDouble(node -> node.x).max().orElse(0)+10;
		var maxY = inputNodes.stream().mapToDouble(node -> node.y).max().orElse(0)+10;
		var minX = inputNodes.stream().mapToDouble(node -> node.x).min().orElse(0)-10;
		var minY = inputNodes.stream().mapToDouble(node -> node.y).min().orElse(0)-10;
		var superTriangle = new DelaunayTriangle(new Node("s1",minX,minY), new Node("s2",maxX*2, minY), new Node("s3",minX,maxY*2));
		triangleList.add(superTriangle);

		//source: http://paulbourke.net/papers/triangulate/
		for (var samplePoint: nodes){
			var trianglesToBeRemoved = new ArrayList<DelaunayTriangle>();
			var edgeBuffer = new ArrayList<Edge>();
			for (var triangle: triangleList){
				//source: https://en.wikipedia.org/wiki/Circumscribed_circle#Cartesian_coordinates_2
				var triNodes= triangle.getNodes();
				var A = triNodes.get(0);
				var B = triNodes.get(1);
				var C = triNodes.get(2);
				var B_ = new Node(B.x - A.x, B.y - A.y);
				var C_ = new Node(C.x - A.x, C.y - A.y );
				var D_ = 2 * (B_.x * C_.y - B_.y * C_.x);
				var Ux_ = 1 / D_ * (C_.y * (Math.pow(B_.x,2) + Math.pow(B_.y,2)) - B_.y * (Math.pow(C_.x,2) + Math.pow(C_.y,2)));
				var Uy_ = -1 / D_ * (C_.x * (Math.pow(B_.x,2) + Math.pow(B_.y,2)) - B_.x * (Math.pow(C_.x,2) + Math.pow(C_.y,2)));
				var circumCenter = new Node( Ux_ + A.x, Uy_ + A.y);
				var circumRadius = Math.sqrt(Math.pow(Ux_,2) + Math.pow(Uy_,2));

				// if the point lies in the triangle circumCircle then
				if (Math.pow(samplePoint.x - circumCenter.x,2) +  Math.pow(samplePoint.y -circumCenter.y,2) < Math.pow(circumRadius,2)) {
					edgeBuffer.addAll(triangle.edges);
					trianglesToBeRemoved.add(triangle);
				}
			}
			triangleList.removeAll(trianglesToBeRemoved);
			//delete all doubly specified edges from the edge buffer
			edgeBuffer.removeIf(edge -> edgeBuffer.stream().filter(e->e.equals(edge,false)).limit(2).count() == 2);
			//this leaves the edges of the enclosing polygon only

			for (var edge : edgeBuffer){
				triangleList.add(new DelaunayTriangle(edge.from, edge.to, samplePoint));
			}
		}

		//remove any triangles from the triangle list that use the superTriangle nodes
		var triangles = new ArrayList<DelaunayTriangle>();
		for (var triangle: triangleList)
			if (superTriangle.getNodes().stream().noneMatch(triangle::contains))
				triangles.add(triangle);
		return triangles;
	}
}
