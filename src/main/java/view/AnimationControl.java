package view;

import controller.Controller;
import model.DisplayOptions;
import model.Model;

import javax.swing.*;
import java.awt.*;

public class AnimationControl extends JPanel implements View {

    private JLabel step = new JLabel();
    private JLabel substep = new JLabel();
    private JSlider stepSlider = new JSlider();
    private JButton playPauseButton;

    public AnimationControl(Controller controller) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //Speed control
        JPanel speedSliderContainer = new JPanel(new FlowLayout());
        JSlider speedSlider = new JSlider();
        speedSlider.addChangeListener(e -> {
            controller.speedSliderChanged(((JSlider)e.getSource()).getValue());
        });
        JLabel speedSliderLabel = new JLabel("Speed");
        speedSliderContainer.add(speedSliderLabel);
        speedSliderContainer.add(speedSlider);
        add(speedSliderContainer);
        speedSlider.setValue(5);

        //Steps
        JPanel stepContainer = new JPanel(new FlowLayout());
        step.setText("");
        stepContainer.add(step);
        stepContainer.add(substep);
        add(stepContainer);

        //Step control
        stepSlider.addChangeListener(e -> {
            controller.stepSliderChanged(((JSlider)e.getSource()).getValue());
        });
        stepSlider.setMinimum(-1);
        stepSlider.setValue(-1);
        add(stepSlider);

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

        add(playbackContainer);

        setBorder(BorderFactory.createTitledBorder("Animation Control"));
    }

    @Override
    public void update(Model model) {
        if (model.isPlayingSteps()){
            playPauseButton.setActionCommand("||");
            playPauseButton.setText("\u23F8");
        } else {
            playPauseButton.setActionCommand("|>");
            playPauseButton.setText("\u25B6");
        }

        stepSlider.setMinimum(-1);
        if (model.getLloydSteps() != null) {
            stepSlider.setMaximum(model.getLloydSteps().size());
        }
        stepSlider.setValue(model.getIndex());

        if (model.getLloydSteps()!=null && model.getInputGraph()!=null) {
            if (model.getLloydSteps().size() == 0 || model.getIndex() < 0){
                step.setText("<html>Input Graph<br>(after force-directed layout)</html>");
            } else{
                step.setText("Step " + model.getIndex() + "/" + (model.getLloydSteps().size() - 1));
                if (model.getDisplayOptions().getOrDefault(DisplayOptions.ENABLE_SUBSTEPS, false)) { // substeps enabled
                    substep.setText("Substep " + model.getSubstepIndex() + "/" + (model.getNumSubsteps()-1));
                } else {
                    substep.setText("");
                }
            }
        }
    }
}
