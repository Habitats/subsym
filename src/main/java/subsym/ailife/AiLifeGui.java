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
    canvas.setAdapter(board);
    buildFrame(mainPanel, null, null);

    simulateButton.addActionListener(e -> simulate());
    generateButton.addActionListener(e -> generateRandomBoard());

    InputMap inputMap = mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), Direction.LEFT);
    mainPanel.getActionMap().put(Direction.LEFT, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        robot.move(0);
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), Direction.UP);
    mainPanel.getActionMap().put(Direction.UP, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        robot.move(1);
      }
    });
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), Direction.RIGHT);
    mainPanel.getActionMap().put(Direction.RIGHT, new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        robot.move(2);
      }
    });
    simulate();
  }

  private void generateRandomBoard() {
    board = AiLife.createAiLifeBoard(ArtificialNeuralNetwork.random().nextInt());
    canvas.setAdapter(board);
    robot = new Robot(0, 0, board);
    board.set(robot);
    board.notifyDataChanged();
  }

  public void simulate() {
    if (ann == null) {
      Log.v(TAG, "No Artificial Neural Network present! Simulation exiting ...");
      return;
    }
    robot = new Robot(0, 0, board);
    board.set(robot);
    for (int i = 0; i < 60; i++) {
      ann.updateInput(robot.getSensoryInput());
      java.util.List<Double> outputs = ann.getOutputs();
      int indexOfBest = outputs.indexOf(outputs.stream().max(Double::compare).get());
      robot.move(indexOfBest);
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
      }
    }
  }

  public void setBoard(Board<TileEntity> board) {
    canvas.setAdapter(board);
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
    new AiLifeGui(board, ann);
  }

  public static void demo() {
    Board<TileEntity> board = AiLife.createAiLifeBoard(1);
    AiLifeGui demo = new AiLifeGui(board, null);
    demo.robot = new Robot(0, 0, board);
    board.set(demo.robot);
    board.notifyDataChanged();
  }
}
