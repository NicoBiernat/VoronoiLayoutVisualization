package main;

import org.eclipse.elk.graph.util.ElkGraphUtil;

import algorithm.LloydRelaxation;
import view.MainView;

public class Main {
  public static void main(String[] args) {
    System.out.println("Hello World!");
    new MainView();
    
    
    var testGraph = ElkGraphUtil.createGraph();
    testGraph.setIdentifier("root");

    var n1 = ElkGraphUtil.createNode(testGraph);
    n1.setIdentifier("n1");
    var n2 = ElkGraphUtil.createNode(testGraph);
    n2.setIdentifier("n2");

    ElkGraphUtil.createSimpleEdge(n1, n2);
    
    var algorithm = new LloydRelaxation(testGraph);
    algorithm.computeSteps();
    algorithm.lloydSteps.get(0);
  }
}
