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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Model {



  private File selectedFile;

  private List<LloydStep> lloydSteps;
  private LloydStep.Graph inputGraph;

  private int index = -1;

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

  public LloydStep previousStep() {
    if (index == -1) return lloydSteps.get(0);
    index--;
    updateViews();
    if (index < 0) {
      return lloydSteps.get(0);
    }
    return lloydSteps.get(index);
  }


  public void firstStep() {
    index=0;
    updateViews();
  }

  public void lastStep() {
    index=lloydSteps.size()-1;
    updateViews();
  }

  private Thread playingThread;

  //TODO this is not pretty but it works, maybe reuse threads, and make everything more thread safe i guess
  public void playSteps() {
    if (playingThread==null){
      playingThread = new Thread(() -> {
        while (playingThread!=null && index<lloydSteps.size()-1){
          index++;
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
  int playBackSpeed=(1000/(50+1));

  public void setPlaybackSpeed(int newSpeed) {
    playBackSpeed=newSpeed;
  }

  public void setStep(int step) {
    if (step<-1) index=-1;
    else if (step>lloydSteps.size()-1) index=lloydSteps.size()-1;
    else index=step;
    updateViews();
  }

  private HashMap<String,Boolean> displayOptions = new HashMap<>();
  public void setDisplayOption(String option, boolean value) {
    displayOptions.put(option,value);
    updateViews();
  }

  public static final String[] DISPLAY_OPTIONS={"Graph Nodes", "Graph Edges", "Delaunay Edges", "Voronoi Edges", "Voronoi Nodes", "Voronoi Centroids"};

  public HashMap<String, Boolean> getDisplayOptions() {
    return displayOptions;
  }
}
