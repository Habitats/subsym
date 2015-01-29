package subsym.broids.gui;

import java.awt.*;

import javax.swing.*;

import subsym.Models.AIAdapter;
import subsym.gui.AIButton;
import subsym.gui.AICanvas;
import subsym.gui.AICheckBox;
import subsym.gui.AIComboBox;
import subsym.gui.AIContiniousScrollPane;
import subsym.gui.AIGui;
import subsym.gui.AILabel;
import subsym.gui.AISlider;
import subsym.gui.AITextArea;
import subsym.gui.AITextField;


/**
 * Created by Patrick on 23.08.2014.
 */
public class BroidGui extends AIGui {

  private static final String TAG = BroidGui.class.getSimpleName();
  private AIButton resetButton;
  private AIButton runButton;
  private AIButton stepButton;

  private JPanel mainPanel;

  private AITextArea inputField;
  private AICanvas drawingCanvas;
  private AILabel sepField;
  private AILabel alignField;
  private AISlider alignSlider;
  private AISlider sepSlider;
  private AILabel cohField;
  private AISlider cohSlider;
  private AILabel numBroidsField;
  private AILabel speedField;
  private AISlider numBroidsSlider;
  private AISlider speedSlider;
  private AILabel radiusField;
  private AISlider radiusSlider;
  private AITextField logField;
  private AITextField statusField;
  private AITextField kField;
  private AIComboBox sampleComboBox;
  private AISlider stepSlider;
  private AICheckBox labelsCheckbox;
  private AIContiniousScrollPane log;
  private AICheckBox stepCheckBox;
  private AIButton readFromFileButton;

  public BroidGui() {
    buildFrame(getMainPanel(), log, statusField);
    sepSlider.addChangeListener(e -> AIAdapter.sepWeight = ((AISlider) e.getSource()).getValue() / 1000.);
    cohSlider.addChangeListener(e -> AIAdapter.cohWeight = ((AISlider) e.getSource()).getValue() / 100.);
    alignSlider.addChangeListener(e -> AIAdapter.alignWeight = ((AISlider) e.getSource()).getValue() / 100.);
    speedSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(100);
      source.setMinimum(0);
      AIAdapter.maxSpeed = source.getValue();
    });
    radiusSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(3000);
      source.setMinimum(0);
      AIAdapter.radius = source.getValue();
    });
    numBroidsSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(200);
      source.setMinimum(0);
      AIAdapter.maxBroids = source.getValue();
    });

  }


  @Override
  protected int getDefaultCloseOperation() {
    return WindowConstants.DISPOSE_ON_CLOSE;
  }

  @Override
  protected Dimension getPreferredSize() {
    return new Dimension(1500, 1200);
  }

  @Override
  public JPanel getMainPanel() {
    return mainPanel;
  }

  @Override
  public AICanvas getDrawingCanvas() {
    return drawingCanvas;
  }

  @Override
  public AITextArea getInputField() {
    return inputField;
  }

}