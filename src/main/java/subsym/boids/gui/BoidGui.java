package subsym.boids.gui;

import java.awt.*;

import javax.swing.*;

import subsym.boids.BoidAdapter;
import subsym.boids.Boids;
import subsym.gui.AIButton;
import subsym.gui.AICanvas;
import subsym.gui.AICheckBox;
import subsym.gui.AIContiniousScrollPane;
import subsym.gui.AIGui;
import subsym.gui.AILabel;
import subsym.gui.AISlider;
import subsym.gui.AITextArea;
import subsym.gui.AITextField;


/**
 * Created by Patrick on 23.08.2014.
 */
public class BoidGui extends AIGui {

  private static final String TAG = BoidGui.class.getSimpleName();

  private static final int COH_HIGH = 100;
  private static final int COH_LOW = 10;
  private static final int ALIGN_HIGH = 100;
  private static final int ALIGN_LOW = 10;
  private static final int SEP_HIGH = 80;
  private static final int SEP_LOW = 0;

  private JPanel mainPanel;

  private AITextArea inputField;
  private BoidCanvas drawingCanvas;
  private AITextField statusField;
  private AIContiniousScrollPane log;

  private AILabel sepField;
  private AILabel alignField;
  private AILabel cohField;
  private AILabel numBroidsField;
  private AILabel speedField;
  private AILabel radiusField;

  private AISlider alignSlider;
  private AISlider sepSlider;
  private AISlider cohSlider;
  private AISlider maxBroidSlider;
  private AISlider speedSlider;
  private AISlider radiusSlider;

  private JTextField sepInput;
  private JTextField alignInput;
  private JTextField cohInput;
  private JTextField maxBroidInput;
  private JTextField speedInput;
  private JTextField radiusInput;
  private AIButton spawnPredButton;
  private AIButton spawnObsticleButton;
  private AIButton clearButton;
  private AILabel simSpeedField;
  private AISlider simSpeedSlider;
  private JTextField simSpeedInput;
  private AIButton scenario1Button;
  private AIButton scenario6Button;
  private AIButton scenario2Button;
  private AIButton scenario3Button;
  private AIButton scenario4Button;
  private AIButton scenario5Button;
  private AICheckBox wrapAroundPhysicsCheckbox;
  public AILabel sepValField;
  public AILabel alignValField;
  public AILabel cohValField;
  private AICheckBox enableVectorsCheckbox;
  private AILabel scaleField;
  private AISlider scaleSLider;
  private AILabel obsticleSepField;
  private AISlider obsticleSepSlider;
  private JTextField obsticleSepInput;

  private Boids boids;

  public BoidGui() {
    initValues();
    initFieldListeners();
    initSliderListeners();

    scenario1Button.addActionListener(e -> initScenario(SEP_LOW, ALIGN_LOW, COH_HIGH));
    scenario2Button.addActionListener(e -> initScenario(SEP_LOW, ALIGN_HIGH, COH_LOW));
    scenario3Button.addActionListener(e -> initScenario(SEP_HIGH, ALIGN_LOW, COH_LOW));
    scenario4Button.addActionListener(e -> initScenario(SEP_LOW, ALIGN_LOW, COH_HIGH));
    scenario5Button.addActionListener(e -> initScenario(SEP_HIGH, ALIGN_LOW, COH_HIGH));
    scenario6Button.addActionListener(e -> initScenario(SEP_HIGH, ALIGN_HIGH, COH_LOW));

    wrapAroundPhysicsCheckbox.setSelected(true);
    wrapAroundPhysicsCheckbox
        .addActionListener(e -> BoidAdapter.WRAP_AROUND_PHYSICS_ENABLED = ((JCheckBox) e.getSource()).isSelected());
    wrapAroundPhysicsCheckbox.setSelected(false);
    enableVectorsCheckbox
        .addActionListener(e -> BoidAdapter.VECTORS_ENABLED = ((JCheckBox) e.getSource()).isSelected());

    spawnObsticleButton.addActionListener(e -> boids.spawnObsticle());
    spawnPredButton.addActionListener(e -> boids.spawnPredator());
    clearButton.addActionListener(e -> boids.clearAll());

    buildFrame(getMainPanel(), log, statusField);
  }

