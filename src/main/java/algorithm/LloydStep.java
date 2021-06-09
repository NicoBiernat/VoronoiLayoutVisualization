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
}
class Node{
	public double x = 0;
	public double y = 0;
	public final String id;
	public Node(String id) {
		this.id=id;
	}
	public Node() {
		this("");
	}
}

class Graph{
	public final List<Node> nodes;
	public final List<Edge> edges;
	public Graph(List<Node> nodes,List<Edge> edges) {
		this.nodes=nodes;
		this.edges=edges;
	}
}
public class LloydStep {
	public final Graph inputGraph;
	public final List<DelaunayTriangle> delaunayTriangles;
	public final List<VoronoiCell> voronoiCells = new ArrayList<>();
	public final Map<DelaunayEdge,VoronoiEdge> delaunayVoronoiEdgeMap = new HashMap<>();
	
	public LloydStep(Graph inputGraph) {
		this.inputGraph=inputGraph;


		delaunayTriangles = computeDelaunayTriangulation(inputGraph);
		
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

	private List<DelaunayTriangle> computeDelaunayTriangulation(Graph inputGraph) {
		List<DelaunayTriangle> triangles = new ArrayList<>();
		System.out.println("computeDelaunayTriangulation");
		//TODO: translate this:
		/*
		 * 	function triangulate(vertices: Vertice[]): Triangle[] {
		        const objEqual = (a: any[], b: any[]) => JSON.stringify(a.sort((c, d) => c.x - d.x)) == JSON.stringify(b.sort((c, d) => c.x - d.x));
		
		        let triangleList: Triangle[] = [];
		        const superTriangle: Triangle = [{ x: 25, y: 25 }, { x: 1000, y: 25 }, { x: 25, y: 1000 }];
		        triangleList.push(superTriangle);
		        //idk why removeing this doesnt make any difference
		        //superTriangle.forEach((v, i) => vertices.push({ ...v, id: "st" + i }));
		        vertices.forEach(samplePoint => {
		            let edgeBuffer: [{ x: number, y: number }, { x: number, y: number }][] = [];
		            triangleList.forEach(triangle => {
		                // calculate the triangle circumcircle center and radius
		                //const d = (a, b) => Math.sqrt((a.x - b.x) ** 2 + (a.y + b.y) ** 2);
		                //const [a, b, c] = [d(triangle[0], triangle[1]), d(triangle[1], triangle[2]), d(triangle[2], triangle[0])];
		                //const s = (a + b + c) / 2;
		                //const circumRadius = (a * b * c) / (4 * Math.sqrt(s * (s - a) * (s - b) * (s - c)))
		
		                //source: https://en.wikipedia.org/wiki/Circumscribed_circle#Cartesian_coordinates_2
		                const [A, B, C] = triangle;
		                const [B_, C_] = [{ x: B.x - A.x, y: B.y - A.y }, { x: C.x - A.x, y: C.y - A.y }];
		                const D_ = 2 * (B_.x * C_.y - B_.y * C_.x)
		                const Ux_ = 1 / D_ * (C_.y * (B_.x ** 2 + B_.y ** 2) - B_.y * (C_.x ** 2 + C_.y ** 2))
		                const Uy_ = -1 / D_ * (C_.x * (B_.x ** 2 + B_.y ** 2) - B_.x * (C_.x ** 2 + C_.y ** 2))
		                const circumCenter = { x: Ux_ + A.x, y: Uy_ + A.y };
		                const circumRadius = Math.sqrt(Ux_ ** 2 + Uy_ ** 2);
		
		                // if the point lies in the triangle circumcircle then
		                if ((samplePoint.x - circumCenter.x) ** 2 + (samplePoint.y - circumCenter.y) ** 2 < circumRadius ** 2) {
		                    edgeBuffer.push([triangle[0], triangle[1]]);
		                    edgeBuffer.push([triangle[1], triangle[2]]);
		                    edgeBuffer.push([triangle[2], triangle[0]]);
		
		                    triangleList = triangleList.filter(t => !objEqual(t, triangle));
		                }
		            })
		            //delete all doubly specified edges from the edge buffer
		            edgeBuffer = edgeBuffer.filter((edge, _, arr) => arr.filter(e => objEqual(e, edge)).length == 1);
		            //this leaves the edges of the enclosing polygon only
		
		            edgeBuffer.forEach(edge => {
		                triangleList.push([edge[0], edge[1], samplePoint])
		            })
		        })
		        //     remove any triangles from the triangle list that use the supertriangle vertices
		        return triangleList.filter(t => !t.some(vertex => superTriangle.some(v => objEqual([vertex], [v]))));
		    }
		 */
		return triangles;
	}
}
