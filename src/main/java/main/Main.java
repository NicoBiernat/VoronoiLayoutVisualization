package main;

import controller.Controller;
import model.Model;
import org.eclipse.elk.alg.force.ForceLayoutProvider;
import org.eclipse.elk.alg.force.options.ForceMetaDataProvider;
import org.eclipse.elk.alg.force.options.ForceModelStrategy;
import org.eclipse.elk.alg.force.options.ForceOptions;
import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import algorithm.LloydRelaxation;
import parser.Parser;
import view.MainView;

import java.io.IOException;
import java.util.List;
import java.util.Random;


public class Main {

  private static final String FILE_NAME = "TestGraph.elkt";

  public static void main(String[] args) {

    System.out.println("Creating MVC");
    Model model = new Model(null, null);
    Controller controller = new Controller(model);
    MainView view = new MainView(controller);
    model.registerView(view);
  }

  public static ElkNode generateRandomGraph(int numNodes, int numEdges, Random rnd) {
    if (rnd == null) rnd = new Random();
    var graph = ElkGraphUtil.createGraph();
    graph.setIdentifier("root");
    for (int i = 0; i < numNodes; i++) {
      var node = ElkGraphUtil.createNode(graph);
      node.setIdentifier("n" + i);
      node.setX(rnd.nextInt(100) + 30);
      node.setY(rnd.nextInt(100) + 30);
    }
    if (numEdges > numNodes * (numNodes - 1)) numEdges = numNodes * (numNodes - 1);
    for (int i = 0; i < numEdges; i++) {
      var node1 = graph.getChildren().get(rnd.nextInt(graph.getChildren().size()));
      var node2 = graph.getChildren().get(rnd.nextInt(graph.getChildren().size()));
      if (node1 == node2 || node1.getOutgoingEdges().stream().anyMatch(edge -> edge.getTargets().contains(node2))) {
        i--;
        continue;
      }
      ElkGraphUtil.createSimpleEdge(node1, node2);
    }
    return graph;
  }
}
