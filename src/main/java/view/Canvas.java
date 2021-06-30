package view;

import algorithm.LloydStep;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Canvas extends JPanel {
  int SCALING_CONST = 1;
  int OFFSET_CONST = 20;

  private LloydStep lloydStep;
  private LloydStep.Graph inputGraph;
  private HashMap<String, Boolean> displayOptions;

  public void update(LloydStep lloydStep, HashMap<String,Boolean> displayOptions) {
    this.lloydStep = lloydStep;
    this.inputGraph = null;
    this.displayOptions=displayOptions;
    repaint();
  }

  public void update(LloydStep.Graph graph, HashMap<String,Boolean> displayOptions) {
    this.inputGraph = graph;
    this.lloydStep = null;
    this.displayOptions=displayOptions;
    repaint();
  }
  //source: https://stackoverflow.com/a/18565148/7421438
  private void drawStringMiddleOfPanel(String string, Graphics g) {
    String msg = string;
    int stringWidth = 0;
    int stringAccent = 0;
    int xCoordinate = 0;
    int yCoordinate = 0;
    // get the FontMetrics for the current font
    FontMetrics fm = g.getFontMetrics();

    /** display new message */
    /** Centering the text */
    // find the center location to display
    stringWidth = fm.stringWidth(msg);
    stringAccent = fm.getAscent();
    // get the position of the leftmost character in the baseline
    xCoordinate = getWidth() / 2 - stringWidth / 2;
    yCoordinate = getHeight() / 2 + stringAccent / 2;

    // draw String
    g.drawString(msg, xCoordinate, yCoordinate);
  }

  private void drawLines(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    g2d.setStroke(new BasicStroke(3));
    if (inputGraph == null && lloydStep == null){
      drawStringMiddleOfPanel("[no graph loaded yet]",g2d);
      return;
    }

    if (lloydStep == null) {
      drawNodes(g2d, inputGraph);
      drawEdges(g2d, inputGraph);
      return;
    }

    if (displayOptions.getOrDefault("Voronoi Centroids",false))
      drawVoronoiCentroids(g2d);
    if (displayOptions.getOrDefault("Graph Nodes",false))
      drawNodes(g2d, lloydStep.inputGraph);
    if (displayOptions.getOrDefault("Delaunay Edges",false))
      drawDelaunyTriangles(g2d);
    if (displayOptions.getOrDefault("Voronoi Edges",false))
      drawVoronoiEdges(g2d);
    if (displayOptions.getOrDefault("Voronoi Nodes",false))
      drawVoronoiNodes(g2d);
    g2d.setStroke(new BasicStroke(2));
    if (displayOptions.getOrDefault("Graph Edges",false))
      drawEdges(g2d, lloydStep.inputGraph);
  }

  private void drawNodes(Graphics2D g2d, LloydStep.Graph graph) {
    for (var node : graph.nodes) {
      g2d.setColor(Color.RED);
      drawOval(Color.RED,node.x,node.y,14,g2d);
      g2d.setColor(Color.BLACK);
      g2d.drawString(node.id, (int) node.x * SCALING_CONST - 5+OFFSET_CONST, (int) node.y * SCALING_CONST - 5+OFFSET_CONST);
//      System.out.println("Drawing " + node.id + " at (" + ((int) node.x * SCALING_CONST - 5) + ", " + ((int) node.y * SCALING_CONST - 5) + ")");
    }
  }

  private void drawEdges(Graphics2D g2d, LloydStep.Graph graph) {
    for (var edge : graph.edges) {
      g2d.setColor(Color.BLACK);
      g2d.drawLine((int) edge.from.x * SCALING_CONST+OFFSET_CONST, (int) edge.from.y * SCALING_CONST+OFFSET_CONST, (int) edge.to.x * SCALING_CONST+OFFSET_CONST, (int) edge.to.y * SCALING_CONST+OFFSET_CONST);
    }
  }

  private void drawDelaunyTriangles(Graphics2D g2d) {
    for (var triangle : lloydStep.delaunayTriangles) {
      for (var edge : triangle.edges) {
        g2d.setColor(Color.ORANGE);
        g2d.drawLine((int) edge.from.x * SCALING_CONST + OFFSET_CONST, (int) edge.from.y * SCALING_CONST + OFFSET_CONST, (int) edge.to.x * SCALING_CONST + OFFSET_CONST, (int) edge.to.y * SCALING_CONST + OFFSET_CONST);
      }
          /*var centroid = triangle.getNodesCentroid();
          g2d.setColor(Color.BLUE);
          g2d.fillOval((int)centroid.x*SCALING_CONST-6, (int)centroid.y*SCALING_CONST-7,14,14);
          centroid = triangle.getCentroid();
          g2d.setColor(Color.CYAN);
          g2d.fillOval((int)centroid.x*SCALING_CONST-5, (int)centroid.y*SCALING_CONST-6,12,12);*/

    }
  }
  private void drawVoronoiNodes(Graphics2D g2d){
    for (var triangle : lloydStep.delaunayTriangles) {
      var centroid = triangle.getCircumCircle().center;
      g2d.setColor(Color.BLUE);
      drawOval(Color.BLUE,centroid.x,centroid.y,14,g2d);
      g2d.setColor(Color.BLACK);
      g2d.drawString("" + triangle.edges.get(0).from + triangle.edges.get(1).from + triangle.edges.get(2).from, (int) centroid.x * SCALING_CONST - 5+OFFSET_CONST, (int) centroid.y * SCALING_CONST - 5+OFFSET_CONST);
    }
  }

  private void drawVoronoiEdges(Graphics2D g2d) {
    for (var cell : lloydStep.voronoiCells) {
      for (var edge : cell.edges) {
        g2d.setColor(Color.BLUE);
        g2d.drawLine((int) edge.from.x * SCALING_CONST+OFFSET_CONST, (int) edge.from.y * SCALING_CONST+OFFSET_CONST, (int) edge.to.x * SCALING_CONST+OFFSET_CONST, (int) edge.to.y * SCALING_CONST+OFFSET_CONST);
      }
    }
  }
  private void drawVoronoiCentroids(Graphics2D g2d) {
    for (var cell : lloydStep.voronoiCells) {
      var centroid = cell.getCentroid();
      g2d.setColor(Color.CYAN);
      drawOval(Color.CYAN,centroid.x,centroid.y,14,g2d);
    }
  }

  private void drawOval(java.awt.Color c, double x, double y, double size, Graphics2D g2d){
    g2d.setColor(c);
    g2d.fillOval((int) (x * SCALING_CONST - size/2+OFFSET_CONST), (int) (y * SCALING_CONST - size/2+OFFSET_CONST), (int) size, (int) size);
  }

  public void paint(Graphics g) {
    super.paint(g);
    drawLines(g);
  }
}