  private void initScenario(double sepWeight, double alignWeight, double cohWeight) {
    BoidAdapter.setSepWeight(sepWeight);
    BoidAdapter.setAlignWeight(alignWeight);
    BoidAdapter.setCohWeight(cohWeight);
    updateWeights();
  }

  private void updateWeights() {
    sepInput.setText(String.valueOf(BoidAdapter.getSepWeight()));
    sepSlider.setValue(BoidAdapter.getSepWeight());

    obsticleSepInput.setText(String.valueOf(BoidAdapter.getObsticleSepWeight()));
    obsticleSepSlider.setValue(BoidAdapter.getObsticleSepWeight());

    alignInput.setText(String.valueOf(BoidAdapter.getAlignWeight()));
    alignSlider.setValue(BoidAdapter.getAlignWeight());

    cohInput.setText(String.valueOf(BoidAdapter.getCohWeight()));
    cohSlider.setValue(BoidAdapter.getCohWeight());
  }

  private void initSliderListeners() {
    sepSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      int value = source.getValue();
      sepInput.setText(String.valueOf(value));
      BoidAdapter.setSepWeight(value);
    });
    obsticleSepSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      int value = source.getValue();
      obsticleSepInput.setText(String.valueOf(value));
      BoidAdapter.setObsticleSepWeight(value);
    });
    cohSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      int value = source.getValue();
      cohInput.setText(String.valueOf(value));
      BoidAdapter.setCohWeight(value);
    });
    alignSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      int value = source.getValue();
      alignInput.setText(String.valueOf(value));
      BoidAdapter.setAlignWeight(value);
    });
    speedSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(100);
      source.setMinimum(0);
      int value = source.getValue();
      BoidAdapter.maxSpeed = value;
      speedInput.setText(String.valueOf(value));
    });
    radiusSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(3000);
      source.setMinimum(0);
      int value = source.getValue();
      radiusInput.setText(String.valueOf(value));
      BoidAdapter.radius = value;
    });
    maxBroidSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(1000);
      source.setMinimum(0);
      int value = source.getValue();
      maxBroidInput.setText(String.valueOf(value));
      BoidAdapter.maxBroids = value;
    });
    simSpeedSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(100);
      source.setMinimum(1);
      int value = source.getValue();
      simSpeedInput.setText(String.valueOf(value));
      Boids.updateFrequency = value;
    });
    scaleSLider.addChangeListener(e -> updateScale(((AISlider) e.getSource()).getValue() / 50.));

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
    obsticleSepInput.addActionListener(e -> {
      String value = ((JTextField) e.getSource()).getText();
      obsticleSepSlider.setValue(Integer.parseInt(value));
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
    simSpeedInput.addActionListener(e -> {
      String value = ((JTextField) e.getSource()).getText();
      simSpeedSlider.setValue(Integer.parseInt(value));
    });

  }

  private void initValues() {
    updateWeights();

    maxBroidInput.setText(String.valueOf(BoidAdapter.maxBroids));
    maxBroidSlider.setValue(BoidAdapter.maxBroids);

    speedInput.setText(String.valueOf(BoidAdapter.maxSpeed));
    speedSlider.setValue(BoidAdapter.maxSpeed);

    radiusInput.setText(String.valueOf(BoidAdapter.radius));
    radiusSlider.setValue(BoidAdapter.radius);

    simSpeedInput.setText(String.valueOf(Boids.updateFrequency));
    simSpeedSlider.setValue((int) Boids.updateFrequency);
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

  public void addListener(Boids boids) {
    this.boids = boids;
  }
}