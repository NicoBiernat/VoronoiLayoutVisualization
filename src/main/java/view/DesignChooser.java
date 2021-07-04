package view;

import controller.DesignChooserController;

import javax.swing.*;

public class DesignChooser extends JPanel implements View {

    public DesignChooser(JFrame root) {
        UIManager.LookAndFeelInfo[] lookAndFeelInfos = UIManager.getInstalledLookAndFeels();
        String[] designOptions = new String[lookAndFeelInfos.length];
        for (int i = 0; i < designOptions.length; i++) {
            designOptions[i] = lookAndFeelInfos[i].getName();
        }
        JComboBox<String> designChooser = new JComboBox<>(designOptions);
        designChooser.setVisible(true);
        designChooser.addActionListener(new DesignChooserController(lookAndFeelInfos, root));
        add(new JLabel("UI Design: "));
        add(designChooser);
    }

    @Override
    public void update() {
        // nothing to do here
    }
}
