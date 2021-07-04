package view;

import controller.Controller;
import model.Model;

import javax.swing.*;

public class DesignChooser extends JPanel implements View {

    public DesignChooser(Controller controller, JFrame root) {
        UIManager.LookAndFeelInfo[] lookAndFeelInfos = UIManager.getInstalledLookAndFeels();
        String[] designOptions = new String[lookAndFeelInfos.length];
        for (int i = 0; i < designOptions.length; i++) {
            designOptions[i] = lookAndFeelInfos[i].getName();
        }
        JComboBox<String> designChooser = new JComboBox<>(designOptions);
        designChooser.setVisible(true);
        designChooser.addActionListener(actionEvent -> { // TODO: move to controller
            UIManager.LookAndFeelInfo info = lookAndFeelInfos[designChooser.getSelectedIndex()];
            try {
                UIManager.setLookAndFeel(info.getClassName());
            } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            SwingUtilities.updateComponentTreeUI(root);
        });
        add(new JLabel("UI Design: "));
        add(designChooser);
    }

    @Override
    public void update(Model model) {
        // nothing to do here
    }
}
