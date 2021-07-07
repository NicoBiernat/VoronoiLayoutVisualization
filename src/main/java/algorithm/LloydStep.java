package algorithm;

import java.awt.geom.Line2D;
import java.util.*;

public class LloydStep {

	private static final double WIDTH = 1000;
	private static final double HEIGHT = 1000;

	public final Graph inputGraph;
	public final List<DelaunayTriangle> delaunayTriangles;
	public final List<VoronoiCell> voronoiCells = new ArrayList<>();

	public LloydStep(Graph inputGraph) {
		this.inputGraph = inputGraph;

		delaunayTriangles = computeDelaunayTriangulation(inputGraph.nodes);

		// List to keep track of outgoing edges
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

				// rotate by 90°
				double h = dy;
				dy = dx;
				dx = -h;

				double x;
				double y;

				// calculate the factor needed to reach the boarder in the x direction
				if (dx > 0) {
					x = (WIDTH - circumCenter.x) / dx;
				} else {
					x = -(circumCenter.x / dx);
				}

				// calculate the factor needed to reach the boarder in the y direction
				if (dy > 0) {
					y = (HEIGHT - circumCenter.y) / dy;
				} else {
					y = -(circumCenter.y / dy);
				}

				// the smaller factor is used to calculate the intersection with the boarder
				// since it is the direction in which the edge crosses the boarder first
				if (Math.abs(x) < Math.abs(y)) {
					dx *= Math.abs(x);
					dy *= Math.abs(x);
				} else {
					dx *= Math.abs(y);
					dy *= Math.abs(y);
				}

				// create new Edge and check clipping with other outgoing edges
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
		// close open voronoi cells, that have been clipped
		for (var cell : voronoiCells) {
			var tl = new Node("tl", 0, 0);
			var tr = new Node("bl", WIDTH, 0);
			var bl = new Node("tl", 0, HEIGHT);
			var br = new Node("bl", WIDTH, HEIGHT);
			var nodes = cell.getNodesInOrder();
			var start = nodes.get(0);
			var end = nodes.get(nodes.size() - 1);
			if (cell.edges.stream()
					.anyMatch(edge -> edge.from == start && edge.to == end || edge.from == end && edge.to == start)) {
				// closed loop nothing to be done
			} else if (start.x < 1 && end.x < 1 || start.x > WIDTH - 1 && end.x > WIDTH - 1 || start.y < 1 && end.y < 1
					|| start.y > HEIGHT - 1 && end.y > HEIGHT - 1) {
				// start and end on one axis
				cell.edges.add(new Edge(start, end));
			} else if (start.x < 1 && end.x > WIDTH - 1 || start.x > WIDTH - 1 && end.x < 1) {
				double y = 0;
				for (var edge2 : cell.edges) {
					var dx = edge2.to.x - edge2.from.x;
					var dy = edge2.to.y - edge2.from.y;
					var len = Math.sqrt(dx * dx + dy * dy);
					dy /= len;
					y += dy;
				}
				if (y > 0) {
					if (start.x < end.x) {
						cell.edges.add(new Edge(start, bl));
						cell.edges.add(new Edge(bl, br));
						cell.edges.add(new Edge(br, end));
					} else {
						cell.edges.add(new Edge(start, br));
						cell.edges.add(new Edge(br, bl));
						cell.edges.add(new Edge(bl, end));
					}
				} else {
					if (start.x < end.x) {
						cell.edges.add(new Edge(start, tl));
						cell.edges.add(new Edge(tl, tr));
						cell.edges.add(new Edge(tr, end));
					} else {
						cell.edges.add(new Edge(start, tr));
						cell.edges.add(new Edge(tr, tl));
						cell.edges.add(new Edge(tl, end));
					}
				}
			} else if (start.y < 1 && end.y > HEIGHT - 1 || start.y > HEIGHT - 1 && end.y < 1) {
				double x = 0;
				for (var edge2 : cell.edges) {
					var dx = edge2.to.x - edge2.from.x;
					var dy = edge2.to.y - edge2.from.y;
					var len = Math.sqrt(dx * dx + dy * dy);
					dx /= len;
					x += dx;
				}
				if (x > 0) {
					if (start.y < end.y) {
						cell.edges.add(new Edge(start, tr));
						cell.edges.add(new Edge(tr, br));
						cell.edges.add(new Edge(br, end));
					} else {
						cell.edges.add(new Edge(start, br));
						cell.edges.add(new Edge(br, tr));
						cell.edges.add(new Edge(tr, end));
					}
				} else {
					if (start.y < end.y) {
						cell.edges.add(new Edge(start, tl));
						cell.edges.add(new Edge(tl, bl));
						cell.edges.add(new Edge(bl, end));
					} else {
						cell.edges.add(new Edge(start, bl));
						cell.edges.add(new Edge(bl, tl));
						cell.edges.add(new Edge(tl, end));
					}
				}
			} else {
				// assuming only one edge crossing, TODO: handle multiple edge crossings

				Node corner = null;

				if (start.x < 0.01 || end.x < 0.01) // left side
					if (start.y < 0.01 || end.y < 0.01) // top side
						corner = tl;
					else // bottom side
						corner = bl;
				else // right side
				if (start.y < 0.01 || end.y < 0.01) // top side
					corner = tr;
				else // bottom side
					corner = br;

				cell.edges.add(new Edge(start, corner));
				cell.edges.add(new Edge(corner, end));
			}
		}
		for (var cell : voronoiCells) {
			SutherlandHodgmanClipping.clip(cell, 0, 0, WIDTH, HEIGHT);
		}
	}

