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
### Writing a graph file <a id="writegraph"></a>
### Opening the graph file <a id="opengraph"></a>
### Animation control <a id="animationcontrol"></a>
### Display options <a id="displayoptions"></a>

---

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
During the algorithms run, a list of `LloydStep`s is collected. Each `LloydStep` contains the graph before executing the step, and calculates a list of `DelaunayTriangle` which forms a [Delaunay triangulation](https://en.wikipedia.org/wiki/Delaunay_triangulation) of the input graph and using the triangulation a list of `VoronoiCell`, which each holds an association to the input `Node` they correspond to. 

The following picture illustrates the steps in the final version of our algorithm:

![image](https://user-images.githubusercontent.com/25539263/124821595-347e7a00-df6f-11eb-8569-0e6cc213746a.png)

We precompute all lloyds relaxation steps after a file is loaded, so that its easier to play it back smoothly afterwards and in order to properly seperate the calculation from the display. The `LloydRelaxation` class is the main class of the algorithm which is instantiated with an `ElkNode` which has to be layouted in some way before. This `inputGraph` is then converted into the internal `Graph` format. The transformed Graph is then rescaled to ensure that it fits within the specified bounds before applying the relaxation. The `LloydStep` calculates the Voronoi cells associated with the given graph to enable the `LloydRelaxation` to perform one iteration of Lloyd's algorithm: The graph is cloned and the coordinates of the nodes are set to the coordiantes of the centroids of the corresponding Voronoi cells. This moving of the Graphs nodes is repeated until every node moved less than a specified distance in one iteration.

In the LloydStep itself at first a Delauney triangulation of the input nodes is calculated. This is done incrementally, based on http://paulbourke.net/papers/triangulate/: First a large super triangle is created which encloses all nodes of the graph, then each node is added one by one to the previous triangles, now every triangle is removed for which the circumcircle includes the new node, violating the delaunay property. Then new triangles are created to fill the hole. After all nodes are added, the super triangle is removed, and a delauney triangulation is obtained. The resulting datastructure `DelaunayTriangle` is a list of 3 `Edge`s respectively and is a superclass of `EdgeArc`: A list of `Edge`s enhanced by methods for computing circumcircles and centroids, etc. The formulas for those are based on https://www.geeksforgeeks.org/find-the-centroid-of-a-non-self-intersecting-closed-polygon/ and https://en.wikipedia.org/wiki/Circumscribed_circle#Cartesian_coordinates_2

Fromt he DelauneyTriangulation the Voronoi cells are calculated, for each edge in the triangulation there is a dual Voronoi edge, if an Edge belongs to two triangles it is internal and the coresponding Voronoi edge lays between th two centroids. If the Delauney edge only belongs to one triangle, the Voronoi edge must be extended from the centroid orthogonally thru the delauney edge until it crosses with the bounding box. The obtained Voronoi Edge is stored in the `VoronoiCell`s associated with the graph node the cells belong to. `VoronoiCell` is also a superclass of `EdgeArc` to be able to compute centroids easily.

Then all open `VoronoiCell`s at the outer edge are closed using line segments connecting to the edges of the bounding box.

In the Last step the `VoronoiCell`s are clipped to the bounding box using the Sutherland Hodgman algorithm.


--- 

## Visualization <a id="visualization"></a>

![image](https://user-images.githubusercontent.com/25539263/122387462-2c827a00-cf6f-11eb-9953-42bdaabd4f55.png)

We want to let the user choose to either automatically step through the algorithm at a certain speed or manually step through it.  
Furthermore we might change the visualization a bit, but only smaller things like labels, colors, sizes, scaling and clipping. We want to have different levels of detail by either implementing sub-steps:  
(Delaunay triangles -> Delaunay centroid -> Voronoi cells ->  Voronoi centroid -> move nodes)  
or have checkboxes to enable the visualization of the different parts.  
As a bonus (if there is enough time and motivation at the end), we might implement a smooth interpolation between the steps.
