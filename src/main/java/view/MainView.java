package view;

import controller.Controller;
import model.Model;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame implements View {
  private static final String FILE_NAME = "TestGraph.elkt";

  private Controller controller;
  private Canvas canvas;

  private JLabel step = new JLabel();

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

    JPanel right = new JPanel();
    right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

    JPanel labelContainer = new JPanel(new FlowLayout());
    JLabel navLabel = new JLabel("Algorithm control");
    navLabel.setFont(new Font("Arial", Font.PLAIN, 20));
    labelContainer.add(navLabel);
    right.add(labelContainer);

    JPanel stepContainer = new JPanel(new FlowLayout());
    step.setText("Step 0/0");
    stepContainer.add(step);
    right.add(stepContainer);

    JPanel nav = new JPanel(new FlowLayout());
    JButton prev = new JButton("<");
    JButton next = new JButton(">");
    next.addActionListener(controller);
    prev.addActionListener(controller);
    nav.add(prev);
    nav.add(next);
    right.add(nav);
    add(right, BorderLayout.EAST);

//    JPanel footer = new JPanel();
//    footer.add(new JLabel("Fooooooooooooooter"));
//    add(footer, BorderLayout.SOUTH);

    setSize(1280, 720);
    setLocationRelativeTo(null);
    setVisible(true);
    JFrame.setDefaultLookAndFeelDecorated(true);
  }

  @Override
  public void update(Model model) {
    if (model.getLloydSteps().size() == 0 || model.getIndex() < 0) {
      canvas.update(model.getInputGraph());
    } else {
      step.setText("Step " + model.getIndex() + "/" + (model.getLloydSteps().size()-1));
      canvas.update(model.getCurrentStep());
    }
  }
}
