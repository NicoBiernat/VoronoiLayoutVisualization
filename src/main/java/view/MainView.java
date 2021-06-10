package view;

import algorithm.LloydRelaxation;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.Random;

public class MainView extends JFrame {
  public MainView() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    setTitle("Voronoi Layout Visualization");
    setResizable(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    JPanel header = new JPanel();
    JLabel headline = new JLabel("Voronoi Layout Visualization");
    headline.setFont(new Font("Arial", Font.PLAIN, 30));
    header.add(headline);
    add(header, BorderLayout.NORTH);


    var testGraph = generateRandomGraph(20, 0, new Random(1));

    var algorithm = new LloydRelaxation(testGraph);
    algorithm.computeSteps();

    var SCALING_CONST = 5;
    JPanel center = new JPanel(){
      void drawLines(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke(3));

        for (var triangle: algorithm.lloydSteps.get(0).delaunayTriangles){
          for (var edge: triangle.edges){
            g2d.setColor(Color.ORANGE);
            g2d.drawLine((int)edge.from.x*SCALING_CONST,(int)edge.from.y*SCALING_CONST,(int)edge.to.x*SCALING_CONST,(int)edge.to.y*SCALING_CONST);
          }
          /*var centroid = triangle.getNodesCentroid();
          g2d.setColor(Color.BLUE);
          g2d.fillOval((int)centroid.x*SCALING_CONST-6, (int)centroid.y*SCALING_CONST-7,14,14);
          centroid = triangle.getCentroid();
          g2d.setColor(Color.CYAN);
          g2d.fillOval((int)centroid.x*SCALING_CONST-5, (int)centroid.y*SCALING_CONST-6,12,12);*/

        }

        for (var cell: algorithm.lloydSteps.get(0).voronoiCells){
          for (var edge: cell.edges){
            g2d.setColor(Color.BLUE);
            g2d.drawLine((int)edge.from.x*SCALING_CONST,(int)edge.from.y*SCALING_CONST,(int)edge.to.x*SCALING_CONST,(int)edge.to.y*SCALING_CONST);
          }
          var centroid = cell.getNodesCentroid();
          /*g2d.setColor(Color.GREEN);
          g2d.fillOval((int)centroid.x*SCALING_CONST-6, (int)centroid.y*SCALING_CONST-7,14,14);
          centroid = cell.getCentroid();*/
          g2d.setColor(Color.CYAN);
          g2d.fillOval((int)centroid.x*SCALING_CONST-5, (int)centroid.y*SCALING_CONST-7,14,14);
        }
        for (var triangle: algorithm.lloydSteps.get(0).delaunayTriangles) {
          var centroid = triangle.getCircumCircle().center;
          g2d.setColor(Color.BLUE);
          g2d.fillOval((int) centroid.x * SCALING_CONST - 5, (int) centroid.y * SCALING_CONST - 5, 10, 10);
          g2d.setColor(Color.BLACK);
          g2d.drawString(""+triangle.edges.get(0).from+triangle.edges.get(1).from+triangle.edges.get(2).from, (int)centroid.x *SCALING_CONST-5, (int)centroid.y *SCALING_CONST-5);
        }

       for (var node: algorithm.lloydSteps.get(0).inputGraph.nodes){
         g2d.setColor(Color.RED);
         g2d.fillOval((int)node.x*SCALING_CONST-5, (int)node.y*SCALING_CONST-5,10,10);
         g2d.setColor(Color.BLACK);
         g2d.drawString(node.id, (int)node.x*SCALING_CONST-5, (int)node.y*SCALING_CONST-5);
       }


      }
      public void paint(Graphics g) {
        super.paint(g);
        drawLines(g);
      }
    };
    center.setBackground(Color.LIGHT_GRAY);
    add(center, BorderLayout.CENTER);

    JPanel right = new JPanel();
    right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
    for (int i = 0; i < 20; i++) {
      right.add(new JButton("Dummy Button " + i));
    }
    add(right, BorderLayout.EAST);

    JPanel footer = new JPanel();
    footer.add(new JLabel("Fooooooooooooooter"));
    add(footer, BorderLayout.SOUTH);

    setSize(1280, 720);
    setLocationRelativeTo(null);
    setVisible(true);
    JFrame.setDefaultLookAndFeelDecorated(true);
  }

  public static ElkNode generateRandomGraph(int numNodes, int numEdges, Random rnd){
    if (rnd==null)rnd = new Random();
    var graph = ElkGraphUtil.createGraph();
    graph.setIdentifier("root");
    for (int i = 0;i<numNodes;i++){
      var node = ElkGraphUtil.createNode(graph);
      node.setIdentifier("n"+i);
      node.setX(rnd.nextInt(100)+30);
      node.setY(rnd.nextInt(100)+30);
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
