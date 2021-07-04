package controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DesignChooserController implements ActionListener {
    private final UIManager.LookAndFeelInfo[] lookAndFeelInfos;
    private final JFrame root;

    public DesignChooserController(UIManager.LookAndFeelInfo[] lookAndFeelInfos, JFrame root) {
        this.lookAndFeelInfos = lookAndFeelInfos;
        this.root = root;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void actionPerformed(ActionEvent actionEvent) {
        UIManager.LookAndFeelInfo info = lookAndFeelInfos[((JComboBox) actionEvent.getSource()).getSelectedIndex()];
        try {
            UIManager.setLookAndFeel(info.getClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(root);
    }
}
