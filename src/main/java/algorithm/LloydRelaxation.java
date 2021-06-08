package algorithm;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.graph.ElkNode;

public class LloydRelaxation {
	public final ElkNode inputGraph;
	public final List<LloydStep> lloydSteps=new ArrayList<LloydStep>();
	
	public LloydRelaxation(ElkNode inputGraph) {
		this.inputGraph=inputGraph;
	}

	public void computeSteps() {
		lloydSteps.add(new LloydStep(inputGraph));
		//TODO: to be implemented
	}

}
