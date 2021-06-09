package algorithm;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.graph.ElkNode;

public class LloydRelaxation {
	public final ElkNode inputGraph;
	public final Graph transformedGraph;
	public final List<LloydStep> lloydSteps=new ArrayList<>();
	
	public LloydRelaxation(ElkNode inputGraph) {
		this.inputGraph=inputGraph;
		this.transformedGraph=null;
	}

	public void computeSteps() {
		lloydSteps.add(new LloydStep(transformedGraph));
		//TODO: to be implemented
	}

}
