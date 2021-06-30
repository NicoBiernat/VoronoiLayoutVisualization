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

  private JLabel selectedFile = new JLabel();
  private JButton playPauseButton;
  private JSlider stepSlider;

  private HashMap<String,JCheckBox> displayOptionCheckboxes=new HashMap<>();

  public MainView(Controller controller) {
    this.controller = controller;

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
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
    canvas.setBackground(Color.LIGHT_GRAY);
    add(canvas, BorderLayout.CENTER);

    JPanel right = new JPanel();
    right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

    //Header
    JPanel labelContainer = new JPanel(new FlowLayout());
    JLabel navLabel = new JLabel("Algorithm control");
    navLabel.setFont(new Font("Arial", Font.PLAIN, 20));
    labelContainer.add(navLabel);
    right.add(labelContainer);

    //TODO: fix the layouting of all the panels, for it to be similar to the UI Mock

    //File Control
    JPanel fileControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    fileControlPanel.setLayout(new BoxLayout(fileControlPanel, BoxLayout.Y_AXIS));

    selectedFile.setFont(new Font("Arial", Font.PLAIN, 20));
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
    right.add(fileControlPanel);

    var displayOptionsPanel= new JPanel(new FlowLayout());
    displayOptionsPanel.setLayout(new BoxLayout(displayOptionsPanel, BoxLayout.Y_AXIS));

    //TODO: maybe rework this into something more typesafe between view, model and controller
    for (var option : Model.DISPLAY_OPTIONS){
      var optionCheckbox = new JCheckBox(option,true);
      optionCheckbox.addActionListener(e->controller.setDisplayOption(option,((JCheckBox)e.getSource()).isSelected()));
      displayOptionCheckboxes.put(option,optionCheckbox);
      displayOptionsPanel.add(optionCheckbox);
    }

    displayOptionsPanel.setBorder(BorderFactory.createTitledBorder("Display Options"));
    right.add(displayOptionsPanel);

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
    JButton stepForward = new JButton("||>");
    stepForward.addActionListener(controller);
    stepBackward.addActionListener(controller);

    var gotoStart = new JButton("|<<");
    var gotoEnd = new JButton(">>|");
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
    right.add(animationControlPanel);

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
      playPauseButton.setText("||");
    }else{
      playPauseButton.setActionCommand("|>");
      playPauseButton.setText("|>");
    }

    stepSlider.setMinimum(-1);
    stepSlider.setMaximum(model.getLloydSteps().size());
    stepSlider.setValue(model.getIndex());

    if (model.getSelectedFile()==null)
      selectedFile.setText("selected: none yet");
    else
      selectedFile.setText("selected: "+pathLengthShortener(model.getSelectedFile().getPath(),20));

    if (model.getLloydSteps()!=null && model.getInputGraph()!=null) {
      if (model.getLloydSteps().size() == 0 || model.getIndex() < 0){
        step.setText("Input Graph");
        canvas.update(model.getInputGraph(),model.getDisplayOptions());
        //FIXME: dev override
        model.firstStep();
      } else{
        step.setText("Step " + model.getIndex() + "/" + (model.getLloydSteps().size() - 1));
        canvas.update(model.getCurrentStep(),model.getDisplayOptions());
      }
    }
  }
}
