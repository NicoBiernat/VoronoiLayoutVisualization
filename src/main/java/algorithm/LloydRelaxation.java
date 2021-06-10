package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.elk.graph.ElkNode;

public class LloydRelaxation {
	public final ElkNode inputGraph;
	public final LloydStep.Graph transformedGraph;
	public final Map<ElkNode,LloydStep.Node> transformationMap;
	public final List<LloydStep> lloydSteps=new ArrayList<>();
	
	public LloydRelaxation(ElkNode inputGraph) {
		this.inputGraph=inputGraph;
		List<LloydStep.Edge> edges = new ArrayList<>();
		List<LloydStep.Node> nodes = new ArrayList<>();
		transformationMap = new HashMap<>();
		for (var elkNode : inputGraph.getChildren()) {
			var node = new LloydStep.Node(elkNode.getIdentifier(), elkNode.getX(), elkNode.getY());
			transformationMap.put(elkNode,node);
			nodes.add(node);
		}
		for (var edge : inputGraph.getContainedEdges())
			edges.add(new LloydStep.Edge(transformationMap.get((ElkNode)edge.getSources().get(0)),transformationMap.get((ElkNode)edge.getTargets().get(0))));
		this.transformedGraph = new LloydStep.Graph(nodes,edges);
	}

	public void computeSteps() {
		lloydSteps.add(new LloydStep(transformedGraph));
		//TODO: to be implemented
	}

}
