package model;

import algorithm.LloydRelaxation;
import algorithm.LloydStep;
import org.eclipse.elk.alg.force.options.ForceMetaDataProvider;
import org.eclipse.elk.alg.force.options.ForceOptions;
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import parser.Parser;
import view.View;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Model {



  private File selectedFile;

  private List<LloydStep> lloydSteps;
  private LloydStep.Graph inputGraph;

  private int index = -1;
  private int substepIndex = 0;

  private Set<View> views = new HashSet<>();

  public File getSelectedFile() {
    return selectedFile;
  }

  public Model(List<LloydStep> lloydSteps, LloydStep.Graph inputGraph) {
    this.lloydSteps = lloydSteps;
    this.inputGraph = inputGraph;
  }

  public void loadFile(File file){
    this.selectedFile = file;

    ElkNode testGraph;
    try {
      testGraph = Parser.parse(this.selectedFile.getPath());
    } catch (IOException e) {
      System.err.println("Could not read or parse file: " + this.selectedFile.getPath());
      e.printStackTrace();
      return;
    }
    System.out.println("Parsed graph:");
    List<ElkNode> nodes = testGraph.getChildren();
    /*for (ElkNode n : nodes) {
      System.out.println(n.getIdentifier());
    }
    for (ElkEdge e : testGraph.getContainedEdges()) {
      System.out.println(e.getSources().get(0).getIdentifier() + " -> " + e.getTargets().get(0).getIdentifier());
    }*/

    System.out.println("Running force based algorithm");
    testGraph.setProperty(CoreOptions.ALGORITHM, ForceOptions.ALGORITHM_ID);
    testGraph.setProperty(CoreOptions.SPACING_NODE_NODE, 10000.0);
    testGraph.setProperty(ForceMetaDataProvider.ITERATIONS, 10000);
    RecursiveGraphLayoutEngine layoutEngine = new RecursiveGraphLayoutEngine();
    layoutEngine.layout(testGraph, new BasicProgressMonitor());
    System.out.println("Force based layout done");

    System.out.println("Transforming ElkGraph into own format");
    LloydRelaxation algorithm = new LloydRelaxation(testGraph);
    algorithm.computeSteps();

    this.lloydSteps= algorithm.lloydSteps;
    this.inputGraph= algorithm.transformedGraph;

    for (var option : Model.DISPLAY_OPTIONS) {
      this.displayOptions.put(option,true); //initialize all display options with being ticked
    }

    updateViews();
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

  public void nextStepOrSubstep() {
    if (index >= 0 && displayOptions.getOrDefault(DISPLAY_OPTIONS[6], false)) { // substeps enabled
      substepIndex++;
      if (substepIndex >= substepOptions.size()) {
        substepIndex = 0;
        nextStep();
      }
      substepOptions.get(substepIndex).forEach(this::setDisplayOption);
//      displayOptions.forEach((option, enabled) -> System.out.println(option + ": " + enabled));
    } else {
      substepIndex = 0;
      nextStep();
    }
    updateViews();
//    System.out.println("Step: " + index + " Substep: " + substepIndex);
//    System.out.println();
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

  public void previousStepOrSubstep() {
    if (index >= 0 && displayOptions.getOrDefault(DISPLAY_OPTIONS[6], false)) { // substeps enabled
      substepIndex--;
      if (substepIndex < 0) {
        substepIndex = substepOptions.size() - 1;
        previousStep();
      }
      substepOptions.get(substepIndex).forEach(this::setDisplayOption);
//      displayOptions.forEach((option, enabled) -> System.out.println(option + ": " + enabled));
    } else {
      substepIndex = 0;
      previousStep();
    }
    updateViews();
//    System.out.println("Step: " + index + " Substep: " + substepIndex);
//    System.out.println();
  }

  public void firstStep() {
    index=-1;
    substepIndex = 0;
    updateViews();
  }

  public void lastStep() {
    index=lloydSteps.size()-1;
    substepIndex = 0;
    updateViews();
  }

  private Thread playingThread;

  //TODO this is not pretty but it works, maybe reuse threads, and make everything more thread safe i guess
  public void playSteps() {
    if (playingThread==null){
      playingThread = new Thread(() -> {
        while (playingThread!=null && index<lloydSteps.size()-1){
          nextStepOrSubstep();
          try {
            //TODO implement interpolation
            //TODO implement substeps
            Thread.sleep(playBackSpeed);
          } catch (InterruptedException e) {
            //swallow
          }
          updateViews();
        }
        playingThread=null;
        updateViews();
      });
      playingThread.start();
    }
  }
  public boolean isPlayingSteps(){
    return playingThread!=null;
  }

  public void pauseSteps() {
    if (playingThread!=null) {
      playingThread = null;
      updateViews();
    }
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

  public int getSubstepIndex() {
    return substepIndex;
  }

  public int getNumSubsteps() {
    return substepOptions.size();
  }

  int playBackSpeed=(1000/(50+1));

  public void setPlaybackSpeed(int newSpeed) {
    playBackSpeed=newSpeed;
  }

  public void setStep(int step) {
    if (lloydSteps == null) return;
    if (step<-1) index=-1;
    else if (step>lloydSteps.size()-1) index=lloydSteps.size()-1;
    else index=step;
    updateViews();
  }

  private Map<String,Boolean> displayOptions = new HashMap<>();
  public void setDisplayOption(String option, boolean value) {
    displayOptions.put(option,value);
    updateViews();
  }

  public static final String[] DISPLAY_OPTIONS={"Graph Nodes", "Graph Edges", "Delaunay Edges", "Voronoi Edges", "Voronoi Nodes", "Voronoi Centroids", "Enable Substeps"};

  public Map<String, Boolean> getDisplayOptions() {
    return displayOptions;
  }

  /*
  0 -> show nodes + edges
  1 -> show nodes
  2 -> show nodes + delaunay edges
  3 -> show nodes + delaunay edges + voronoi edges
  4 -> show nodes + voronoi edges + voronoi nodes
  5 -> show nodes + voronoi edges + voronoi nodes + voronoi centroids
  6 -> show nodes + voronoi centroids + arrows
   */
  private final List<Map<String, Boolean>> substepOptions = Arrays.asList(
          Map.of(DISPLAY_OPTIONS[0], true,
                  DISPLAY_OPTIONS[1], true,
                  DISPLAY_OPTIONS[2], false,
                  DISPLAY_OPTIONS[3], false,
                  DISPLAY_OPTIONS[4], false,
                  DISPLAY_OPTIONS[5], false,
                  DISPLAY_OPTIONS[6], true),  // nodes and edges
          Map.of(DISPLAY_OPTIONS[0],true,
                  DISPLAY_OPTIONS[1], false,
                  DISPLAY_OPTIONS[2], false,
                  DISPLAY_OPTIONS[3], false,
                  DISPLAY_OPTIONS[4], false,
                  DISPLAY_OPTIONS[5], false,
                  DISPLAY_OPTIONS[6], true),                               // nodes
          Map.of(DISPLAY_OPTIONS[0], true,
                  DISPLAY_OPTIONS[1], false,
                  DISPLAY_OPTIONS[2], true,
                  DISPLAY_OPTIONS[3], false,
                  DISPLAY_OPTIONS[4], false,
                  DISPLAY_OPTIONS[5], false,
                  DISPLAY_OPTIONS[6], true), // nodes and delaunay edges
          Map.of(DISPLAY_OPTIONS[0], true,
                  DISPLAY_OPTIONS[1], false,
                  DISPLAY_OPTIONS[2], true,
                  DISPLAY_OPTIONS[3], true,
                  DISPLAY_OPTIONS[4], false,
                  DISPLAY_OPTIONS[5], false,
                  DISPLAY_OPTIONS[6], true), // nodes, delaunay edges and voronoi edges
          Map.of(DISPLAY_OPTIONS[0], true,
                  DISPLAY_OPTIONS[1], false,
                  DISPLAY_OPTIONS[2], false,
                  DISPLAY_OPTIONS[3], true,
                  DISPLAY_OPTIONS[4], true,
                  DISPLAY_OPTIONS[6], true), // nodes, voronoi edges and voronoi nodes
          Map.of(DISPLAY_OPTIONS[0], true,
                  DISPLAY_OPTIONS[1], false,
                  DISPLAY_OPTIONS[2], false,
                  DISPLAY_OPTIONS[3], true,
                  DISPLAY_OPTIONS[4], true,
                  DISPLAY_OPTIONS[5], true,
                  DISPLAY_OPTIONS[6], true), // nodes, voronoi edges, voronoi nodes and voronoi centroids
          Map.of(DISPLAY_OPTIONS[0], true,
                  DISPLAY_OPTIONS[1], false,
                  DISPLAY_OPTIONS[2], false,
                  DISPLAY_OPTIONS[3], false,
                  DISPLAY_OPTIONS[4], false,
                  DISPLAY_OPTIONS[5], true,
                  DISPLAY_OPTIONS[6], true) // nodes and voronoi centroids
  );
}
