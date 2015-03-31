package subsym.beertracker;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;

import subsym.gui.AIButton;
import subsym.gui.AICanvas;
import subsym.gui.AIGridCanvas;
import subsym.gui.AIGui;
import subsym.gui.AILabel;
import subsym.gui.AITextArea;
import subsym.gui.Direction;
import subsym.models.entity.TileEntity;

/**
 * Created by Patrick on 30.03.2015.
 */
public class BeerGui extends AIGui<TileEntity> implements TrackerListener {

  private JPanel mainPanel;
  private AIGridCanvas canvas;
  private AILabel missedLabel;
  private AILabel caughtLabel;
  private AILabel crashedLabel;
  private AILabel scoreLabel;
  private AIButton simulateButton;
  private AIButton resetButton;
  private Tracker tracker;

  public BeerGui(Tracker tracker) {
    this.tracker = tracker;
    buildFrame(mainPanel, null, null);
    tracker.addListener(this);

    InputMap inputMap = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = mainPanel.getActionMap();

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), Direction.LEFT);
    actionMap.put(Direction.LEFT, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        tracker.moveLeft(true);
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), Direction.RIGHT);
    actionMap.put(Direction.RIGHT, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        tracker.moveRight(true);
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "pull");
    actionMap.put("pull", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        tracker.pull();
      }
    });
  }

  @Override
  public int getDefaultCloseOperation() {
    return WindowConstants.DISPOSE_ON_CLOSE;
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(977, 490);
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

  @Override
  public void onCaught() {
    caughtLabel.setText("Caught: " + tracker.getCaught());
    updateScore();
  }

  private void updateScore() {
    scoreLabel.setText("Score: " + tracker.fitness());
  }

  @Override
  public void onAvoided() {
    missedLabel.setText("Avoided: " + tracker.getAvoided());
    updateScore();
  }

  @Override
  public void onCrash() {
    crashedLabel.setText("Crashed: " + tracker.getCrashed());
    updateScore();
  }
}
