package view;

import algorithm.LloydStep;

import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel {
  int SCALING_CONST = 1;

  private LloydStep lloydStep;
  private LloydStep.Graph inputGraph;

  public void update(LloydStep lloydStep) {
    this.lloydStep = lloydStep;
    this.inputGraph = null;
    repaint();
  }

  public void update(LloydStep.Graph graph) {
    this.inputGraph = graph;
    this.lloydStep = null;
    repaint();
  }

  private void drawLines(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    g2d.setStroke(new BasicStroke(3));

    if (lloydStep == null) {
      drawNodes(g2d, inputGraph);
      drawEdges(g2d, inputGraph);
    } else {
      drawNodes(g2d, lloydStep.inputGraph);
//      drawEdges(g2d, lloydStep.inputGraph);
      drawDelaunyTriangles(g2d);
      drawVoronoiCells(g2d);
    }
  }

  private void drawNodes(Graphics2D g2d, LloydStep.Graph graph) {
    for (var node : graph.nodes) {
      g2d.setColor(Color.RED);
      g2d.fillOval((int) node.x * SCALING_CONST - 5, (int) node.y * SCALING_CONST - 5, 10, 10);
      g2d.setColor(Color.BLACK);
      g2d.drawString(node.id, (int) node.x * SCALING_CONST - 5, (int) node.y * SCALING_CONST - 5);
//      System.out.println("Drawing " + node.id + " at (" + ((int) node.x * SCALING_CONST - 5) + ", " + ((int) node.y * SCALING_CONST - 5) + ")");
    }
  }

  private void drawEdges(Graphics2D g2d, LloydStep.Graph graph) {
    for (var edge : graph.edges) {
      g2d.setColor(Color.BLACK);
      g2d.drawLine((int) edge.from.x * SCALING_CONST, (int) edge.from.y * SCALING_CONST, (int) edge.to.x * SCALING_CONST, (int) edge.to.y * SCALING_CONST);
    }
  }

  private void drawDelaunyTriangles(Graphics2D g2d) {
    for (var triangle : lloydStep.delaunayTriangles) {
      for (var edge : triangle.edges) {
        g2d.setColor(Color.ORANGE);
        g2d.drawLine((int) edge.from.x * SCALING_CONST, (int) edge.from.y * SCALING_CONST, (int) edge.to.x * SCALING_CONST, (int) edge.to.y * SCALING_CONST);
      }
          /*var centroid = triangle.getNodesCentroid();
          g2d.setColor(Color.BLUE);
          g2d.fillOval((int)centroid.x*SCALING_CONST-6, (int)centroid.y*SCALING_CONST-7,14,14);
          centroid = triangle.getCentroid();
          g2d.setColor(Color.CYAN);
          g2d.fillOval((int)centroid.x*SCALING_CONST-5, (int)centroid.y*SCALING_CONST-6,12,12);*/

    }
    for (var triangle : lloydStep.delaunayTriangles) {
      var centroid = triangle.getCircumCircle().center;
      g2d.setColor(Color.BLUE);
      g2d.fillOval((int) centroid.x * SCALING_CONST - 5, (int) centroid.y * SCALING_CONST - 5, 10, 10);
      g2d.setColor(Color.BLACK);
      g2d.drawString("" + triangle.edges.get(0).from + triangle.edges.get(1).from + triangle.edges.get(2).from, (int) centroid.x * SCALING_CONST - 5, (int) centroid.y * SCALING_CONST - 5);
    }
  }

  private void drawVoronoiCells(Graphics2D g2d) {
    for (var cell : lloydStep.voronoiCells) {
      for (var edge : cell.edges) {
        g2d.setColor(Color.BLUE);
        g2d.drawLine((int) edge.from.x * SCALING_CONST, (int) edge.from.y * SCALING_CONST, (int) edge.to.x * SCALING_CONST, (int) edge.to.y * SCALING_CONST);
      }
      var centroid = cell.getNodesCentroid();
          /*g2d.setColor(Color.GREEN);
          g2d.fillOval((int)centroid.x*SCALING_CONST-6, (int)centroid.y*SCALING_CONST-7,14,14);
          centroid = cell.getCentroid();*/
      g2d.setColor(Color.CYAN);
      g2d.fillOval((int) centroid.x * SCALING_CONST - 5, (int) centroid.y * SCALING_CONST - 7, 14, 14);
    }
  }

  public void paint(Graphics g) {
    super.paint(g);
    drawLines(g);
  }
}
