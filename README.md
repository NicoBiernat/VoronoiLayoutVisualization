# Voronoi Layout Algorithm Visualization

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

The architecture consists of a parser, which parses an input graph from a file and creates an ELK-Graph. After that, a conversion into our own internal graph format is carried out.  
The ELK-Force algorithm is executed on the graph before the Voronoi-Layout algorithm begins.  
We chose to implement Lloyd's algorithm ourselves to be able to better extract all the interesting intermediate steps.
We execute the whole algorithm and save all intermediate steps.
Those steps are then loaded into the model of our Model-View-Controller architecture, which can then interactively visualize all precalculated steps.
We might move the preprocessing (parsing, conversion and force-layout) into the MVC architecture such that the graph can be changed at runtime.


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

Only Some additions were made: The Delauney circles are also displayed as well as the ability to display substeps was added.

We wanted to let the user choose to either automatically step through the algorithm at a certain speed or manually step through it. We also implemented different levels of detail by letting the user enable sub-steps:  
(Input Graph -> Delaunay triangles -> Delaunay circumcircles -> Voronoi Edges -> Voronoi cells ->  Voronoi centroids -> displacment arrows)  
We also wanted to use to be able to select the Parts of the algorithm  to be displayed manually, which is realized by checkboxes enabling certain parts of the rendering 
