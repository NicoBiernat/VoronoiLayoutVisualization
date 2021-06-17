# Voronoi Layout Algorithm Visualization

## How to start
#### Linux / MacOS:  
`./gradlew run`  
#### Windows:  
`gradlew.bat run`  

## Current Progress

![image](https://user-images.githubusercontent.com/25539263/122387067-c564c580-cf6e-11eb-8b23-d7b2150ed2f8.png)

We implemented a parser for our graph input format, Lloyd's algorithm for Voronoi layouting (although there might be some bugs or edge cases left), a Model-View-Controller architecture for the interactive application as well as a visualization of the input graph (after force layout) and for each major step (Delaunay triangles + Voronoi cells) in Lloyd's algorithm. There currently are two buttons to step forward or backward through the algorithm.



## Architecture

![image](https://user-images.githubusercontent.com/25539263/122384258-cea06300-cf6b-11eb-87bb-e49952ab9222.png)

The architecture consists of a parser, which parses an input graph from a file and creates an ELK-Graph. After that, a conversion into our own internal graph format is carried out.  
The ELK-Force algorithm is executed on the graph before the Voronoi-Layout algorithm begins.  
We chose to implement Lloyd's algorithm ourselves to be able to better extract all the interesting intermediate steps.
We execute the whole algorithm and save all intermediate steps.
Those steps are then loaded into the model of our Model-View-Controller architecture, which can then interactively visualize all precalculated steps.
We might move the preprocessing (parsing, conversion and force-layout) into the MVC architecture such that the graph can be changed at runtime.


### Graph Input Format
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
Node n1
Node n2
Node n3
Edge n1->n2
Edge n2->n3
```

### Additional Data (Internal Format)

![image](https://user-images.githubusercontent.com/25539263/122387810-8a16c680-cf6f-11eb-8dd7-cb5b5434af54.png)

We decided to use our own graph format. We thought that this might be easier and quicker to use than ELK-Graphs, leaving out some properties of an ELK-Graph like ports and hyperedges, and adding others like `DelaunayTriangle`s and `VoronoiCell`s.  
During the algorithms run, a list of `LloydStep`s is collected. Each `LloydStep` contains the graph before executing the step, and calculates a list of `DelaunayTriangle` which forms a [Delaunay triangulation](https://en.wikipedia.org/wiki/Delaunay_triangulation) of the input graph and using the triangulation a list of `VoronoiCell`, which each holds an association to the input `Node` they correspond to. 

### Envisioned Visualization

![image](https://user-images.githubusercontent.com/25539263/122387462-2c827a00-cf6f-11eb-9953-42bdaabd4f55.png)

We want to let the user choose to either automatically step through the algorithm at a certain speed or manually step through it.  
Furthermore we might change the visualization a bit, but only smaller things like labels, colors, sizes, scaling and clipping. We want to have different levels of detail by either implementing sub-steps:  
(Delaunay triangles -> Delaunay centroid -> Voronoi cells ->  Voronoi centroid -> move nodes)  
or have checkboxes to enable the visualization of the different parts.  
As a bonus (if there is enough time and motivation at the end), we might implement a smooth interpolation between the steps.
