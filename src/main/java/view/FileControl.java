package view;

import controller.FileController;
import model.Model;

import javax.swing.*;
import java.awt.*;

public class FileControl extends JPanel {

    private final JLabel selectedFile = new JLabel();

    public FileControl() {
        selectedFile.setFont(new Font("Arial", Font.PLAIN, 14));
        add(selectedFile);

        JButton openFileBtn = new JButton("Open file");
        openFileBtn.addActionListener(new FileController(this));
        add(openFileBtn);
        setBorder(BorderFactory.createTitledBorder("File Control"));
    }

    public void update() {
        Model model = Model.INSTANCE;
        if (model.getSelectedFile() == null) {
            selectedFile.setText("none yet");
        } else {
            selectedFile.setText(pathLengthShortener(model.getSelectedFile().getPath(), 20));
        }
    }

    /**
     * source: https://www.rgagnon.com/javadetails/java-0661.html
     * Compact a path into a given number of characters. Similar to the
     * Win32 API PathCompactPathExA
     *
     * @param path
     * @param limit
     * @return
     */
    public static String pathLengthShortener(String path, int limit) {

        if (path.length() <= limit) {
            return path;
        }

        char[] shortPathArray = new char[limit];
        char[] pathArray = path.toCharArray();
        char[] ellipseArray = "...".toCharArray();

        int pathindex = pathArray.length - 1;
        int shortpathindex = limit - 1;


        // fill the array from the end
        int i = 0;
        for (; i < limit; i++) {
            if (pathArray[pathindex - i] != '/' && pathArray[pathindex - i] != '\\') {
                shortPathArray[shortpathindex - i] = pathArray[pathindex - i];
            } else {
                break;
            }
        }
        // check how much space is left
        int free = limit - i;

        if (free < "...".length()) {
            // fill the beginning with ellipse
            for (int j = 0; j < ellipseArray.length; j++) {
                shortPathArray[j] = ellipseArray[j];
            }
        } else {
            // fill the beginning with path and leave room for the ellipse
            int j = 0;
            for (; j + ellipseArray.length < free; j++) {
                shortPathArray[j] = pathArray[j];
            }
            // ... add the ellipse
            for (int k = 0; j + k < free; k++) {
                shortPathArray[j + k] = ellipseArray[k];
            }
        }
        return new String(shortPathArray);
    }
}
