package subsym.beertracker;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import subsym.Log;
import subsym.gui.AIButton;
import subsym.gui.AICanvas;
import subsym.gui.AIGridCanvas;
import subsym.gui.AIGui;
import subsym.gui.AILabel;
import subsym.gui.AISlider;
import subsym.gui.AITextArea;
import subsym.gui.Direction;
import subsym.models.entity.TileEntity;

/**
 * Created by Patrick on 30.03.2015.
 */
public class BeerGui extends AIGui<TileEntity> implements TrackerListener {

  private static final String TAG = BeerGui.class.getSimpleName();
  private final BeerGame game;
  private JPanel mainPanel;
  private AIGridCanvas canvas;
  private AILabel missedLabel;
  private AILabel caughtLabel;
  private AILabel crashedLabel;
  private AILabel scoreLabel;
  private AIButton playButton;
  private AIButton simulateButton;
  private AILabel timeLabel;
  private AISlider simulationSpeedSlider;
  private AIButton manualButton;
  private subsym.gui.AITextField inputField;
  private AILabel numGoodLabel;
  private AILabel numBadLabel;
  private AILabel goodPullLabel;
  private AILabel badPullLabel;

  public BeerGui(BeerGame game) {
    buildFrame(mainPanel, null, null);
    canvas.setOutlinesEnabled(true);
    this.game = game;
    game.setListener(this);
    game.setSimulationSpeed(simulationSpeedSlider.getValue());

    InputMap inputMap = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = mainPanel.getActionMap();

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), Direction.LEFT);
    actionMap.put(Direction.LEFT, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        game.moveLeft();
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), Direction.RIGHT);
    actionMap.put(Direction.RIGHT, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        game.moveRight();
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "pull");
    actionMap.put("pull", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        game.pull();
      }
    });
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        game.stop(() -> Log.v(TAG, "Exiting ..."));
      }
    });

    simulationSpeedSlider.addChangeListener(e -> game.setSimulationSpeed(simulationSpeedSlider.getValue()));
    simulateButton.addActionListener(e -> game.stop(() -> game.generateNew()));
    manualButton.addActionListener(e -> game.stop(() -> game.manual(inputField.getText())));
    playButton.addActionListener(e -> game.stop(() -> game.play()));

    scoreLabel.setVisible(false);
    onTick();
  }

  @Override
  public int getDefaultCloseOperation() {
    return WindowConstants.DISPOSE_ON_CLOSE;
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(1277, 600);
  }

  @Override
  protected void init() {
    buildFrame(canvas, null, null);
  }

  @Override
  public JPanel getMainPanel() {
    return mainPanel;
  }

  @Override
  public AICanvas getDrawingCanvas() {
    return canvas;
  }

  @Override
  public AITextArea getInputField() {
    throw new NotImplementedException();
  }

  public void onCaught() {
    caughtLabel.setText(String.format("Caught: %3d", game.getCaught()));
  }

  public void onAvoided() {
    missedLabel.setText(String.format("Avoided: %3d", game.getAvoided()));
  }

  public void onCrash() {
    crashedLabel.setText(String.format("Crashed: %3d", game.getCrashed()));
  }

  private void updateScore() {
    scoreLabel.setText(String.format("Score: %3f", game.getScore()));
    numBadLabel.setText(String.format("Bad: %3d", game.getNumBad()));
    numGoodLabel.setText(String.format("Good: %3d", game.getNumGood()));
    goodPullLabel.setText(String.format("Good Pull: %3d", game.getNumGoodPull()));
    badPullLabel.setText(String.format("Bad Pull: %3d", game.getNumBadPull()));
  }

  private void updateTime() {
    timeLabel.setText(String.format("Time: %3d", game.getTime()));
  }

  public void onTick() {
    onAvoided();
    onCaught();
    onCrash();
    updateScore();
    updateTime();
  }

  public void setInput(String input) {
    inputField.setText(input);
  }
}
