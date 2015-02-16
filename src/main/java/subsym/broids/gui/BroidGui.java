package subsym.broids.gui;

import java.awt.*;

import javax.swing.*;

import subsym.broids.BroidAdapter;
import subsym.broids.Broids;
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
public class BroidGui extends AIGui {

  private static final String TAG = BroidGui.class.getSimpleName();

  private static final int COH_HIGH = 100;
  private static final int COH_LOW = 10;
  private static final int ALIGN_HIGH = 100;
  private static final int ALIGN_LOW = 10;
  private static final int SEP_HIGH = 80;
  private static final int SEP_LOW = 0;

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

  private Broids broids;

  public BroidGui() {
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
        .addActionListener(e -> BroidAdapter.WRAP_AROUND_PHYSICS_ENABLED = ((JCheckBox) e.getSource()).isSelected());
    wrapAroundPhysicsCheckbox.setSelected(false);
    enableVectorsCheckbox
        .addActionListener(e -> BroidAdapter.VECTORS_ENABLED = ((JCheckBox) e.getSource()).isSelected());

    spawnObsticleButton.addActionListener(e -> broids.spawnObsticle());
    spawnPredButton.addActionListener(e -> broids.spawnPredator());
    clearButton.addActionListener(e -> broids.clearAll());

    buildFrame(getMainPanel(), log, statusField);
  }

  private void initScenario(double sepWeight, double alignWeight, double cohWeight) {
    BroidAdapter.setSepWeight(sepWeight);
    BroidAdapter.setAlignWeight(alignWeight);
    BroidAdapter.setCohWeight(cohWeight);
    updateWeights();
  }

  private void updateWeights() {
    sepInput.setText(String.valueOf(BroidAdapter.getSepWeight()));
    sepSlider.setValue(BroidAdapter.getSepWeight());
    
    obsticleSepInput.setText(String.valueOf(BroidAdapter.getObsticleSepWeight()));
    obsticleSepSlider.setValue(BroidAdapter.getObsticleSepWeight());

    alignInput.setText(String.valueOf((int) BroidAdapter.getAlignWeight()));
    alignSlider.setValue(BroidAdapter.getAlignWeight());

    cohInput.setText(String.valueOf(BroidAdapter.getCohWeight()));
    cohSlider.setValue(BroidAdapter.getCohWeight());
  }

  private void initSliderListeners() {
    sepSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      int value = source.getValue();
      sepInput.setText(String.valueOf(value));
      BroidAdapter.setSepWeight(value);
    });
    obsticleSepSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      int value = source.getValue();
      obsticleSepInput.setText(String.valueOf(value));
      BroidAdapter.setObsticleSepWeight(value);
    });
    cohSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      int value = source.getValue();
      cohInput.setText(String.valueOf(value));
      BroidAdapter.setCohWeight(value);
    });
    alignSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      int value = source.getValue();
      alignInput.setText(String.valueOf(value));
      BroidAdapter.setAlignWeight(value);
    });
    speedSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(100);
      source.setMinimum(0);
      int value = source.getValue();
      BroidAdapter.maxSpeed = value;
      speedInput.setText(String.valueOf(value));
    });
    radiusSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(3000);
      source.setMinimum(0);
      int value = source.getValue();
      radiusInput.setText(String.valueOf(value));
      BroidAdapter.radius = value;
    });
    maxBroidSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(1000);
      source.setMinimum(0);
      int value = source.getValue();
      maxBroidInput.setText(String.valueOf(value));
      BroidAdapter.maxBroids = value;
    });
    predatorSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(200);
      source.setMinimum(0);
      int value = source.getValue();
      predatorInput.setText(String.valueOf(value));
      BroidAdapter.numPredators = value;
    });
    obsticleSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(200);
      source.setMinimum(0);
      int value = source.getValue();
      obsticleInput.setText(String.valueOf(value));
      BroidAdapter.numObsticles = value;
    });
    simSpeedSlider.addChangeListener(e -> {
      AISlider source = (AISlider) e.getSource();
      source.setMaximum(100);
      source.setMinimum(1);
      int value = source.getValue();
      simSpeedInput.setText(String.valueOf(value));
      Broids.updateFrequency = value;
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
    predatorInput.addActionListener(e -> {
      String value = ((JTextField) e.getSource()).getText();
      predatorSlider.setValue(Integer.parseInt(value));
    });
    obsticleInput.addActionListener(e -> {
      String value = ((JTextField) e.getSource()).getText();
      obsticleSlider.setValue(Integer.parseInt(value));
    });
    simSpeedInput.addActionListener(e -> {
      String value = ((JTextField) e.getSource()).getText();
      simSpeedSlider.setValue(Integer.parseInt(value));
    });

  }

  private void initValues() {
    updateWeights();

    maxBroidInput.setText(String.valueOf(BroidAdapter.maxBroids));
    maxBroidSlider.setValue(BroidAdapter.maxBroids);

    speedInput.setText(String.valueOf(BroidAdapter.maxSpeed));
    speedSlider.setValue(BroidAdapter.maxSpeed);

    radiusInput.setText(String.valueOf(BroidAdapter.radius));
    radiusSlider.setValue(BroidAdapter.radius);

    predatorInput.setText(String.valueOf(BroidAdapter.numPredators));
    predatorSlider.setValue(BroidAdapter.numPredators);

    obsticleInput.setText(String.valueOf(BroidAdapter.numObsticles));
    obsticleSlider.setValue(BroidAdapter.numObsticles);

    simSpeedInput.setText(String.valueOf(Broids.updateFrequency));
    simSpeedSlider.setValue((int) Broids.updateFrequency);
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

  public void addListener(Broids broids) {
    this.broids = broids;
  }
}