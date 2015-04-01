package subsym.ailife;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;

import subsym.Log;
import subsym.ailife.entity.Robot;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.gui.AIButton;
import subsym.gui.AICanvas;
import subsym.gui.AIGridCanvas;
import subsym.gui.AIGui;
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
  private ArtificialNeuralNetwork ann;
  private JPanel mainPanel;
  private AIButton simulateButton;
  private AIButton generateButton;
  private AIButton resetButton;
  private Board<TileEntity> board;

  public AiLifeGui(Board<TileEntity> board, ArtificialNeuralNetwork ann) {
    this.ann = ann;
    this.board = board;
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
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), Direction.UP);
    actionMap.put(Direction.UP, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        robot.move(1);
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), Direction.RIGHT);
    actionMap.put(Direction.RIGHT, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        robot.move(2);
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "simulate");
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
  }

  private void generateRandomBoard() {
    board = AiLife.createAiLifeBoard(ArtificialNeuralNetwork.random().nextInt());
    initBoard(board);
  }

  public void simulate() {
    new Thread(() -> {
      if (ann == null) {
        Log.v(TAG, "No Artificial Neural Network present! Simulation exiting ...");
        return;
      }
      initBoard(board);
      for (int i = 0; i < 60; i++) {
        int indexOfBest = ann.getBestIndex(robot.getSensoryInput());
        robot.move(indexOfBest);
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  private void initBoard(Board<TileEntity> board) {
    canvas.setAdapter(board);
    robot = new Robot(0, 0, board);
    this.board.set(robot);
    this.board.notifyDataChanged();
  }

  @Override
  public int getDefaultCloseOperation() {
    return WindowConstants.DISPOSE_ON_CLOSE;
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(500, 500);
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

  public static void show(Board<TileEntity> board, ArtificialNeuralNetwork ann) {
    AiLifeGui gui = new AiLifeGui(board, ann);
    gui.simulate();
  }

  public static void demo() {
    Board<TileEntity> board = AiLife.createAiLifeBoard(1);
    AiLifeGui demo = new AiLifeGui(board, null);
    demo.robot = new Robot(0, 0, board);
    board.set(demo.robot);
    board.notifyDataChanged();
  }

}
