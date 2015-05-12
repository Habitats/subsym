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
  private FlatlandQSimulator flatland;
  private String TAG = QGui.class.getSimpleName();

  public QGui() {
    QPreferences.SCENARIOS.forEach(scenarioCombobox::addItem);
    scenarioCombobox.addActionListener(e -> QPreferences.SCENARIO = scenarioCombobox.getSelectedItem().toString());
    drawArrowsCheckbox.addActionListener(e -> QPreferences.DRAW_ARROWS = drawArrowsCheckbox.isSelected());
    runForverCheckbox.addActionListener(e -> QPreferences.RUN_FOREVER = runForverCheckbox.isSelected());

    drawArrowsCheckbox.setSelected(QPreferences.DRAW_ARROWS);
    runForverCheckbox.setSelected(QPreferences.RUN_FOREVER);
    scenarioCombobox.setSelectedItem(QPreferences.SCENARIO);
    trainButton.addActionListener(e -> flatland());
    stopButton.addActionListener(e -> stop());

    QPreferences.setProgressBar(progressBar);
    buildFrame(mainPanel, null, null);
  }

  private void stop() {
    QPreferences.SHOULD_TERMINATE = true;
    flatland = null;
    System.gc();
  }

  private void flatland() {
    if (flatland == null || !flatland.isRunning()) {
      if(flatland != null)
      flatland.clear();
      flatland = new FlatlandQSimulator();
      new Thread(flatland).start();
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
