package model;

import algorithm.LloydStep;
import view.View;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Model {

  private List<LloydStep> lloydSteps;
  private LloydStep.Graph inputGraph;
  private int index = -1;

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
    if (index == lloydSteps.size() - 1) return lloydSteps.get(index);
    index++;
    var step = lloydSteps.get(index);
    updateViews();
    return step;
  }

  public LloydStep previousStep() {
    if (index == -1) return lloydSteps.get(0);
    index--;
    updateViews();
    if (index < 0) {
      return lloydSteps.get(0);
    }
    return lloydSteps.get(index);
  }

  public LloydStep getCurrentStep() {
    if (index < 0) return null;
    return lloydSteps.get(index);
  }

  public LloydStep.Graph getInputGraph() {
    return inputGraph;
  }

  public int getIndex() {
    return index;
  }
}
