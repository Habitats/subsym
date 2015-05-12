package subsym.q;

import java.awt.*;

import javax.swing.*;

import subsym.Log;
import subsym.flatland.FlatlandQSimulator;
import subsym.gui.AIButton;
import subsym.gui.AICanvas;
import subsym.gui.AIGui;
import subsym.gui.AITextArea;

/**
 * Created by mail on 11.05.2015.
 */
public class QGui extends AIGui {

  private JCheckBox drawArrowsCheckbox;
  private JCheckBox runForverCheckbox;
  private JComboBox scenarioCombobox;
  private AIButton trainButton;
  private JPanel mainPanel;
  private JProgressBar progressBar;
  private AIButton stopButton;
  private JCheckBox intermediateCheckbox;
  private JTextField iterationsInput;
  private AIButton restartButton;
  private FlatlandQSimulator simulator;
  private String TAG = QGui.class.getSimpleName();

  public QGui() {
    QPreferences.SCENARIOS.forEach(scenarioCombobox::addItem);
    runForverCheckbox.addActionListener(e -> QPreferences.RUN_FOREVER = runForverCheckbox.isSelected());
    drawArrowsCheckbox.addActionListener(e -> QPreferences.DRAW_ARROWS = drawArrowsCheckbox.isSelected());
    scenarioCombobox.addActionListener(e -> QPreferences.SCENARIO = scenarioCombobox.getSelectedItem().toString());
    intermediateCheckbox.addActionListener(e -> QPreferences.INTERMEDIATE_SIMULATIONS = intermediateCheckbox.isSelected());

    stopButton.addActionListener(e -> stop());
    trainButton.addActionListener(e -> flatland());
    restartButton.addActionListener(e -> new Thread(() -> simulator.simulateCurrentState(true)).start());
    runForverCheckbox.setSelected(QPreferences.RUN_FOREVER);
    scenarioCombobox.setSelectedItem(QPreferences.SCENARIO);
    drawArrowsCheckbox.setSelected(QPreferences.DRAW_ARROWS);
    intermediateCheckbox.setSelected(QPreferences.INTERMEDIATE_SIMULATIONS);
    iterationsInput.setText(String.valueOf(QPreferences.MAX_ITERATION));

    QPreferences.setProgressBar(progressBar);
    buildFrame(mainPanel, null, null);
  }

  private void stop() {
    QPreferences.SHOULD_TERMINATE = true;
    simulator = null;
    System.gc();
  }

  private void flatland() {
    QPreferences.MAX_ITERATION = Integer.parseInt(iterationsInput.getText());
    if (simulator == null || !simulator.isRunning()) {
      if (simulator != null) {
        simulator.clear();
      }
      simulator = new FlatlandQSimulator();
      new Thread(simulator).start();
    } else {
      Log.v(TAG, "Already running!");
    }
  }

  @Override
  public int getDefaultCloseOperation() {
    return WindowConstants.DISPOSE_ON_CLOSE;
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(500, 200);
  }

  @Override
  protected void init() {
    buildFrame(getMainPanel(), null, null);
  }

  @Override
  public JPanel getMainPanel() {
    return mainPanel;
  }

  @Override
  public AICanvas getDrawingCanvas() {
    return null;
  }

  @Override
  public AITextArea getInputField() {
    throw new IllegalStateException("Not implemented!");
  }
}
