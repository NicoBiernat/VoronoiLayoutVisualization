package view;

import controller.DisplayOptionsController;
import model.Model;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class DisplayOptions extends JPanel implements View {

    private final Map<model.DisplayOptions,JCheckBox> displayOptionCheckboxes = new HashMap<>();

    public DisplayOptions(DesignChooser designChooser) {
        var internalDisplayOptionsPanel = new JPanel();
        internalDisplayOptionsPanel.setLayout(new BoxLayout(internalDisplayOptionsPanel, BoxLayout.Y_AXIS));
        internalDisplayOptionsPanel.add(designChooser);

        for (var option : model.DisplayOptions.values()){
            var optionCheckbox = new JCheckBox(option.toString(),true);
            optionCheckbox.addActionListener(new DisplayOptionsController(option));
            displayOptionCheckboxes.put(option,optionCheckbox);
            internalDisplayOptionsPanel.add(optionCheckbox);
        }
        setBorder(BorderFactory.createTitledBorder("Display Options"));
        add(internalDisplayOptionsPanel);
    }

    public void update() {
        Model model = Model.INSTANCE;
        displayOptionCheckboxes.forEach(
                (option, checkbox) -> checkbox.setSelected(model.getDisplayOptions().getOrDefault(option, false))
        );
    }
}
