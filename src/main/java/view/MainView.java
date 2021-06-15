package view;

import controller.Controller;
import model.Model;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame implements View {
  private static final String FILE_NAME = "TestGraph.elkt";

  private Controller controller;
  private Canvas canvas;

  public MainView(Controller controller) {
    this.controller = controller;

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    setTitle("Voronoi Layout Visualization");
    setResizable(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

//    JPanel header = new JPanel();
//    JLabel headline = new JLabel("Voronoi Layout Visualization");
//    headline.setFont(new Font("Arial", Font.PLAIN, 30));
//    header.add(headline);
//    add(header, BorderLayout.NORTH);

    canvas = new Canvas();
    canvas.setBackground(Color.LIGHT_GRAY);
    add(canvas, BorderLayout.CENTER);

//    JPanel right = new JPanel();
//    right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
//    for (int i = 0; i < 20; i++) {
//      right.add(new JButton("Dummy Button " + i));
//    }
//    add(right, BorderLayout.EAST);

//    JPanel footer = new JPanel();
//    footer.add(new JLabel("Fooooooooooooooter"));
//    add(footer, BorderLayout.SOUTH);

    setSize(1920, 1080);
    setLocationRelativeTo(null);
    setVisible(true);
    JFrame.setDefaultLookAndFeelDecorated(true);
  }

  @Override
  public void update(Model model) {
    if (model.getLloydSteps().size() == 0) {
      System.out.println("Updating inputgraph");
      canvas.update(model.getInputGraph());
    } else {
      System.out.println("Updating lloydsteps");
      canvas.update(model.getCurrentStep());
    }
  }
}
