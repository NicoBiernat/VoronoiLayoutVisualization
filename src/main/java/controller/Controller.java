package controller;

import model.DisplayOptions;
import model.Model;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

public class Controller implements MouseListener, ActionListener {

  private Model model;

  public Controller(Model model) {

    this.model = model;

    //FIXME: dev override
//    fileSelected(new File("./TestGraph.elkt")); // not everyone is using Windows :/
  }

  public void fileSelected(File file){ model.loadFile(file); }

  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    switch (actionEvent.getActionCommand()) {
      case "<||":
        model.previousStepOrSubstep();
        break;
      case "||>":
        model.nextStepOrSubstep();
        break;
      case "|<<":
        model.firstStep();
        break;
      case ">>|":
        model.lastStep();
        break;
      case "|>":
        model.playSteps();
        break;
      case "||":
        model.pauseSteps();
        break;
    }
  }


  @Override
  public void mouseClicked(MouseEvent mouseEvent) {

  }

  @Override
  public void mousePressed(MouseEvent mouseEvent) {

  }

  @Override
  public void mouseReleased(MouseEvent mouseEvent) {

  }

  @Override
  public void mouseEntered(MouseEvent mouseEvent) {

  }

  @Override
  public void mouseExited(MouseEvent mouseEvent) {

  }

  public void speedSliderChanged(int newValue) {
    model.setPlaybackSpeed(1000/(newValue+1));
  }

  public void stepSliderChanged(int newStep) { model.setStep(newStep);}

  public void setDisplayOption(DisplayOptions option, boolean value) {
    model.setDisplayOption(option,value);
  }
}
