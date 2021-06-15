package model;

import algorithm.LloydStep;
import view.View;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Model {

  private List<LloydStep> lloydSteps;
  private LloydStep.Graph inputGraph;
  private int index = 0;

  private Set<View> views = new HashSet<>();

  public Model(List<LloydStep> lloydSteps, LloydStep.Graph inputGraph) {
    this.lloydSteps = lloydSteps;
    this.inputGraph = inputGraph;
  }

  public void registerView(View v) {
    views.add(v);
    v.update(this);
  }

  public void updateViews() {
    views.forEach(v -> v.update(this));
  }

  public List<LloydStep> getLloydSteps() {
    return lloydSteps;
  }

  public LloydStep nextStep() {
    return lloydSteps.get(++index);
  }

  public LloydStep previousStep() {
    return lloydSteps.get(--index);
  }

  public LloydStep getCurrentStep() {
    return lloydSteps.get(index);
  }

  public LloydStep.Graph getInputGraph() {
    return inputGraph;
  }

}
