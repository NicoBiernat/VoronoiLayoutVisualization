package view;

import controller.Controller;
import model.Model;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

public class FileControl extends JPanel {

    private final JLabel selectedFile = new JLabel();

    public FileControl(Controller controller) {
        selectedFile.setFont(new Font("Arial", Font.PLAIN, 14));
        add(selectedFile);

        JButton openFileBtn = new JButton("open file");
        // TODO: move to controller:
        openFileBtn.addActionListener(e -> {
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
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                controller.fileSelected(f);
            } else {
                //cancelled, do nothing
            }
        });
        add(openFileBtn);
        setBorder(BorderFactory.createTitledBorder("File Control"));
    }

    public void update(Model model) {
        if (model.getSelectedFile()==null)
            selectedFile.setText("none yet");
        else
            selectedFile.setText(pathLengthShortener(model.getSelectedFile().getPath(),20));
    }

    /**
     * source: https://www.rgagnon.com/javadetails/java-0661.html
     * Compact a path into a given number of characters. Similar to the
     * Win32 API PathCompactPathExA
     * @param path
     * @param limit
     * @return
     */
    public static String pathLengthShortener(String path, int limit) {

        if (path.length() <= limit) {
            return path;
        }

        char shortPathArray[] = new char [limit];
        char pathArray [] = path.toCharArray();
        char ellipseArray [] = "...".toCharArray();

        int pathindex = pathArray.length - 1 ;
        int shortpathindex = limit - 1;


        // fill the array from the end
        int i = 0;
        for (; i < limit  ; i++) {
            if (pathArray[pathindex - i] != '/' && pathArray[pathindex - i] != '\\') {
                shortPathArray[shortpathindex - i] = pathArray[pathindex - i] ;
            }
            else {
                break;
            }
        }
        // check how much space is left
        int free = limit - i;

        if (free < "...".length()) {
            // fill the beginning with ellipse
            for(int j = 0; j < ellipseArray.length; j++) {
                shortPathArray[j] = ellipseArray[j] ;
            }
        }
        else {
            // fill the beginning with path and leave room for the ellipse
            int j = 0;
            for(; j + ellipseArray.length < free; j++) {
                shortPathArray[j] = pathArray[j] ;
            }
            // ... add the ellipse
            for(int k = 0; j + k < free;k++) {
                shortPathArray[j + k] = ellipseArray[k] ;
            }
        }
        return new String(shortPathArray);
    }
}
