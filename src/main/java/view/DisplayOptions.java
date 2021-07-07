package view;

import controller.DisplayOptionsController;
import model.Model;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class DisplayOptions extends JPanel implements View {

    private final Map<model.DisplayOptions, JCheckBox> displayOptionCheckboxes = new HashMap<>();

    public DisplayOptions(DesignChooser designChooser) {
        var internalDisplayOptionsPanel = new JPanel();
        internalDisplayOptionsPanel.setLayout(new BoxLayout(internalDisplayOptionsPanel, BoxLayout.Y_AXIS));
        internalDisplayOptionsPanel.add(designChooser);

        for (var option : model.DisplayOptions.values()) {
            var optionCheckbox = new JCheckBox(option.toString(), true);
            optionCheckbox.addActionListener(new DisplayOptionsController(option));
            displayOptionCheckboxes.put(option, optionCheckbox);
            internalDisplayOptionsPanel.add(optionCheckbox);
        }
        setBorder(BorderFactory.createTitledBorder("Display Options"));
        add(internalDisplayOptionsPanel);
    }

    public void update() {
        Model m = Model.INSTANCE;
        displayOptionCheckboxes.forEach(
                (option, checkbox) -> {
                    checkbox.setSelected(m.getDisplayOptions().getOrDefault(option, false));
                    if (m.getIndex() < 0) {
                        checkbox.setEnabled(option.equals(model.DisplayOptions.GRAPH_NODES) || option.equals(model.DisplayOptions.GRAPH_EDGES));
                    } else {
                        if (!option.equals(model.DisplayOptions.ENABLE_SUBSTEPS)) {
                            boolean substepsEnabled = m.getDisplayOptions().getOrDefault(model.DisplayOptions.ENABLE_SUBSTEPS, false);
                            // enable again when substeps are disabled
                            checkbox.setEnabled(!substepsEnabled); // disable other options when substeps are enabled
                        } else {
                            checkbox.setEnabled(true);
                        }
                    }
                }
        );
    }
}
