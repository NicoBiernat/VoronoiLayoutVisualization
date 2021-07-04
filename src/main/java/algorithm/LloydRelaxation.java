package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.elk.graph.ElkNode;

import algorithm.LloydStep.Edge;
import algorithm.LloydStep.Graph;
import algorithm.LloydStep.Node;

public class LloydRelaxation {
	private static final double MAX_DISTANCE = 0.01;
	public final ElkNode inputGraph;
	public final LloydStep.Graph transformedGraph;
	public final Map<ElkNode, LloydStep.Node> transformationMap;
	public final List<LloydStep> lloydSteps = new ArrayList<>();

	public LloydRelaxation(ElkNode inputGraph) {
		this.inputGraph = inputGraph;
		List<LloydStep.Edge> edges = new ArrayList<>();
		List<LloydStep.Node> nodes = new ArrayList<>();
		transformationMap = new HashMap<>();
		for (var elkNode : inputGraph.getChildren()) {
			var node = new LloydStep.Node(elkNode.getIdentifier(), elkNode.getX(), elkNode.getY());
			transformationMap.put(elkNode, node);
			nodes.add(node);
		}
		for (var edge : inputGraph.getContainedEdges())
			edges.add(new LloydStep.Edge(transformationMap.get((ElkNode) edge.getSources().get(0)),
					transformationMap.get((ElkNode) edge.getTargets().get(0))));
		this.transformedGraph = new LloydStep.Graph(nodes, edges);
	}

	private boolean distance(Node a, Node b) {
		return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2)) > MAX_DISTANCE;
	}
	
	public void computeSteps() {

		transformedGraph.rescale(1000,1000,100);

		lloydSteps.add(new LloydStep(transformedGraph));
		
		Boolean end = true;
		int i = 0;
		while (end && lloydSteps.size()<1000) {
			LloydStep last = lloydSteps.get(i);
			ArrayList<Node> nodes = new ArrayList<>();

			var newNodesMap = new HashMap<Node,Node>();

			final double RELAXATION_MOVING_RATE=1;
			for (Node n : last.inputGraph.nodes) {
				Node centroid = last.getVoronoiCellForNode(n).getCentroid();
				end = distance(n, centroid);
				var newNode = new Node(n.id,
						n.x*(1-RELAXATION_MOVING_RATE) + centroid.x*RELAXATION_MOVING_RATE,
						n.y*(1-RELAXATION_MOVING_RATE) + centroid.y*RELAXATION_MOVING_RATE);
				nodes.add(newNode);
				newNodesMap.put(n,newNode);
			}

			var edges = new ArrayList<Edge>();
			for (Edge e : last.inputGraph.edges) {
				edges.add(new Edge(newNodesMap.get(e.from), newNodesMap.get(e.to)));
			}

			lloydSteps.add(new LloydStep(new Graph(nodes, edges)));
			i++;
		}
	}

}
