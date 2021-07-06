package algorithm;

import java.awt.geom.Line2D;
import java.util.*;
import java.util.stream.Collectors;

public class LloydStep {

	public static class DelaunayTriangle extends EdgeArc {
		public DelaunayTriangle(Edge e1, Edge e2, Edge e3) {
			edges.add(e1);
			edges.add(e2);
			edges.add(e3);
		}

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

	public static class VoronoiCell extends EdgeArc {
		public final Node node;

		public VoronoiCell(Node node) {
			this.node = node;
		}
	}

	public static class EdgeArc {
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
			for (var node: inputNodes)
				if (findAdjacent(node).size()==1)
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

	public static class DelaunayEdge extends Edge {
		public DelaunayEdge(Node from, Node to) {
			super(from, to);
		}
	}

	public static class VoronoiEdge extends Edge {
		public VoronoiEdge(Node from, Node to) {
			super(from, to);
		}
	}

	public static class Edge {
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

	public static class Node {
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
		public void rescale(double offset, double scale){
			x=(x-offset)*scale;
			y=(y-offset)*scale;
		}
		public Node() {
			this("");
		}

		public String toString() {
			return id;// "("+id+": "+x+", "+y+")";
		}
	}

	public static class Graph {
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
			var offset = Math.min(nodes.stream().mapToDouble(n->n.x).min().orElse(0),
					                     nodes.stream().mapToDouble(n->n.y).min().orElse(0))+pad;
			var scale = Math.min(width/(nodes.stream().mapToDouble(n->n.x).max().orElse(width)+2*offset),
				       			height/(nodes.stream().mapToDouble(n->n.y).max().orElse(height)+2*offset));

//			System.out.println(offset+" "+scale);
			for (var node: nodes){
				node.rescale(-offset,scale);
			}
		}
	}


	private static final double WIDTH = 1000;
	private static final double HEIGHT = 1000;

	public final Graph inputGraph;
	public final List<DelaunayTriangle> delaunayTriangles;
	public final List<VoronoiCell> voronoiCells = new ArrayList<>();

	public LloydStep(Graph inputGraph) {
		this.inputGraph = inputGraph;

		delaunayTriangles = computeDelaunayTriangulation(inputGraph.nodes);

		//List to keep track of outgoing edges
		var outgoingEdges = new ArrayList<Edge>();
		// compute voronoiCells
		for (var delaunayEdge : getDelaunayEdges()) {
			var adjacentTriangles = new ArrayList<DelaunayTriangle>();
			for (var triangle : delaunayTriangles)
				if (triangle.edges.stream().anyMatch(e -> e.equals(delaunayEdge, false)))
					adjacentTriangles.add(triangle);
			if (adjacentTriangles.size() == 1) {
				var circumCenter = adjacentTriangles.get(0).getCircumCircle().center;

				var dx = delaunayEdge.from.x - delaunayEdge.to.x;
				var dy = delaunayEdge.from.y - delaunayEdge.to.y;

				//rotate by 90°
				double h = dy;
				dy = dx;
				dx = -h;

				double x;
				double y;

				//calculate the factor needed to reach the boarder in the x direction
				if (dx > 0) {
					x = (WIDTH - circumCenter.x) / dx;
				} else {
					x = -(circumCenter.x / dx);
				}
				
				//calculate the factor needed to reach the boarder in the y direction
				if (dy > 0) {
					y = (HEIGHT - circumCenter.y) / dy;
				} else {
					y = -(circumCenter.y / dy);
				}

				//the smaller factor is used to calculate the intersection with the boarder
				//since it is the direction in which the edge crosses the boarder first
				if (Math.abs(x) < Math.abs(y)) {
					dx *= Math.abs(x);
					dy *= Math.abs(x);
				} else {
					dx *= Math.abs(y);
					dy *= Math.abs(y);
				}

				//create new Edge and check clipping with other outgoing edges
				var frameCrossing = new Node("F" + circumCenter.toString(), circumCenter.x + dx, circumCenter.y + dy);
				var voronoiEdge = new Edge(circumCenter, frameCrossing);
				getVoronoiCellForNode(delaunayEdge.from).edges.add(voronoiEdge);
				getVoronoiCellForNode(delaunayEdge.to).edges.add(voronoiEdge);
				checkClipping(outgoingEdges, voronoiEdge);
			} else {
				// simple case 2 adjacent triangles add line from adjacent circumCenters
				var voronoiEdge = new Edge(adjacentTriangles.get(0).getCircumCircle().center,
						adjacentTriangles.get(1).getCircumCircle().center);
				getVoronoiCellForNode(delaunayEdge.from).edges.add(voronoiEdge);
				getVoronoiCellForNode(delaunayEdge.to).edges.add(voronoiEdge);
			}
		}
		//close open voronoi cells, that have been clipped
		for (var cell : voronoiCells){
			var nodes = cell.getNodesInOrder();
			var start = nodes.get(0);
			var end = nodes.get(nodes.size()-1);
			if (cell.edges.stream().anyMatch(edge -> edge.from==start && edge.to == end || edge.from==end && edge.to == start)){
				//closed loop nothing to be done
			}else if (Math.abs(start.x-end.x)<0.01 || Math.abs(start.y-end.y)<0.01){
				//start and end on one axis
				cell.edges.add(new Edge(start,end));
			}else{
				//assuming only one edge crossing, TODO: handle multiple edge crossings

				var tl=new Node("tl",0,0);
				var tr=new Node("bl",WIDTH,0);
				var bl=new Node("tl",0, HEIGHT);
				var br=new Node("bl",WIDTH, HEIGHT);
				Node corner = null;

				if (start.x<0.01 || end.x<0.01) //left side
					if (start.y<0.01 || end.y<0.01) //top side
						corner=tl;
					else //bottom side
						corner=bl;
				else //right side
					if (start.y<0.01 || end.y<0.01) //top side
						corner=tr;
					else //bottom side
						corner=br;

				cell.edges.add(new Edge(start,corner));
				cell.edges.add(new Edge(corner,end));
			}
		}
		for (var cell : voronoiCells) {
			SutherlandHodgmanClipping.clip(cell, 0, 0, WIDTH, HEIGHT);
		}
	}
	
