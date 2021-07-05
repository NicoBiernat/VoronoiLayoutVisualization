package view;

import algorithm.LloydStep;
import model.DisplayOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Map;

public class Canvas extends JPanel {
  double SCALING_CONST = 1;
  double OFFSET_CONST = 100;

  private LloydStep lloydStep;
  private LloydStep.Graph inputGraph;
  private Map<DisplayOptions, Boolean> displayOptions;

  public Canvas() {
    super();
    addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        super.componentResized(e);
        updateScaling(e.getComponent().getWidth(), e.getComponent().getHeight());
      }
    });
  }

  public void update(LloydStep lloydStep, Map<DisplayOptions,Boolean> displayOptions) {
    this.lloydStep = lloydStep;
    this.inputGraph = null;
    this.displayOptions=displayOptions;
    updateScaling(getWidth(), getHeight());
    repaint();
  }

  public void update(LloydStep.Graph graph, Map<DisplayOptions,Boolean> displayOptions) {
    this.inputGraph = graph;
    this.lloydStep = null;
    this.displayOptions=displayOptions;
    updateScaling(getWidth(), getHeight());
    repaint();
  }

  private void updateScaling(double width, double height) {
    double minCanvasSize = Math.min(width, height);
    SCALING_CONST = minCanvasSize / (1000 + 4*OFFSET_CONST);
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
      if (displayOptions.getOrDefault(DisplayOptions.GRAPH_EDGES,false))
        drawEdges(g2d, inputGraph);
      if (displayOptions.getOrDefault(DisplayOptions.GRAPH_NODES,false))
        drawNodes(g2d, inputGraph);
      return;
    }

    if (displayOptions.getOrDefault(DisplayOptions.GRAPH_EDGES,false))
      drawEdges(g2d, lloydStep.inputGraph);
    if (displayOptions.getOrDefault(DisplayOptions.DELAUNAY_CIRCLES, false))
      drawDelaunayCircles(g2d);
    if (displayOptions.getOrDefault(DisplayOptions.DELAUNAY_EDGES,false))
      drawDelaunayTriangles(g2d);
    if (displayOptions.getOrDefault(DisplayOptions.VORONOI_EDGES,false))
      drawVoronoiEdges(g2d);
    if (displayOptions.getOrDefault(DisplayOptions.VORONOI_NODES,false))
      drawVoronoiNodes(g2d);
//    g2d.setStroke(new BasicStroke(2));
    if (displayOptions.getOrDefault(DisplayOptions.VORONOI_CENTROIDS, false))
      drawVoronoiCentroids(g2d);
    if (displayOptions.getOrDefault(DisplayOptions.GRAPH_NODES,false))
      drawNodes(g2d, lloydStep.inputGraph);
    if (displayOptions.getOrDefault(DisplayOptions.SHOW_NODE_DISPLACEMENT, false))
      drawNodeDisplacement(g2d);
  }

  private void drawNodes(Graphics2D g2d, LloydStep.Graph graph) {
    for (var node : graph.nodes) {
      g2d.setColor(Color.RED);
      drawOval(Color.RED,node.x,node.y,14,g2d);
      g2d.setColor(Color.BLACK);
      g2d.setFont(new Font("Arial", Font.PLAIN, 15));
      g2d.drawString(node.id, (int) (node.x * SCALING_CONST - 5+OFFSET_CONST), (int) (node.y * SCALING_CONST - 5+OFFSET_CONST));
//      System.out.println("Drawing " + node.id + " at (" + ((int) node.x * SCALING_CONST - 5) + ", " + ((int) node.y * SCALING_CONST - 5) + ")");
    }
  }

  private void drawEdges(Graphics2D g2d, LloydStep.Graph graph) {
    for (var edge : graph.edges) {
      g2d.setColor(Color.LIGHT_GRAY);
      g2d.drawLine((int) (edge.from.x * SCALING_CONST+OFFSET_CONST), (int) (edge.from.y * SCALING_CONST+OFFSET_CONST), (int) (edge.to.x * SCALING_CONST+OFFSET_CONST), (int) (edge.to.y * SCALING_CONST+OFFSET_CONST));
    }
  }

  private void drawDelaunayCircles(Graphics2D g2d) {
    for (var triangle : lloydStep.delaunayTriangles) {
      LloydStep.DelaunayTriangle.Circle circle = triangle.getCircumCircle();
      g2d.setColor(Color.GREEN);
      g2d.drawOval((int) ((circle.center.x - circle.radius) * SCALING_CONST + OFFSET_CONST), (int) ((circle.center.y - circle.radius) * SCALING_CONST + OFFSET_CONST), (int) (2*circle.radius*SCALING_CONST), (int) (2*circle.radius*SCALING_CONST));
    }
  }

  private void drawDelaunayTriangles(Graphics2D g2d) {
    for (var triangle : lloydStep.delaunayTriangles) {
      for (var edge : triangle.edges) {
        g2d.setColor(Color.ORANGE);
        g2d.drawLine((int) (edge.from.x * SCALING_CONST + OFFSET_CONST), (int) (edge.from.y * SCALING_CONST + OFFSET_CONST), (int) (edge.to.x * SCALING_CONST + OFFSET_CONST), (int) (edge.to.y * SCALING_CONST + OFFSET_CONST));
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
      g2d.drawString("" + triangle.edges.get(0).from + triangle.edges.get(1).from + triangle.edges.get(2).from, (int) (centroid.x * SCALING_CONST - 5+OFFSET_CONST), (int) (centroid.y * SCALING_CONST - 5+OFFSET_CONST));
    }
  }

  private void drawVoronoiEdges(Graphics2D g2d) {
    for (var cell : lloydStep.voronoiCells) {
      for (var edge : cell.edges) {
        g2d.setColor(Color.BLUE);
        g2d.drawLine((int) (edge.from.x * SCALING_CONST+OFFSET_CONST), (int) (edge.from.y * SCALING_CONST+OFFSET_CONST), (int) (edge.to.x * SCALING_CONST+OFFSET_CONST), (int) (edge.to.y * SCALING_CONST+OFFSET_CONST));
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

  private void drawNodeDisplacement(Graphics2D g2d) {
    for (var node : lloydStep.inputGraph.nodes) {
      var centroid = lloydStep.getVoronoiCellForNode(node).getCentroid();
      int x1 = (int) (node.x*SCALING_CONST+OFFSET_CONST);
      int y1 = (int) (node.y*SCALING_CONST+OFFSET_CONST);
      int x2 = (int) (centroid.x*SCALING_CONST+OFFSET_CONST);
      int y2 = (int) (centroid.y*SCALING_CONST+OFFSET_CONST);
      g2d.drawLine(x1, y1, x2, y2);
      Polygon arrowHead = new Polygon();
      arrowHead.addPoint(0,5);
      arrowHead.addPoint(-5, -5);
      arrowHead.addPoint(5, -5);
      AffineTransform t = new AffineTransform();
      t.setToIdentity();
      t.translate(x2, y2);
      double angle = Math.atan2(y2-y1, x2-x1);
      t.rotate(angle - Math.PI / 2d);
      AffineTransform before = g2d.getTransform();
      g2d.setTransform(t);
      g2d.fill(arrowHead);
      g2d.setTransform(before);
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
