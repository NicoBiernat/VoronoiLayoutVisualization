package view;

import controller.Controller;
import model.Model;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame implements View {

  private Canvas canvas;

  private FileControl fileControl;
  private view.DisplayOptions displayOptions;
  private AnimationControl animationControl;

  public MainView(Controller controller) {
    setTitle("Voronoi Layout Visualization");
    setResizable(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    // TODO: header or no header?
//    JPanel header = new JPanel();
//    JLabel headline = new JLabel("Voronoi Layout Visualization");
//    headline.setFont(new Font("Arial", Font.PLAIN, 30));
//    header.add(headline);
//    add(header, BorderLayout.NORTH);

    canvas = new Canvas();
    canvas.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    add(canvas, BorderLayout.CENTER);

    JPanel right = new JPanel();
    right.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

    //Header
    JPanel labelContainer = new JPanel(new FlowLayout());
    JLabel navLabel = new JLabel("Algorithm control");
    navLabel.setFont(new Font("Arial", Font.PLAIN, 20));
    labelContainer.add(navLabel);
    right.add(labelContainer);

    fileControl = new FileControl(controller);
    right.add(fileControl);
    displayOptions = new view.DisplayOptions(controller, new DesignChooser(controller, this));
    right.add(displayOptions);
    animationControl = new AnimationControl(controller);
    right.add(animationControl);

    //basically margin:10
    right.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    add(right, BorderLayout.EAST);

    setSize(1280, 720);
    setLocationRelativeTo(null);
    setVisible(true);
    JFrame.setDefaultLookAndFeelDecorated(true);
  }

  @Override
  public void update(Model model) {
    animationControl.update(model);
    fileControl.update(model);

    if (model.getLloydSteps()!=null && model.getInputGraph()!=null) {
      displayOptions.update(model);
      if (model.getLloydSteps().size() == 0 || model.getIndex() < 0){
        canvas.update(model.getInputGraph(),model.getDisplayOptions());
      } else{
        canvas.update(model.getCurrentStep(),model.getDisplayOptions());
      }
    }
  }
}
