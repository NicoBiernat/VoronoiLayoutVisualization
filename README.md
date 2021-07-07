# Voronoi Layout Algorithm Visualization

## Table of contents
1. [How to start](#how-to-start)
2. [User guide](#userguide)
    1. [Writing a graph file](#writegraph)
    2. [Opening a graph file](#opengraph)
    3. [Animation control](#animationcontrol)
    4. [Display options](#displayoptions)
3. [Technical Details](#technical)  
    1. [Architecture](#architecture)  
    2. [Graph Input Format](#graphinputformat)
    3. [Internal Format](#internalformat)
4. [Visualization](#visualization)
    1. [Display Options](#visualization-displayoptions)
    2. [Substeps](#visualization-substeps)
    3. [Animation Control](#visualization-animationcontrol)

---

## How to start <a id="how-to-start"></a>
### Versions
- Java 11 (needed)
- Gradle 7.0.2 (included with Gradle Wrapper)

### Running
#### Linux / MacOS:  
```
./gradlew run
```
#### Windows:  
```
gradlew.bat run
```

### Building and Running as JAR
#### Linux / MacOS:
```
./gradlew shadowJar
java -jar ./build/libs/VoronoiLayoutVisualization-1.0-SNAPSHOT-all.jar
```
#### Windows:
```
gradlew.bat shadowJar
java -jar ./build/libs/VoronoiLayoutVisualization-1.0-SNAPSHOT-all.jar
```

### Dependencies (see build.gradle)
- ELK Graph
- ELK Force-directed algorithm

---

## User Guide <a id="userguide"></a>
### Writing a graph file <a id="writegraph"></a>
### Opening the graph file <a id="opengraph"></a>
### Animation control <a id="animationcontrol"></a>
### Display options <a id="displayoptions"></a>

---

## Technical Details <a id="technical"></a>
### Architecture <a id="architecture"></a>

![image](https://user-images.githubusercontent.com/25539263/122384258-cea06300-cf6b-11eb-87bb-e49952ab9222.png)

The software architecture is based on the Model-View-Controller pattern. With this pattern the concerns of storing and manipulating the data (model) is separated from the visualization (view) and the user input handling is separated from that as well (controller).  

#### Model
The model in this application mainly stores the output of Lloyd's-Algorithm, which is a list of all steps the algorithm calculated. It also contains indices for safely stepping through this list in steps and sub-steps as well as the currently selected input file.  Everytime the model is updated (automatically or through user input), it notifies the view(s) that they should update as well. For this reason there exists a single-method interface `View` in the view-package, which is used by the model.  
We designed the model to be a Singleton because all the passing around of the model instance makes the code difficult to understand and there really only needs to be one model instance.

#### View
The view is separated into smaller components such as input groups, which are then composed in the `MainView`.  
The components are `AnimationControl` with buttons for play/pause, next and previous as well as sliders for progress and speed, `Canvas` the main drawing area, `DesignChooser`  a dropdown menu for choosing the UI theme, `DisplayOptions` with several checkboxes for enabling drawing features, `FileControl` for choosing and showing the input graph file and `Header` which contains explanations for the sub-steps.  
Some components only visualize data such as the `Canvas` but others contain user input elements such as the `AnimationControl`. Visualizing components are updated by the model when a change happens and components with user input register corresponding event handlers from the Controller package.  

#### Controller
We decided to alter the classical MVC-Pattern in the controller package.  
Our controller is not a single class but rather a collection of event handlers, one for each view component that has user input elements.  
The `AnimationController` has inner classes for every button and slider in the `AnimationControl` view component as this simplifies the event handling in comparison to checking the source component or using the "actionCommand" property.  
The other controllers are the `DesignChooserController` which handles the dropdown menu, the `DisplayOptionsController` which handles one checkbox (the corresponding DisplayOption is passed in the contructor of this controller) and the `FileController` that handles the "Open file" button and opens a file chooser popup.  

#### Parser and Algorithm
Whenever a new file is loaded using the "Open file" button, the parser tries to parse this file and create an ElkGraph (with random node positions) from it. If this was successful, we execute the force-directed layout algorithm from ELK to get an initial layout.  
After that, the ElkGraph is converted into our own internal datastructure which is easier to work with and has extra types and fields for Voronoi layouting.  
With the graph now pre-layouted and converted into the internal format, our own implementation of Lloyd's algorithm for Voronoi layouting is executed.  
It performs the complete `LloydRelaxation` and records all intermediate steps in a list of `LloydStep`s.


### Graph Input Format <a id="graphinputformat"></a>
Our graph input format is a subset of the "elkt" DSL.  
The file must only contain lines of the form  
`node <id>`  
or  
`edge <id> -> <id>`  
where `<id>` is an arbitrary identifier for a node (without spaces).
Formally, each line must either be empty (containing only whitespace) or match one of the following RegExs:  
`^\s*(node)\s+(\S*)\s*$`
or `^\s*(edge)\s+(\S+)\s*->\s*(\S+)\s*$`

example: 
```
node n1
node n2
node n3
edge n1 -> n2
edge n2 -> n3
```

### Internal Graph Format <a id="internalformat"></a>

![image](https://user-images.githubusercontent.com/25539263/122387810-8a16c680-cf6f-11eb-8dd7-cb5b5434af54.png)

We decided to use our own graph format. We thought that this might be easier and quicker to use than ELK-Graphs, leaving out some properties of an ELK-Graph like ports and hyperedges, and adding others like `DelaunayTriangle`s and `VoronoiCell`s.  
During the algorithms run, a list of `LloydStep`s is collected. Each `LloydStep` contains the graph before executing the step, and calculates a list of `DelaunayTriangle` which forms a [Delaunay triangulation](https://en.wikipedia.org/wiki/Delaunay_triangulation) of the input graph and using the triangulation a list of `VoronoiCell`, which each holds an association to the input `Node` they correspond to. 

--- 

## Visualization <a id="visualization"></a>

Our first idea for visualizing the Voronoi layout algorithm was to either have a set of display options that the user can toggle to see more or less detail or let the application decide what level of detail the user should see at any moment of the animation.  
But then we thought: Why not both?
So we opted for a "manual mode" where the user can freely toggle all available display options as well as an "automatic mode" where the application divides each step of the algorithm in smaller sub-steps and activates appropriate display options for that sub-step such that the user only needs to step through. The "automatic mode" is enabled by a special display option called "Enable substeps" and also features explanations below the graph for every sub-step.  

### Display Options <a id="visualization-displayoptions"></a>
We wanted to show every important step in Lloyd's algorithm, so we implemented display options for the input graph (of the current step) separate for nodes and edges, for the edges of the Delaunay triangulation and the circumscribed circles as well as for the Voronoi edges, nodes and centroids of the Voronoi cells. We also added an option that shows how the graph nodes are relocated to the Voronoi centroids.  
We then hard-coded a sensible sequence of those display options as our sub-step options to explain what the algorithm does in each step.  

### Automatic Sub-steps <a id="visualization-substeps"></a>
First, the input graph (of the step) is shown with nodes and edges. Then, the edges disappear to underline the fact that this algorithm only operates on the nodes.  
The next step adds the edges created by the Delaunay triangulation and then the circumscribed circles are added to show which circle corresponds to which triangle.  

In the following step the Delaunay edges vanish and the Voronoi nodes are added. This shows that the Voronoi nodes are located in the center of the circles. The circles are then removed and the Voronoi edges are drawn by connecting the Voronoi nodes. Efforts were made to get clipping and combination of outer Voronoi edges right. We also show the Delaunay edges again in this step, so the user sees that each Voronoi edge intersects (or would intersect) a Delaunay edge exactly in the middle.  
After that, the Delaunay edges are removed once more and the centroids of the Voronoi cells are shown.  
In the last step, the Voronoi cells are removed and only the graph nodes, Voronoi cells centroids and arrows connecting the nodes to the centroids are displayed in order to show how the node position is updated before the next step.  

### Animation control <a id="visualization-animationcontrol"></a>
We wanted the user to have full control over the playback of the animation.
Therefore we added "forward" and "backward" buttons, "skip to end" and "skip to beginning" buttons as well as a slider input.  
For automatic playback the user can use the "play/pause" button and set the playback speed using the speed slider.  
With those options the user can step through the algorithm manually, revisit previous steps or even set the speed very high and watch the algorithm smoothly layout the nodes.