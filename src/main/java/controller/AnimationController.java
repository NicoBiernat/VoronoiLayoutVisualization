package controller;

import model.Model;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnimationController {

    public static class SpeedSliderController implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent changeEvent) {
            int newValue = ((JSlider) changeEvent.getSource()).getValue();
            Model.INSTANCE.setPlaybackSpeed(1000 / (newValue + 1));
        }
    }

    public static class StepSliderController implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent changeEvent) {
            if (inputDisabled()) return;
            Model.INSTANCE.setStep(((JSlider) changeEvent.getSource()).getValue());
        }
    }

    public static class StepForwardController implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (inputDisabled()) return;
            pauseIfPlaying();
            Model.INSTANCE.nextStepOrSubstep();
        }
    }

    public static class StepBackwardController implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (inputDisabled()) return;
            pauseIfPlaying();
            Model.INSTANCE.previousStepOrSubstep();
        }
    }

    public static class GotoStartController implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (inputDisabled()) return;
            pauseIfPlaying();
            Model.INSTANCE.firstStep();
        }
    }

    public static class GotoEndController implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (inputDisabled()) return;
            pauseIfPlaying();
            Model.INSTANCE.lastStep();
        }
    }

    public static class PlayPauseController implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (inputDisabled()) return;
            switch (actionEvent.getActionCommand()) {
                case "play":
                    Model.INSTANCE.playSteps();
                    break;
                case "pause":
                    Model.INSTANCE.pauseSteps();
                    break;
            }
        }
    }

    private static void pauseIfPlaying() {
        if (Model.INSTANCE.playingThread != null) {
            Model.INSTANCE.pauseSteps();
        }
    }

    private static boolean inputDisabled() {
        return Model.INSTANCE.getLloydSteps() == null;
    }
}
