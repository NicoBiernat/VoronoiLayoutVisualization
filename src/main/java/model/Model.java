package model;

import algorithm.datastructure.Graph;
import algorithm.LloydRelaxation;
import algorithm.LloydStep;
import org.eclipse.elk.alg.force.options.ForceMetaDataProvider;
import org.eclipse.elk.alg.force.options.ForceOptions;
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.graph.ElkNode;
import parser.Parser;
import view.View;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Model {

    public static final Model INSTANCE = new Model();

    private File selectedFile;

    private List<LloydStep> lloydSteps;
    private Graph inputGraph;

    private int index = -1;
    private int substepIndex = 0;

    private final Set<View> views = new HashSet<>();

    public File getSelectedFile() {
        return selectedFile;
    }

    public void loadFile(File file) {
        this.selectedFile = file;

        ElkNode testGraph;
        try {
            testGraph = Parser.parse(this.selectedFile.getPath());
        } catch (IOException e) {
            System.err.println("Could not read or parse file: " + this.selectedFile.getPath());
            e.printStackTrace();
            return;
        }
        loadGraph(testGraph);
    }

    public void loadGraph(ElkNode graph) {
//    graph.setProperty(CoreOptions.ALGORITHM, StressOptions.ALGORITHM_ID);
        graph.setProperty(CoreOptions.ALGORITHM, ForceOptions.ALGORITHM_ID);
        graph.setProperty(CoreOptions.SPACING_NODE_NODE, 10000.0);
        graph.setProperty(ForceMetaDataProvider.ITERATIONS, 10000);
        RecursiveGraphLayoutEngine layoutEngine = new RecursiveGraphLayoutEngine();
        layoutEngine.layout(graph, new BasicProgressMonitor());

        LloydRelaxation algorithm = new LloydRelaxation(graph);
        algorithm.computeSteps();

        this.lloydSteps = algorithm.lloydSteps;
        this.inputGraph = algorithm.transformedGraph;

        for (var option : DisplayOptions.values()) {
            this.displayOptions.put(option, true); //initialize all display options with being ticked
        }
        firstStep();
        updateViews();
    }

    public void registerView(View v) {
        views.add(v);
        v.update();
    }

    public void updateViews() {
        views.forEach(View::update);
    }

    public List<LloydStep> getLloydSteps() {
        return lloydSteps;
    }

    public void nextStep() {
        if (index == lloydSteps.size() - 1) return;
        index++;
        updateViews();
    }

    public void nextStepOrSubstep() {
        if (displayOptions.getOrDefault(DisplayOptions.ENABLE_SUBSTEPS, false)) { // substeps enabled
            if (index >= 0) {
                substepIndex++;
                if (substepIndex >= substepOptions.size()) {
                    substepIndex = index < lloydSteps.size() - 1 ? 0 : substepOptions.size() - 1;
                    nextStep();
                }
            } else {
                substepIndex = 0;
                nextStep();
            }
            substepOptions.get(substepIndex).forEach(this::setDisplayOption);
        } else {
            substepIndex = 0;
            nextStep();
        }
        updateViews();
    }

    public void previousStep() {
        if (index == -1) return;
        index--;
        updateViews();
    }

    public void previousStepOrSubstep() {
        if (displayOptions.getOrDefault(DisplayOptions.ENABLE_SUBSTEPS, false)) { // substeps enabled
            if (index >= 0) {
                substepIndex--;
                if (substepIndex < 0) {
                    substepIndex = substepOptions.size() - 1;
                    previousStep();
                }
            } else {
                substepIndex = substepOptions.size() - 1;
                previousStep();
            }
            substepOptions.get(substepIndex).forEach(this::setDisplayOption);
        } else {
            substepIndex = 0;
            previousStep();
        }
        if (index < 0) substepIndex = 0;
        updateViews();
    }

    public void firstStep() {
        index = -1;
        substepIndex = 0;
        updateViews();
    }

    public void lastStep() {
        index = lloydSteps.size() - 1;
        substepIndex = substepOptions.size() - 1;
        updateViews();
    }

    public Thread playingThread;

    public void playSteps() {
        if (playingThread == null) {
            playingThread = new Thread(() -> {
                while (playingThread != null && (index < lloydSteps.size() - 1 || substepIndex < substepOptions.size() - 1)) {
                    nextStepOrSubstep();
                    try {
                        boolean substepsEnabled = displayOptions.getOrDefault(DisplayOptions.ENABLE_SUBSTEPS, false);
                        Thread.sleep(substepsEnabled ? (long) (playBackSpeed * 5.0) : playBackSpeed);
                    } catch (InterruptedException e) {
                        //swallow
                    }
                    updateViews();
                }
                playingThread = null;
                updateViews();
            });
            playingThread.start();
        }
    }

    public boolean isPlayingSteps() {
        return playingThread != null;
    }

    public void pauseSteps() {
        if (playingThread != null) {
            playingThread = null;
            updateViews();
        }
    }

    public LloydStep getCurrentStep() {
        if (index < 0) return null;
        return lloydSteps.get(index);
    }

    public Graph getInputGraph() {
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

    int playBackSpeed = (1000 / (50 + 1));

    public void setPlaybackSpeed(int newSpeed) {
        playBackSpeed = newSpeed;
    }

    public void setStep(int step) {
        if (lloydSteps == null) return;
        if (step < -1) index = -1;
        else if (step > lloydSteps.size() - 1) index = lloydSteps.size() - 1;
        else index = step;
        updateViews();
    }

    private final Map<DisplayOptions, Boolean> displayOptions = new HashMap<>();

    public void setDisplayOption(DisplayOptions option, boolean value) {
        displayOptions.put(option, value);

        // enabling/disabling substeps enables/disables other options
        if (option.equals(DisplayOptions.ENABLE_SUBSTEPS)) {
            if (value) { // enabled -> enable options of substep
                substepOptions.get(substepIndex).entrySet().stream()
                        .filter(e -> !e.getKey().equals(DisplayOptions.ENABLE_SUBSTEPS))
                        .forEach(e -> setDisplayOption(e.getKey(), e.getValue()));
            } else { // disabled -> enable all options
                Arrays.stream(DisplayOptions.values())
                        .filter(o -> !o.equals(DisplayOptions.ENABLE_SUBSTEPS))
                        .forEach(o -> setDisplayOption(o, true));
            }
        }
        updateViews();
    }

    public Map<DisplayOptions, Boolean> getDisplayOptions() {
        return displayOptions;
    }

    private final List<Map<DisplayOptions, Boolean>> substepOptions = Arrays.asList(
            Map.of(DisplayOptions.GRAPH_NODES, true,
                    DisplayOptions.GRAPH_EDGES, true,
                    DisplayOptions.DELAUNAY_CIRCLES, false,
                    DisplayOptions.DELAUNAY_EDGES, false,
                    DisplayOptions.VORONOI_EDGES, false,
                    DisplayOptions.VORONOI_NODES, false,
                    DisplayOptions.VORONOI_CENTROIDS, false,
                    DisplayOptions.SHOW_NODE_DISPLACEMENT, false,
                    DisplayOptions.ENABLE_SUBSTEPS, true),  // nodes and edges
            Map.of(DisplayOptions.GRAPH_NODES, true,
                    DisplayOptions.GRAPH_EDGES, false,
                    DisplayOptions.DELAUNAY_CIRCLES, false,
                    DisplayOptions.DELAUNAY_EDGES, false,
                    DisplayOptions.VORONOI_EDGES, false,
                    DisplayOptions.VORONOI_NODES, false,
                    DisplayOptions.VORONOI_CENTROIDS, false,
                    DisplayOptions.SHOW_NODE_DISPLACEMENT, false,
                    DisplayOptions.ENABLE_SUBSTEPS, true), // nodes
            Map.of(DisplayOptions.GRAPH_NODES, true,
                    DisplayOptions.GRAPH_EDGES, false,
                    DisplayOptions.DELAUNAY_CIRCLES, false,
                    DisplayOptions.DELAUNAY_EDGES, true,
                    DisplayOptions.VORONOI_EDGES, false,
                    DisplayOptions.VORONOI_NODES, false,
                    DisplayOptions.VORONOI_CENTROIDS, false,
                    DisplayOptions.SHOW_NODE_DISPLACEMENT, false,
                    DisplayOptions.ENABLE_SUBSTEPS, true),  // nodes and delaunay edges
            Map.of(DisplayOptions.GRAPH_NODES, true,
                    DisplayOptions.GRAPH_EDGES, false,
                    DisplayOptions.DELAUNAY_CIRCLES, true,
                    DisplayOptions.DELAUNAY_EDGES, true,
                    DisplayOptions.VORONOI_EDGES, false,
                    DisplayOptions.VORONOI_NODES, false,
                    DisplayOptions.VORONOI_CENTROIDS, false,
                    DisplayOptions.SHOW_NODE_DISPLACEMENT, false,
                    DisplayOptions.ENABLE_SUBSTEPS, true),  // nodes, delaunay circles and delaunay edges
            Map.of(DisplayOptions.GRAPH_NODES, true,
                    DisplayOptions.GRAPH_EDGES, false,
                    DisplayOptions.DELAUNAY_CIRCLES, true,
                    DisplayOptions.DELAUNAY_EDGES, false,
                    DisplayOptions.VORONOI_EDGES, false,
                    DisplayOptions.VORONOI_NODES, true,
                    DisplayOptions.VORONOI_CENTROIDS, false,
                    DisplayOptions.SHOW_NODE_DISPLACEMENT, false,
                    DisplayOptions.ENABLE_SUBSTEPS, true), // nodes, delaunay circles and voronoi nodes
            Map.of(DisplayOptions.GRAPH_NODES, true,
                    DisplayOptions.GRAPH_EDGES, false,
                    DisplayOptions.DELAUNAY_CIRCLES, false,
                    DisplayOptions.DELAUNAY_EDGES, true,
                    DisplayOptions.VORONOI_EDGES, true,
                    DisplayOptions.VORONOI_NODES, true,
                    DisplayOptions.VORONOI_CENTROIDS, false,
                    DisplayOptions.SHOW_NODE_DISPLACEMENT, false,
                    DisplayOptions.ENABLE_SUBSTEPS, true), // nodes, delaunay edges, voronoi edges and voronoi nodes
            Map.of(DisplayOptions.GRAPH_NODES, true,
                    DisplayOptions.GRAPH_EDGES, false,
                    DisplayOptions.DELAUNAY_CIRCLES, false,
                    DisplayOptions.DELAUNAY_EDGES, false,
                    DisplayOptions.VORONOI_EDGES, true,
                    DisplayOptions.VORONOI_NODES, true,
                    DisplayOptions.VORONOI_CENTROIDS, true,
                    DisplayOptions.SHOW_NODE_DISPLACEMENT, false,
                    DisplayOptions.ENABLE_SUBSTEPS, true), // nodes, voronoi edges, voronoi nodes and voronoi centroids
            Map.of(DisplayOptions.GRAPH_NODES, true,
                    DisplayOptions.GRAPH_EDGES, false,
                    DisplayOptions.DELAUNAY_CIRCLES, false,
                    DisplayOptions.DELAUNAY_EDGES, false,
                    DisplayOptions.VORONOI_EDGES, false,
                    DisplayOptions.VORONOI_NODES, false,
                    DisplayOptions.VORONOI_CENTROIDS, true,
                    DisplayOptions.SHOW_NODE_DISPLACEMENT, true,
                    DisplayOptions.ENABLE_SUBSTEPS, true) // nodes, voronoi centroids and displacement arrows
    );
}
