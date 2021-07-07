package view;

import model.Model;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame implements View {

    private final Canvas canvas;

    private final Header header;
    private final FileControl fileControl;
    private final view.DisplayOptions displayOptions;
    private final AnimationControl animationControl;

    public MainView() {
        setTitle("Voronoi Layout Visualization");
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        header = new Header();

        JPanel canvasPanel = new JPanel();
        canvasPanel.setLayout(new BorderLayout());
        canvas = new Canvas();
        canvasPanel.add(canvas, BorderLayout.CENTER);
        canvasPanel.add(header, BorderLayout.SOUTH);
        add(canvasPanel, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        fileControl = new FileControl();
        right.add(fileControl);
        displayOptions = new view.DisplayOptions(new DesignChooser(this));
        right.add(displayOptions);
        animationControl = new AnimationControl();
        right.add(animationControl);

        //basically margin:10
        right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(right, BorderLayout.EAST);

        setSize(1280, 720);
        setLocationRelativeTo(null);
        setVisible(true);
        JFrame.setDefaultLookAndFeelDecorated(true);
    }

    @Override
    public void update() {
        Model model = Model.INSTANCE;
        header.update();
        animationControl.update();
        fileControl.update();

        if (model.getLloydSteps() != null && model.getInputGraph() != null) {
            displayOptions.update();
            if (model.getLloydSteps().size() == 0 || model.getIndex() < 0) {
                canvas.update(model.getInputGraph(), model.getDisplayOptions());
            } else {
                canvas.update(model.getCurrentStep(), model.getDisplayOptions());
            }
        }
    }
}