	public void checkClipping(ArrayList<Edge> edges, Edge newEdge) {
		for (Iterator<Edge> i = edges.iterator(); i.hasNext();) {
			Edge currentEdge = i.next();
			//check if the current edge crosses the new edge
			if (Line2D.linesIntersect(newEdge.from.x, newEdge.from.y, newEdge.to.x, newEdge.to.y, currentEdge.from.x, currentEdge.from.y, currentEdge.to.x, currentEdge.to.y) && !currentEdge.equals(newEdge) && !newEdge.from.equals(currentEdge.from)) {
				//calculate the normed vectors of the current and new edge
				var dxNew = newEdge.from.x - newEdge.to.x;
				var dyNew = newEdge.from.y - newEdge.to.y;
				var dxCurrent = currentEdge.from.x - currentEdge.to.x;
				var dyCurrent = currentEdge.from.y - currentEdge.to.y;
				var lenNew = Math.sqrt(dxNew * dxNew + dyNew * dyNew);
				var lenCurrent = Math.sqrt(dxCurrent * dxCurrent + dyCurrent * dyCurrent);
				dxNew /= lenNew;
				dyNew /= lenNew;
				dxCurrent /= lenCurrent;
				dyCurrent /= lenCurrent;
				
				//calculate the point at which the two edges cross
				var x = ((currentEdge.from.x - newEdge.from.x) * dyCurrent - (currentEdge.from.y - newEdge.from.y) * dxCurrent) / (dxNew * dyCurrent - dyNew * dxCurrent);
				var y = ((currentEdge.from.x - newEdge.from.x) * dyNew - (currentEdge.from.y - newEdge.from.y) * dxNew) / (dxNew * dyCurrent - dyNew * dxCurrent);
				var intersec = new Node(newEdge.from.x + x * dxNew, newEdge.from.y + x * dyNew);
				newEdge.to = intersec;
				currentEdge.to = intersec;
				
				edges.remove(currentEdge);
				edges.remove(newEdge);
				
				//calculate the intersection of the new outgoing Edge with the boarder
				var dxOutgoing = -dxNew - dxCurrent;
				var dyOutgoing = -dyNew - dyCurrent;

				if (dxOutgoing > 0) {
					x = (WIDTH - newEdge.to.x) / dxOutgoing;
				} else {
					x = -(newEdge.to.x / dxOutgoing);
				}
				
				if (dyOutgoing > 0) {
					y = (HEIGHT - newEdge.to.y) / dyOutgoing;
				} else {
					y = -(newEdge.to.y / dyOutgoing);
				}

				if (Math.abs(x) < Math.abs(y)) {
					dxOutgoing *= Math.abs(x);
					dyOutgoing *= Math.abs(x);
				} else {
					dxOutgoing *= Math.abs(y);
					dyOutgoing *= Math.abs(y);
				}
				
				//create the new outgoing Edge
				Edge newOutgoing = new Edge(intersec, new Node(newEdge.to.x + dxOutgoing, newEdge.to.y + dyOutgoing));
				
				//search for the voronoi cells of the new outgoing Edge
				boolean match1 = false;
				boolean match2 = false;
				for (var cell : voronoiCells) {
					for (Iterator<Edge> e = cell.edges.iterator(); e.hasNext();) {
						var f = e.next();
						if (f.equals(newEdge)) {
							match1 = true;
						}else if (f.equals(currentEdge)) {
							match2 = true;
						}
					}
					//if one of the new or current Edge is part of a cell the new outgoing Edge must also be part of that cell
					if (match1 ^ match2) {
						cell.edges.add(newOutgoing);
					}
					match1 = false;
					match2 = false;
				}
				//add the new outgoing Edge to the outgoing edges
				edges.add(newOutgoing);
				checkClipping(edges, newOutgoing);
				return;
			}
		}
		//if the Edge dosen't cross any other outgoing Edge it isn't changed and added to the outgoing Edges
		edges.add(newEdge);
	}

