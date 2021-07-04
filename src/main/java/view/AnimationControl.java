package view;

import controller.AnimationController;
import model.DisplayOptions;
import model.Model;

import javax.swing.*;
import java.awt.*;

public class AnimationControl extends JPanel implements View {

    private final JLabel step = new JLabel();
    private final JLabel substep = new JLabel();
    private final JSlider stepSlider = new JSlider();
    private final JButton playPauseButton;

    public AnimationControl() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //Speed control
        JPanel speedSliderContainer = new JPanel(new FlowLayout());
        JSlider speedSlider = new JSlider();
        speedSlider.addChangeListener(new AnimationController.SpeedSliderController());
        JLabel speedSliderLabel = new JLabel("Speed");
        speedSliderContainer.add(speedSliderLabel);
        speedSliderContainer.add(speedSlider);
        add(speedSliderContainer);
        speedSlider.setValue(5);

        //Steps
        JPanel stepContainerContainer = new JPanel();
        JPanel stepContainer = new JPanel();
        stepContainer.setLayout(new BoxLayout(stepContainer, BoxLayout.Y_AXIS));
        step.setText("");
        substep.setText("");
        step.setFont(new Font("Arial", Font.PLAIN, 20));
        substep.setFont(new Font("Arial", Font.PLAIN, 20));
        stepContainer.add(step);
        stepContainer.add(substep);
        stepContainerContainer.add(stepContainer);
        add(stepContainerContainer);

        //Step control
        stepSlider.addChangeListener(new AnimationController.StepSliderController());
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
        stepForward.addActionListener(new AnimationController.StepForwardController());
        stepBackward.addActionListener(new AnimationController.StepBackwardController());

        var gotoStart = new JButton("|<<");
        gotoStart.setText("\u23EE");
        gotoStart.setActionCommand("|<<");
        var gotoEnd = new JButton(">>|");
        gotoEnd.setText("\u23ED");
        gotoEnd.setActionCommand(">>|");
        playPauseButton = new JButton("play");
        gotoStart.addActionListener(new AnimationController.GotoStartController());
        gotoEnd.addActionListener(new AnimationController.GotoEndController());
        playPauseButton.addActionListener(new AnimationController.PlayPauseController());

        playbackContainer.add(gotoStart);
        playbackContainer.add(stepBackward);
        playbackContainer.add(playPauseButton);
        playbackContainer.add(stepForward);
        playbackContainer.add(gotoEnd);

        add(playbackContainer);

        setBorder(BorderFactory.createTitledBorder("Animation Control"));
    }

    @Override
    public void update() {
        Model model = Model.INSTANCE;
        if (model.isPlayingSteps()){
            playPauseButton.setActionCommand("pause");
            playPauseButton.setText("\u23F8");
        } else {
            playPauseButton.setActionCommand("play");
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
                substep.setText("");
            } else {
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
