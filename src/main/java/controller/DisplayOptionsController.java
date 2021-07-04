package controller;

import model.DisplayOptions;
import model.Model;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DisplayOptionsController implements ActionListener {

    private DisplayOptions option;

    public DisplayOptionsController(DisplayOptions option) {
        this.option = option;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Model model = Model.INSTANCE;
        model.setDisplayOption(option,((JCheckBox) actionEvent.getSource()).isSelected());
    }
}