	public ArrayList<Node> getVoronoiCentroids() {
		var nodes = new ArrayList<Node>();
		for (var cell : voronoiCells)
			nodes.add(cell.getCentroid());
		return nodes;
	}

	public VoronoiCell getVoronoiCellForNode(Node node) {
		for (var cell : voronoiCells)
			if (cell.node == node)
				return cell;

		var cell = new VoronoiCell(node);
		voronoiCells.add(cell);
		return cell;
	}

	public ArrayList<Edge> getDelaunayEdges() {
		var edges = new ArrayList<Edge>();
		for (var triangle : delaunayTriangles)
			for (var edge : triangle.edges)
				if (edges.stream().noneMatch(e -> e.equals(edge, false)))
					edges.add(edge);
		return edges;
	}

	private List<DelaunayTriangle> computeDelaunayTriangulation(List<Node> inputNodes) {

		List<DelaunayTriangle> triangleList = new ArrayList<>();

		// create copy of input nodes
		List<Node> nodes = new ArrayList<>(inputNodes);

		// construct triangle containing all input nodes
		var maxX = inputNodes.stream().mapToDouble(node -> node.x).max().orElse(0) + 10;
		var maxY = inputNodes.stream().mapToDouble(node -> node.y).max().orElse(0) + 10;
		var minX = inputNodes.stream().mapToDouble(node -> node.x).min().orElse(0) - 10;
		var minY = inputNodes.stream().mapToDouble(node -> node.y).min().orElse(0) - 10;
		var superTriangle = new DelaunayTriangle(new Node("s1", minX, minY), new Node("s2", maxX * 2, minY),
				new Node("s3", minX, maxY * 2));
		triangleList.add(superTriangle);

		// source: http://paulbourke.net/papers/triangulate/
		for (var samplePoint : nodes) {
			var trianglesToBeRemoved = new ArrayList<DelaunayTriangle>();
			var edgeBuffer = new ArrayList<Edge>();
			for (var triangle : triangleList) {
				var circumCircle = triangle.getCircumCircle();
				// if the point lies in the triangle circumCircle then
				if (Math.pow(samplePoint.x - circumCircle.center.x, 2)
						+ Math.pow(samplePoint.y - circumCircle.center.y, 2) < Math.pow(circumCircle.radius, 2)) {
					edgeBuffer.addAll(triangle.edges);
					trianglesToBeRemoved.add(triangle);
				}
			}
			triangleList.removeAll(trianglesToBeRemoved);
			// delete all doubly specified edges from the edge buffer
			edgeBuffer.removeIf(edge -> edgeBuffer.stream().filter(e -> e.equals(edge, false)).limit(2).count() == 2);
			// this leaves the edges of the enclosing polygon only

			for (var edge : edgeBuffer) {
				triangleList.add(new DelaunayTriangle(edge.from, edge.to, samplePoint));
			}
		}
		// remove any triangles from the triangle list that use the superTriangle nodes
		var triangles = new ArrayList<DelaunayTriangle>();
		for (var triangle : triangleList)
			if (superTriangle.getNodes().stream().noneMatch(triangle::contains))
				triangles.add(triangle);
		return triangles;
	}
}
