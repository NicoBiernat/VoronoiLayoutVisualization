package controller;

import model.Model;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileController implements ActionListener {

    private final Component root;

    public FileController(Component root) {
        this.root = root;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
        chooser.setFileFilter(new FileFilter() {
            public String getDescription() {
                return "ELKT files (*.elkt)";
            }

            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    String filename = f.getName().toLowerCase();
                    return filename.endsWith(".elkt");
                }
            }
        });
        if (chooser.showOpenDialog(root) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            Model model = Model.INSTANCE;
            model.loadFile(f);
        }
    }
}
