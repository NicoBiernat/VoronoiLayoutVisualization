package view;

import controller.Controller;
import model.Model;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.HashMap;

public class MainView extends JFrame implements View {
  private Controller controller;
  private Canvas canvas;

  private JLabel step = new JLabel();
  private JLabel substep = new JLabel();

  private JLabel selectedFile = new JLabel();
  private JButton playPauseButton;
  private JSlider stepSlider;

  private HashMap<String,JCheckBox> displayOptionCheckboxes=new HashMap<>();

  public MainView(Controller controller) {
    this.controller = controller;

    setTitle("Voronoi Layout Visualization");
    setResizable(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
//    JPanel header = new JPanel();
//    JLabel headline = new JLabel("Voronoi Layout Visualization");
//    headline.setFont(new Font("Arial", Font.PLAIN, 30));
//    header.add(headline);
//    add(header, BorderLayout.NORTH);

    canvas = new Canvas();
//    canvas.setBackground(Color.LIGHT_GRAY);
    canvas.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    add(canvas, BorderLayout.CENTER);

    JPanel right = new JPanel();
    right.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

    //Header
    JPanel labelContainer = new JPanel(new FlowLayout());
    JLabel navLabel = new JLabel("Algorithm control");
    navLabel.setFont(new Font("Arial", Font.PLAIN, 20));
    labelContainer.add(navLabel);
    right.add(labelContainer);

    //TODO: fix the layouting of all the panels, for it to be similar to the UI Mock


    right.add(fileControl());
    right.add(displayOptions());
    right.add(animationControl());

    //basically margin:10
    right.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    add(right, BorderLayout.EAST);

    setSize(1280, 720);
    setLocationRelativeTo(null);
    setVisible(true);
    JFrame.setDefaultLookAndFeelDecorated(true);
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

  @Override
  public void update(Model model) {

    if (model.isPlayingSteps()){
      playPauseButton.setActionCommand("||");
      playPauseButton.setText("\u23F8");
    }else{
      playPauseButton.setActionCommand("|>");
      playPauseButton.setText("\u25B6");
    }

    stepSlider.setMinimum(-1);
    if (model.getLloydSteps() != null) {
      stepSlider.setMaximum(model.getLloydSteps().size());
    }
    stepSlider.setValue(model.getIndex());

    if (model.getSelectedFile()==null)
      selectedFile.setText("none yet");
    else
      selectedFile.setText(pathLengthShortener(model.getSelectedFile().getPath(),20));

    if (model.getLloydSteps()!=null && model.getInputGraph()!=null) {
      displayOptionCheckboxes.forEach((option, checkbox) -> checkbox.setSelected(model.getDisplayOptions().getOrDefault(option, false)));
      if (model.getLloydSteps().size() == 0 || model.getIndex() < 0){
        step.setText("<html>Input Graph<br>(after force-directed layout)</html>");
        canvas.update(model.getInputGraph(),model.getDisplayOptions());
        //FIXME: dev override
//        model.firstStep();
      } else{
        step.setText("Step " + model.getIndex() + "/" + (model.getLloydSteps().size() - 1));
        if (model.getDisplayOptions().getOrDefault(Model.DISPLAY_OPTIONS[6], false)) { // substeps enabled
          substep.setText("Substep " + model.getSubstepIndex() + "/" + (model.getNumSubsteps()-1));
        } else {
          substep.setText("");
        }
        canvas.update(model.getCurrentStep(),model.getDisplayOptions());
      }
    }
  }

  private JPanel designChooser() {
    UIManager.LookAndFeelInfo[] lookAndFeelInfos = UIManager.getInstalledLookAndFeels();
    String[] designOptions = new String[lookAndFeelInfos.length];
    for (int i = 0; i < designOptions.length; i++) {
      designOptions[i] = lookAndFeelInfos[i].getName();
    }
    JComboBox<String> designChooser = new JComboBox<>(designOptions);
    designChooser.setVisible(true);
    designChooser.addActionListener(actionEvent -> {
      UIManager.LookAndFeelInfo info = lookAndFeelInfos[designChooser.getSelectedIndex()];
      try {
        UIManager.setLookAndFeel(info.getClassName());
      } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
        e.printStackTrace();
      }
      SwingUtilities.updateComponentTreeUI(this);
    });
    JPanel panelDesignChooser = new JPanel(new FlowLayout());
    panelDesignChooser.add(new JLabel("UI Design: "));
    panelDesignChooser.add(designChooser);
    return panelDesignChooser;
  }

  private JPanel fileControl() {
    //File Control
    JPanel fileControlPanel = new JPanel(new FlowLayout());
//    fileControlPanel.setLayout(new BoxLayout(fileControlPanel, BoxLayout.Y_AXIS));
//    JLabel selectedFileText = new JLabel("Selected file:");
//    fileControlPanel.add(selectedFileText);
    selectedFile.setFont(new Font("Arial", Font.PLAIN, 14));
    fileControlPanel.add(selectedFile);

    JButton openFileBtn = new JButton("open file");
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
    fileControlPanel.add(openFileBtn);
    fileControlPanel.setBorder(BorderFactory.createTitledBorder("File Control"));
//    fileControlAndDisplayOptions.add(fileControlPanel);
    return fileControlPanel;
  }

  private JPanel displayOptions() {
    var displayOptionsPanel= new JPanel(new FlowLayout());
    var internalDisplayOptionsPanel = new JPanel();
    internalDisplayOptionsPanel.setLayout(new BoxLayout(internalDisplayOptionsPanel, BoxLayout.Y_AXIS));
    internalDisplayOptionsPanel.add(designChooser());

    //TODO: maybe rework this into something more typesafe between view, model and controller
    for (var option : Model.DISPLAY_OPTIONS){
      var optionCheckbox = new JCheckBox(option,true);
      optionCheckbox.addActionListener(e->controller.setDisplayOption(option,((JCheckBox)e.getSource()).isSelected()));
      displayOptionCheckboxes.put(option,optionCheckbox);
      internalDisplayOptionsPanel.add(optionCheckbox);
    }
    displayOptionsPanel.setBorder(BorderFactory.createTitledBorder("Display Options"));
    displayOptionsPanel.add(internalDisplayOptionsPanel);
//    fileControlAndDisplayOptions.add(displayOptionsPanel);
//    right.add(fileControlAndDisplayOptions);
    return displayOptionsPanel;
  }

  private JPanel animationControl() {
    var animationControlPanel= new JPanel(new FlowLayout());
    animationControlPanel.setLayout(new BoxLayout(animationControlPanel, BoxLayout.Y_AXIS));

    //Speed control
    JPanel speedSliderContainer = new JPanel(new FlowLayout());
    JSlider speedSlider = new JSlider();
    speedSlider.addChangeListener(e -> {
      controller.speedSliderChanged(((JSlider)e.getSource()).getValue());
    });
    JLabel speedSliderLabel = new JLabel("Speed");
    speedSliderContainer.add(speedSliderLabel);
    speedSliderContainer.add(speedSlider);
    animationControlPanel.add(speedSliderContainer);
    speedSlider.setValue(5);

    //Steps
    JPanel stepContainer = new JPanel(new FlowLayout());
    step.setText("");
    stepContainer.add(step);
    stepContainer.add(substep);
    animationControlPanel.add(stepContainer);

    //Step control
    stepSlider = new JSlider();
    stepSlider.addChangeListener(e -> {
      controller.stepSliderChanged(((JSlider)e.getSource()).getValue());
    });
    stepSlider.setMinimum(-1);
    stepSlider.setValue(-1);
    animationControlPanel.add(stepSlider);

    //Steps Control
    JPanel playbackContainer = new JPanel(new FlowLayout());
    JButton stepBackward = new JButton("<||");
    stepBackward.setText("\u23EA");
    stepBackward.setActionCommand("<||");
    JButton stepForward = new JButton("||>");
    stepForward.setText("\u23E9");
    stepForward.setActionCommand("||>");
    stepForward.addActionListener(controller);
    stepBackward.addActionListener(controller);

    var gotoStart = new JButton("|<<");
    gotoStart.setText("\u23EE");
    gotoStart.setActionCommand("|<<");
    var gotoEnd = new JButton(">>|");
    gotoEnd.setText("\u23ED");
    gotoEnd.setActionCommand(">>|");
    playPauseButton = new JButton("|>");
    gotoStart.addActionListener(controller);
    gotoEnd.addActionListener(controller);
    playPauseButton.addActionListener(controller);

    playbackContainer.add(gotoStart);
    playbackContainer.add(stepBackward);
    playbackContainer.add(playPauseButton);
    playbackContainer.add(stepForward);
    playbackContainer.add(gotoEnd);

    animationControlPanel.add(playbackContainer);

    animationControlPanel.setBorder(BorderFactory.createTitledBorder("Animation Control"));
    return animationControlPanel;
  }
}
