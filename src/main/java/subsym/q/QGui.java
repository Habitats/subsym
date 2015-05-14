package subsym.q;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

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
  private AIButton bestButton;
  private JLabel heapUsedLabel;
  private JLabel heapLabel;
  private AIButton panicButton;
  private JLabel iterationRate;
  private FlatlandQSimulator simulator;
  private String TAG = QGui.class.getSimpleName();

  public QGui() {
    QPreferences.SCENARIOS.forEach(scenarioCombobox::addItem);
    runForverCheckbox.addActionListener(e -> QPreferences.RUN_FOREVER = runForverCheckbox.isSelected());
    drawArrowsCheckbox.addActionListener(e -> QPreferences.DRAW_ARROWS = drawArrowsCheckbox.isSelected());
    scenarioCombobox.addActionListener(e -> QPreferences.SCENARIO = scenarioCombobox.getSelectedItem().toString());
    intermediateCheckbox.addActionListener(e -> QPreferences.INTERMEDIATE_SIMULATIONS = intermediateCheckbox.isSelected());

    stopButton.addActionListener(e -> stop());
    bestButton.addActionListener(e -> new Thread(() -> simulator.simulateBestState()).start());
    trainButton.addActionListener(e -> flatland());
    restartButton.addActionListener(e -> new Thread(() -> simulator.simulateCurrentState(true)).start());
    panicButton.addActionListener(e -> System.gc());
    runForverCheckbox.setSelected(QPreferences.RUN_FOREVER);
    scenarioCombobox.setSelectedItem(QPreferences.SCENARIO);
    drawArrowsCheckbox.setSelected(QPreferences.DRAW_ARROWS);
    intermediateCheckbox.setSelected(QPreferences.INTERMEDIATE_SIMULATIONS);
    iterationsInput.setText(String.valueOf(QPreferences.MAX_ITERATION));

    new Timer().scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        updateHeapStats();
        iterationRate.setText(String.format("R: %5.3f", QPreferences.ITERATION_RATE));
      }
    }, 0, 200);

    QPreferences.setProgressBar(progressBar);
    buildFrame(mainPanel, null, null);
  }

  private void updateHeapStats() {
    long heapSize = Runtime.getRuntime().totalMemory() / (1024 * 1024);
    long heapMaxSize = Runtime.getRuntime().maxMemory() / (1024 * 1024);
    long heapFreeSize = Runtime.getRuntime().freeMemory() / (1024 * 1024);
    heapLabel.setText(String.format("Heap: %4d/%4d \t Free: %4d", heapSize, heapMaxSize, heapFreeSize));

    if (heapSize == heapMaxSize && heapFreeSize < 200) {
      QPreferences.SHOULD_TERMINATE = true;
      Log.v(TAG, "Oh shit oh shit oh shit!");
    }
  }

  private void stop() {
    QPreferences.SHOULD_TERMINATE = true;
  }

  private void flatland() {
    QPreferences.MAX_ITERATION = Integer.parseInt(iterationsInput.getText());
    QPreferences.RANDOM_ITERATION_THRESHOLD  = Math.max(0, QPreferences.MAX_ITERATION - 1500);
    if (simulator == null || !simulator.isRunning()) {
      if (simulator == null) {
        simulator = new FlatlandQSimulator();
        new Thread(simulator).start();

      } else {
        simulator.clear();
        new Thread(simulator).start();
      }
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
    return new Dimension(450, 200);
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
