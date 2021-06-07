package view;

import javax.swing.*;
import java.awt.*;

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

    JPanel center = new JPanel();
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
}
