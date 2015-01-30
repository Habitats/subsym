package subsym.broids.gui;

import java.awt.*;

import javax.swing.*;

import subsym.Models.AIAdapter;
import subsym.gui.AICanvas;
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
  private JPanel mainPanel;

  private AITextArea inputField;
  private AICanvas drawingCanvas;
  private AITextField statusField;
  private AIContiniousScrollPane log;

  private AILabel sepField;
  private AILabel alignField;
  private AILabel cohField;
  private AILabel numBroidsField;
  private AILabel speedField;
  private AILabel radiusField;
  private AILabel obsticlesField;
  private AILabel predatorField;

  private AISlider alignSlider;
  private AISlider sepSlider;
  private AISlider cohSlider;
  private AISlider maxBroidSlider;
  private AISlider speedSlider;
  private AISlider radiusSlider;
  private AISlider obsticleSlider;
  private AISlider predatorSlider;

  private JTextField sepInput;
  private JTextField alignInput;
  private JTextField cohInput;
  private JTextField maxBroidInput;
  private JTextField speedInput;
  private JTextField radiusInput;
  private JTextField obsticleInput;
  private JTextField predatorInput;


  public BroidGui() {
    initValues();
    initFieldListeners();
    initSliderListeners();

    buildFrame(getMainPanel(), log, statusField);
  }

  private void initSliderListeners() {
    sepSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      int value = source.getValue();
      sepInput.setText(String.valueOf(value));
      AIAdapter.sepWeight = value / 100.;
    });
    cohSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      int value = source.getValue();
      cohInput.setText(String.valueOf(value));
      AIAdapter.cohWeight = value / 100.;
    });
    alignSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      int value = source.getValue();
      AIAdapter.alignWeight = value / 100.;
      alignInput.setText(String.valueOf(value));
    });
    speedSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(100);
      source.setMinimum(0);
      int value = source.getValue();
      AIAdapter.maxSpeed = value;
      speedInput.setText(String.valueOf(value));
    });
    radiusSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(3000);
      source.setMinimum(0);
      int value = source.getValue();
      radiusInput.setText(String.valueOf(value));
      AIAdapter.radius = value;
    });
    maxBroidSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(200);
      source.setMinimum(0);
      int value = source.getValue();
      maxBroidInput.setText(String.valueOf(value));
      AIAdapter.maxBroids = value;
    });
    predatorSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(200);
      source.setMinimum(0);
      int value = source.getValue();
      predatorInput.setText(String.valueOf(value));
      AIAdapter.numPredators = value;
    });
    obsticleSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(200);
      source.setMinimum(0);
      int value = source.getValue();
      obsticleInput.setText(String.valueOf(value));
      AIAdapter.numObsticles = value;
    });

  }

  private void initFieldListeners() {
    alignInput.addActionListener(e -> {
      String value = ((JTextField) e.getSource()).getText();
      alignSlider.setValue(Integer.parseInt(value));
    });
    cohInput.addActionListener(e -> {
      String value = ((JTextField) e.getSource()).getText();
      cohSlider.setValue(Integer.parseInt(value));
    });
    sepInput.addActionListener(e -> {
      String value = ((JTextField) e.getSource()).getText();
      sepSlider.setValue(Integer.parseInt(value));
    });
    maxBroidInput.addActionListener(e -> {
      String value = ((JTextField) e.getSource()).getText();
      maxBroidSlider.setValue(Integer.parseInt(value));
    });
    radiusInput.addActionListener(e -> {
      String value = ((JTextField) e.getSource()).getText();
      radiusSlider.setValue(Integer.parseInt(value));
    });
    speedInput.addActionListener(e -> {
      String value = ((JTextField) e.getSource()).getText();
      speedSlider.setValue(Integer.parseInt(value));
    });
    predatorInput.addActionListener(e -> {
      String value = ((JTextField) e.getSource()).getText();
      predatorSlider.setValue(Integer.parseInt(value));
    });
    obsticleInput.addActionListener(e -> {
      String value = ((JTextField) e.getSource()).getText();
      obsticleSlider.setValue(Integer.parseInt(value));
    });

  }

  private void initValues() {
    sepInput.setText(String.valueOf(AIAdapter.sepWeight));
    sepSlider.setValue((int) AIAdapter.sepWeight);

    alignInput.setText(String.valueOf(AIAdapter.alignWeight));
    alignSlider.setValue((int) AIAdapter.alignWeight);

    cohInput.setText(String.valueOf(AIAdapter.cohWeight));
    cohSlider.setValue((int) AIAdapter.cohWeight);

    maxBroidInput.setText(String.valueOf(AIAdapter.maxBroids));
    maxBroidSlider.setValue(AIAdapter.maxBroids);

    speedInput.setText(String.valueOf(AIAdapter.maxSpeed));
    speedSlider.setValue(AIAdapter.maxSpeed);

    radiusInput.setText(String.valueOf(AIAdapter.radius));
    radiusSlider.setValue(AIAdapter.radius);

    predatorInput.setText(String.valueOf(AIAdapter.numPredators));
    predatorSlider.setValue(AIAdapter.numPredators);

    obsticleInput.setText(String.valueOf(AIAdapter.numObsticles));
    obsticleSlider.setValue(AIAdapter.numObsticles);
  }


  @Override
  protected int getDefaultCloseOperation() {
    return WindowConstants.EXIT_ON_CLOSE;
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