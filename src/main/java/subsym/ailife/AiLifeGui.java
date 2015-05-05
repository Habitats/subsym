package subsym.ailife;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.*;

import subsym.Log;
import subsym.Main;
import subsym.ailife.entity.Food;
import subsym.ailife.entity.Poison;
import subsym.ailife.entity.Robot;
import subsym.gui.AIButton;
import subsym.gui.AICanvas;
import subsym.gui.AIGridCanvas;
import subsym.gui.AIGui;
import subsym.gui.AILabel;
import subsym.gui.AISlider;
import subsym.gui.AITextArea;
import subsym.gui.Direction;
import subsym.models.Board;
import subsym.models.entity.TileEntity;

/**
 * Created by Patrick on 26.03.2015.
 */
public class AiLifeGui extends AIGui<TileEntity> {

  private static final String TAG = AiLifeGui.class.getSimpleName();
  private AIGridCanvas<TileEntity> canvas;
  private Robot robot;
  private AiLifeSimulator simulator;
  private JPanel mainPanel;
  private AIButton simulateButton;
  private AIButton generateButton;
  private AIButton resetButton;
  private AILabel timeLabel;
  private AILabel poisonLabel;
  private AILabel foodLabel;
  private AISlider simulationSpeedSlider;
  private AILabel scoreLabel;
  private Board<TileEntity> board;
  private boolean shouldStop = false;
  private long numFood;
  private long numPoison;

  public AiLifeGui(Board<TileEntity> board, AiLifeSimulator simulator, Robot robot) {
    this.simulator = simulator;
    this.board = board;
    this.robot = robot;
    buildFrame(mainPanel, null, null);

    simulateButton.addActionListener(e -> simulate());
    generateButton.addActionListener(e -> generateRandomBoard());

    InputMap inputMap = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = mainPanel.getActionMap();

    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), Direction.LEFT);
    actionMap.put(Direction.LEFT, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        robot.move(0);
        printBoard();
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), Direction.UP);
    actionMap.put(Direction.UP, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        robot.move(1);
        printBoard();
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), Direction.RIGHT);
    actionMap.put(Direction.RIGHT, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        robot.move(2);
        printBoard();
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), Direction.DOWN);
    actionMap.put(Direction.DOWN, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        robot.move(3);
        printBoard();
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), "simulate");
    actionMap.put("simulate", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        simulate();
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0), "generate");
    actionMap.put("generate", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        generateRandomBoard();
      }
    });

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        shouldStop = true;
      }
    });
  }

  private void simulate() {
    simulate(() -> Log.v(TAG, "Simulation done!"));
  }

  private void printBoard() {
    Log.v(TAG, board.getFormattedBoard());
  }

  private void generateRandomBoard() {
    int seed = Main.random().nextInt();
    Log.v(TAG, "Board seed: " + seed);
    board = AiLifeAnnSimulator.createAiLifeBoard(seed);
    initBoard(board);
    Log.v(TAG, board.getFormattedBoard());
    onTick(0);
  }

  public void simulate(Runnable callback) {
    new Thread(() -> {
      initBoard(board);
      for (int i = 1; i <= simulator.getMaxSteps(); i++) {
        simulator.move(robot);
        onTick(i);
        try {
          Thread.sleep(simulationSpeedSlider.getValue());
        } catch (InterruptedException e) {
        }
        if (shouldStop) {
          Log.v(TAG, "Exiting ...");
          return;
        }
      }
      callback.run();
    }).start();
  }


  private void initBoard(Board<TileEntity> board) {
    canvas.setAdapter(board);
    numFood = board.getItems().stream().filter(i -> i instanceof Food).count();
    numPoison = board.getItems().stream().filter(i -> i instanceof Poison).count();
    robot = new Robot(robot.getStartX(), robot.getStartY(), board, robot.isDirectional());
    this.board.set(robot);
    this.board.notifyDataChanged();
    if (isVisible()) {
      onTick(0);
    }
  }

  public void onTick(int time) {
    foodLabel.setText(String.format("Food: %2d/%2d", robot.getFoodCount(), numFood));
    poisonLabel.setText(String.format("Poison: %2d/%2d", robot.getPoisonCount(), numPoison));
    timeLabel.setText(String.format("Time: %2d", time));
    scoreLabel.setText(String.format("Score: %.3f", robot.getScore()));
  }

  @Override
  public int getDefaultCloseOperation() {
    return WindowConstants.DISPOSE_ON_CLOSE;
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(650, 650);
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

  public static void demo(Board<TileEntity> board, Robot robot) {
    AiLifeGui demo = new AiLifeGui(board, null, robot);
    demo.initBoard(board);
  }

  public static void simulate(List<Board<TileEntity>> boards, AiLifeSimulator ann, Runnable callback, Robot robot) {
    AiLifeGui gui = new AiLifeGui(boards.get(0), ann, robot);
    simulate(boards, callback, gui);
  }

  private static void simulate(List<Board<TileEntity>> boards, Runnable callback, AiLifeGui gui) {
    if (boards.isEmpty()) {
      callback.run();
      return;
    }
    gui.board = boards.remove(0);
    gui.simulate(() -> simulate(boards, callback, gui));
  }
}
