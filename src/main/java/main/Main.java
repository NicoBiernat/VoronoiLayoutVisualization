package main;

import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import algorithm.LloydRelaxation;
import view.MainView;

import java.util.Random;

public class Main {
  public static void main(String[] args) {
    System.out.println("Hello World!");
    new MainView();

    var testGraph = generateRandomGraph(5, 0, new Random(1));

    var algorithm = new LloydRelaxation(testGraph);
    algorithm.computeSteps();
  }

  public static ElkNode generateRandomGraph(int numNodes, int numEdges,Random rnd){
    if (rnd==null)rnd = new Random();
    var graph = ElkGraphUtil.createGraph();
    graph.setIdentifier("root");
    for (int i = 0;i<numNodes;i++){
      var node = ElkGraphUtil.createNode(graph);
      node.setIdentifier("n"+i);
      node.setX(rnd.nextInt(100));
      node.setY(rnd.nextInt(100));
    }
    if (numEdges > numNodes*(numNodes-1)) numEdges=numNodes*(numNodes-1);
    for (int i = 0;i<numEdges;i++){
      var node1 = graph.getChildren().get(rnd.nextInt(graph.getChildren().size()));
      var node2 = graph.getChildren().get(rnd.nextInt(graph.getChildren().size()));
      if (node1==node2 || node1.getOutgoingEdges().stream().anyMatch(edge->edge.getTargets().contains(node2))) {
        i--;
        continue;
      }
      ElkGraphUtil.createSimpleEdge(node1,node2);
    }
    return graph;
  }
}
