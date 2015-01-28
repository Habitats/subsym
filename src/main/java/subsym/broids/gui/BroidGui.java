package subsym.broids.gui;

import java.awt.*;

import javax.swing.*;

import subsym.gui.AIButton;
import subsym.gui.AICanvas;
import subsym.gui.AICheckBox;
import subsym.gui.AIComboBox;
import subsym.gui.AIContiniousScrollPane;
import subsym.gui.AIGui;
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