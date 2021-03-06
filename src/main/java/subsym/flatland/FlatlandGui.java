package subsym.flatland;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import subsym.gui.AIButton;
import subsym.gui.AICanvas;
import subsym.gui.AIGridCanvas;
import subsym.gui.AIGui;
import subsym.gui.AILabel;
import subsym.gui.AISlider;
import subsym.gui.AITextArea;
import subsym.gui.Direction;
import subsym.models.AIAdapter;
import subsym.models.entity.TileEntity;

/**
 * Created by Patrick on 26.03.2015.
 */
public class FlatlandGui extends AIGui<TileEntity> {

  private static final String TAG = FlatlandGui.class.getSimpleName();
  private static FlatlandGui instance;
  private AIGridCanvas<TileEntity> canvas;
  private Flatland flatland;
  private JPanel mainPanel;
  private AIButton simulateButton;
  private AIButton generateButton;
  private AIButton resetButton;
  private AILabel timeLabel;
  private AILabel foodLabel;
  private AILabel scoreLabel;
  private AILabel poisonLabel;
  private AISlider simulationSpeedSlider;
  private static final int TILE_SCALE = 30;
  private Dimension preferredSize;
  private boolean isBuilt = false;

  private void init(final Flatland flatland) {
    this.flatland = flatland;

    simulateButton.addActionListener(e -> new Thread(() -> flatland.simulate(true)).start());
    generateButton.addActionListener(e -> flatland.generateRandomBoard());

    InputMap inputMap = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = mainPanel.getActionMap();

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), Direction.LEFT);
    actionMap.put(Direction.LEFT, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        flatland.move(Direction.LEFT);
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), Direction.UP);
    actionMap.put(Direction.UP, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        flatland.move(Direction.UP);
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), Direction.RIGHT);
    actionMap.put(Direction.RIGHT, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        flatland.move(Direction.RIGHT);
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), Direction.DOWN);
    actionMap.put(Direction.DOWN, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        flatland.move(Direction.DOWN);
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), "simulate");
    actionMap.put("simulate", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        flatland.simulate(true);
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0), "generate");
    actionMap.put("generate", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        flatland.generateRandomBoard();
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "reset");
    actionMap.put("reset", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        flatland.reset();
      }
    });

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        flatland.terminate();
      }
    });
  }

  @Override
  public void setAdapter(AIAdapter<TileEntity> adapter) {
    super.setAdapter(adapter);
    preferredSize = new Dimension(adapter.getWidth() * TILE_SCALE, adapter.getHeight() * TILE_SCALE);
    setPreferredSize(preferredSize);
    Dimension minimumSize = new Dimension(650, 650);
    setMinimumSize(minimumSize);
    if (!isBuilt) {
      buildFrame(mainPanel, null, null);
      isBuilt = true;
    } else {
      pack();
    }
  }

  public int getSimulationSpeed() {
    return simulationSpeedSlider.getValue();
  }

  public void onTick(int time) {
    foodLabel.setText(String.format("Food: %2d/%2d", flatland.getFoodCount(), flatland.getMaxFoodCount()));
    poisonLabel.setText(String.format("Poison: %2d/%2d", flatland.getPoisonCount(), flatland.getMaxPoisonCount()));
    timeLabel.setText(String.format("Time: %2d", time));
    scoreLabel.setText(String.format("Score: %.3f", flatland.getScore()));
  }

  @Override
  public int getDefaultCloseOperation() {
    return WindowConstants.DISPOSE_ON_CLOSE;
  }

  @Override
  public Dimension getPreferredSize() {
    return preferredSize;
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
    throw new IllegalStateException("Not implemented!");
  }

  public static FlatlandGui get(Flatland flatland) {
    if (instance == null) {
      instance = new FlatlandGui();
    }
    instance.init(flatland);
    return instance;
  }
}
