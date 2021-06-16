# Voronoi Layout Algorithm Visualization

## How to start
#### Linux / MacOS:  
`./gradlew run`  
#### Windows:  
`gradlew.bat run`  

## Current Progress
We implemented a parser for our graph input format, Lloyd's algorithm for Voronoi layouting (although there might be some bugs or edge cases left), a Model-View-Controller architecture for the interactive application as well as a visualization of the input graph (after force layout) and for each major step (Delaunay triangles + Voronoi cells) in Lloyd's algorithm. There currently are two buttons to step forward or backward through the algorithm.

## Architecture
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
The following regular expression defines this format more formally but less readable:  
`^\s*(node)\s+(\S*)\s*$|^\s*(edge)\s*(\S+)\s*->\s*(\S+)\s*$`

### Additional Data (Internal Format)
We decided to use our own graph format. We thought that this might be easier and quicker to use than ELK-Graphs, leaving out some properties of an ELK-Graph like ports and adding others like `DelaunayTriangle`s and `VoronoiCell`s.  
During the algorithms run, a list of `LloydStep`s is collected. Each `LloydStep` contains the graph before executing the step, a list of `DelaunayTriangle`s and a list of `VoronoiCell`s as well as a map that associates each `DelaunayEdge` with a `VoronoiEdge`.

### Envisioned Visualization
We want to let the user choose to either automatically step through the algorithm at a certain speed or manually step through it.  
Furthermore we might change the visualization a bit, but only smaller things like labels, colors, sizes, scaling and clipping. We want to have different levels of detail by either implementing sub-steps:  
(Delaunay triangles -> Delaunay centroid -> Voronoi cells ->  Voronoi centroid -> move nodes)  
or have checkboxes to enable the visualization of the different parts.  
As a bonus (if there is enough time and motivation at the end), we might implement a smooth interpolation between the steps.