# Voronoi Layout Algorithm Visualization

### by Nico Biernat, Corvin Fischer Rivera and Alexander Seidler (Group B)

## Table of contents
1. [How to start](#how-to-start)
2. [User guide](#userguide)
    1. [Opening a graph file](#opengraph)
    2. [Display options](#displayoptions)
    3. [Animation control](#animationcontrol)
    4. [Substeps](#substeps)
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

<!-- ## Current Progress

![image](https://user-images.githubusercontent.com/25539263/122387067-c564c580-cf6e-11eb-8b23-d7b2150ed2f8.png)

We implemented a parser for our graph input format, Lloyd's algorithm for Voronoi layouting (although there might be some bugs or edge cases left), a Model-View-Controller architecture for the interactive application as well as a visualization of the input graph (after force layout) and for each major step (Delaunay triangles + Voronoi cells) in Lloyd's algorithm. There currently are two buttons to step forward or backward through the algorithm. -->

## User Guide <a id="userguide"></a>
![image](https://user-images.githubusercontent.com/79837801/124779170-d7b89a80-df41-11eb-844e-02b893c9e61f.png)

### Opening the graph file <a id="opengraph"></a>
![image](https://user-images.githubusercontent.com/79837801/124780326-cb810d00-df42-11eb-8d5e-9a9f337cedd9.png)

When the Program is first started, no graph is loaded. To load a graph, click on the button with the label "Open file" in the section "File Control".
This will open a new window in which an ELKT file can be selected and opened. After the File is opened, 
the graph will be displayed on the canvas and the file name next to the "Open file" button.

### Display options <a id="displayoptions"></a>

When clicking on the first element in the "Display Options" section labeled "UI Design" a drop-down menu opens with options for the style of the UI.
There are five different options namely "Metal", "Nimbus", "CDE/Motif", "Windows" and "Windows Classic" with "Metal" being the default.
Under that, there are nine checkboxes. The first one of which is used to enable or disable the substeps of the algorithm.
The other ones are used to enable or disable different elements of the visualization.
By default, the substeps are enabled. While this is the case, the program will automatically enable and disable the relevant elements for the current substep.
In this mode, the checkboxes for the different elements can not be manually set. To do that, the substeps must be disabled.

The first two options after the substeps are "Graph nodes" and "Graph edges". They are used to enabling and disable the nodes in red and edges in gray of the graph specified in the ELKT file.
The combination of these two options can be used to see how the nodes of the graph move during the algorithm.

![image](https://user-images.githubusercontent.com/79837801/124802333-24a76b80-df58-11eb-8f7c-b921016ce75f.png)

The "Delaunay circles" and "Delaunay edges" show the triangulation of the nodes in yellow and the circumscribed circles light green around those triangles.

![image](https://user-images.githubusercontent.com/79837801/124804554-b617dd00-df5a-11eb-8f47-f2600624f30d.png)

The Delaunay edges can be used as an alternative to the graph edges for connecting the graph nodes.
This works especially well in combination with "Voronoi Edges" and "Voronoi nodes" enabled. Both the nodes and edges are blue.
The Voronoi nodes and edges form cells around the graph nodes. These are the Voronoi cells.

![image](https://user-images.githubusercontent.com/79837801/124805813-47d41a00-df5c-11eb-895b-908dfc836fba.png)

The last two options "Voronoi centroids" and "Show node displacement" enable the centroid of each Voronoi cell in light blue and an arrow from the graph nodes to the corresponding centroid.
They represent the movement of the nodes in each step.

![image](https://user-images.githubusercontent.com/79837801/124808411-34767e00-df5f-11eb-9386-0fac6cf16464.png)


### Animation control <a id="animationcontrol"></a>

Under The section "Animation Control" two sliders as well as five buttons can be found.
The first slider labeled "Speed" is used to set the time interval between steps while the animation is running.
To start the animation, the button in the middle is used. If pressed, the program will automatically advance through the different substeps of the algorithm until paused by pressing the button again or the algorithm is finished.
The two buttons to the left and right of the play button can be used to manually advance through the algorithm.
If substeps are enabled, they will move one substep at a time. Otherwise, they will just go through the steps of the algorithm.
To get to the first or last step of the algorithm quickly, the last two buttons can be used.
Another way to easily move through the steps is to use the second slider.
In order to know at which point in the algorithm the graph is at the moment, the current step as well as substep if enabled is displayed.

### Substeps <a id="substeps"></a>
One step of the algorithm can be subdivided into seven substeps.

#### Step 0
![image](https://user-images.githubusercontent.com/79837801/124794793-acd54300-df4f-11eb-9566-82003e938013.png)

The program includes a step zero to show the full graph between the steps of the algorithm in order to get a better feeling of how the graph looks 
throughout the algorithm.

#### Step 1
![image](https://user-images.githubusercontent.com/79837801/124796192-2cafdd00-df51-11eb-9eea-2acd2dccf2cf.png)

The First step removes the edges from the graph, leaving only the nodes, since the edges are not used in the calculations of the algorithm.

#### Step 2
![image](https://user-images.githubusercontent.com/79837801/124796967-076f9e80-df52-11eb-87d6-87e91782df2c.png)

In the second step, the nodes are triangulated and the resulting Delaunay edges are displayed.

#### Step 3
![image](https://user-images.githubusercontent.com/79837801/124797743-e8bdd780-df52-11eb-80ec-6c1b26e3d543.png)

In this step, the circumscribed circles to the corresponding Delaunay triangles are calculated and added to the canvas.
These circles are the Delaunay circles.

#### Step 4
![image](https://user-images.githubusercontent.com/79837801/124798256-71d50e80-df53-11eb-816b-af80c5eadc80.png)

Step four removes the Delaunay edges and replaces them with the centers of the Delaunay circles. The centers are the Voronoi nodes.

#### Step 5
![image](https://user-images.githubusercontent.com/79837801/124798914-39820000-df54-11eb-9aa6-273d607fe94d.png)

Starting from the Voronoi nodes added in step four, the Voronoi edges move orthogonally through the Delaunay edges and get clipped at the border of the canvas
or if they intersect one another.
To make this better visible, the Delauney edges get displayed again and the Delaunay circles are removed.

#### Step 6
![image](https://user-images.githubusercontent.com/79837801/124799921-5b2fb700-df55-11eb-9d9d-a707931fc6df.png)

For step six, the Delaunay edges are removed again and replaced by the centroids of the Voronoi cells.

#### Step 7
![image](https://user-images.githubusercontent.com/79837801/124800542-0ccee800-df56-11eb-88da-a6793a43f61e.png)

In the final step, the Voronoi nodes and edges are removed and instead arrows point from each node to the corresponding Voronoi centroid it will move to for the next step.

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

### Lloyd Implementation and Internal Graph Format<a id="internalformat"></a>
 
We decided to use our own graph format. We thought that this might be easier and quicker to use than ELK-Graphs, leaving out some properties of an ELK-Graph like ports and hyperedges, and adding others like `DelaunayTriangle`s and `VoronoiCell`s.  
During the algorithms run, a list of `LloydStep`s is collected. Each `LloydStep` contains the graph before executing the step and calculates a list of `DelaunayTriangle` which forms a [Delaunay triangulation](https://en.wikipedia.org/wiki/Delaunay_triangulation) of the input graph and using the triangulation a list of `VoronoiCell`, which each holds an association to the input `Node` they correspond to. 

The following picture illustrates the steps in the final version of our algorithm:

![image](https://user-images.githubusercontent.com/25539263/124821595-347e7a00-df6f-11eb-8569-0e6cc213746a.png)

We precompute all Lloyd relaxation steps after a file is loaded so that it is easier to play it back smoothly afterward and to properly separate the calculation from the display. The `LloydRelaxation` class is the main class of the algorithm which is instantiated with an `ElkNode` which has to be layouted in some way before. This `inputGraph` is then converted into the internal `Graph` format. The transformed Graph is then rescaled to ensure that it fits within the specified bounds before applying the relaxation. The `LloydStep` calculates the Voronoi cells associated with the given graph to enable the `LloydRelaxation` to perform one iteration of Lloyd's algorithm: The graph is cloned and the coordinates of the nodes are set to the coordinates of the centroids of the corresponding Voronoi cells. This moving of the Graphs nodes is repeated until every node moved less than a specified distance in one iteration.

In the LloydStep itself at first, a Delauney triangulation of the input nodes is calculated. This is done incrementally, based on http://paulbourke.net/papers/triangulate/: First, a large super triangle is created which encloses all nodes of the graph, then each node is added one by one to the previous triangles, now every triangle is removed for which the circumcircle includes the new node, violating the Delaunay property. Then new triangles are created to fill the hole. After all nodes are added, the super triangle is removed, and a Delauney triangulation is obtained. The resulting data structure `DelaunayTriangle` is a list of 3 `Edge`s respectively and is a superclass of `EdgeArc`: A list of `Edge`s enhanced by methods for computing circumcircles and centroids, etc. The formulas for those are based on https://www.geeksforgeeks.org/find-the-centroid-of-a-non-self-intersecting-closed-polygon/ and https://en.wikipedia.org/wiki/Circumscribed_circle#Cartesian_coordinates_2

From the Delauney triangulation, the Voronoi cells are calculated, for each edge in the triangulation there is a dual Voronoi edge, if an Edge belongs to two triangles it is internal and the corresponding Voronoi edge lays between the two centroids. If the Delauney edge only belongs to one triangle, the Voronoi edge must be extended from the centroid orthogonally thru the Delauney edge until it crosses with the bounding box. The obtained Voronoi Edge is stored in the `VoronoiCell`s associated with the graph node the cells belong to. `VoronoiCell` is also a superclass of `EdgeArc` to be able to compute centroids easily.

Then all open `VoronoiCell`s at the outer edge are closed using line segments connecting to the edges of the bounding box.

In the last step, the `VoronoiCell`s are clipped to the bounding box using the Sutherland Hodgman algorithm.


--- 

## Visualization <a id="visualization"></a>

Below the Visualization that we mocked in the beginning can be seen: 
![image](https://user-images.githubusercontent.com/25539263/122387462-2c827a00-cf6f-11eb-9953-42bdaabd4f55.png)

We implemented the visuals close to the designed visualization:
![image](https://user-images.githubusercontent.com/25539263/124823073-0ef27000-df71-11eb-8fc8-37b264631c99.png)

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