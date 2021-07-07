package view;

import model.DisplayOptions;
import model.Model;

import javax.swing.*;
import java.awt.*;

public class Header extends JPanel implements View {

    private JLabel headline = new JLabel();

    public static final String[] explanations = {
            "Input graph of this step",
            "Input graph without edges",
            "Delaunay triangulation",
            "Determine circumscribed circles",
            "Centers of the circles are the Voronoi nodes",
            "Connect the Voronoi nodes to get Voronoi cells",
            "Calculate the centroid for each Voronoi cell",
            "Move each node to the centroid of its Voronoi cell"
    };

    public Header() {
        headline = new JLabel("");
        headline.setFont(new Font("Arial", Font.PLAIN, 30));
        add(headline);
    }

    @Override
    public void update() {
        Model model = Model.INSTANCE;
        if (model.getDisplayOptions().getOrDefault(DisplayOptions.ENABLE_SUBSTEPS, false)) {
            headline.setText(explanations[model.getSubstepIndex()]);
        } else {
            headline.setText("");
        }
        if (model.getIndex() == -1) {
            headline.setText("Input graph (after force-directed layout)");
        }
    }
}