	public void checkClipping(ArrayList<Edge> edges, Edge newEdge) {
		for (Iterator<Edge> i = edges.iterator(); i.hasNext();) {
			Edge currentEdge = i.next();
			// check if the current edge crosses the new edge
			if (Line2D.linesIntersect(newEdge.from.x, newEdge.from.y, newEdge.to.x, newEdge.to.y, currentEdge.from.x,
					currentEdge.from.y, currentEdge.to.x, currentEdge.to.y) && !currentEdge.equals(newEdge)
					&& !newEdge.from.equals(currentEdge.from)) {
				// calculate the normed vectors of the current and new edge
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

				// calculate the point at which the two edges cross
				var x = ((currentEdge.from.x - newEdge.from.x) * dyCurrent
						- (currentEdge.from.y - newEdge.from.y) * dxCurrent) / (dxNew * dyCurrent - dyNew * dxCurrent);
				var y = ((currentEdge.from.x - newEdge.from.x) * dyNew - (currentEdge.from.y - newEdge.from.y) * dxNew)
						/ (dxNew * dyCurrent - dyNew * dxCurrent);
				var intersec = new Node(newEdge.from.x + x * dxNew, newEdge.from.y + x * dyNew);
				newEdge.to = intersec;
				currentEdge.to = intersec;

				edges.remove(currentEdge);
				edges.remove(newEdge);

				// calculate the intersection of the new outgoing Edge with the boarder
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

				// create the new outgoing Edge
				Edge newOutgoing = new Edge(intersec, new Node(newEdge.to.x + dxOutgoing, newEdge.to.y + dyOutgoing));

				// search for the voronoi cells of the new outgoing Edge
				boolean match1 = false;
				boolean match2 = false;
				for (var cell : voronoiCells) {
					for (Iterator<Edge> e = cell.edges.iterator(); e.hasNext();) {
						var f = e.next();
						if (f.equals(newEdge)) {
							match1 = true;
						} else if (f.equals(currentEdge)) {
							match2 = true;
						}
					}
					// if one of the new or current Edge is part of a cell the new outgoing Edge
					// must also be part of that cell
					if (match1 ^ match2) {
						cell.edges.add(newOutgoing);
					}
					match1 = false;
					match2 = false;
				}
				// add the new outgoing Edge to the outgoing edges
				edges.add(newOutgoing);
				checkClipping(edges, newOutgoing);
				return;
			}
		}
		// if the Edge dosen't cross any other outgoing Edge it isn't changed and added
		// to the outgoing Edges
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
		var maxX = inputNodes.stream().mapToDouble(node -> Double.isNaN(node.x)?0:node.x).max().orElse(0) + 10;
		var maxY = inputNodes.stream().mapToDouble(node -> Double.isNaN(node.y)?0:node.y).max().orElse(0) + 10;
		var minX = inputNodes.stream().mapToDouble(node -> Double.isNaN(node.x)?0:node.x).min().orElse(0) - 10;
		var minY = inputNodes.stream().mapToDouble(node -> Double.isNaN(node.y)?0:node.y).min().orElse(0) - 10;
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
