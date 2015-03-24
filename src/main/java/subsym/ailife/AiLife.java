package subsym.ailife;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import javax.swing.*;

import subsym.Log;
import subsym.ailife.entity.Robot;
import subsym.ailife.entity.Empty;
import subsym.ailife.entity.Food;
import subsym.ailife.entity.Poison;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.genetics.GeneticPreferences;
import subsym.genetics.GeneticProblem;
import subsym.gui.AICanvas;
import subsym.gui.AIGridCanvas;
import subsym.gui.AIGui;
import subsym.gui.AITextArea;
import subsym.models.Board;
import subsym.models.entity.TileEntity;

/**
 * Created by anon on 20.03.2015.
 */
public class AiLife extends GeneticProblem {

  private static final String TAG = AiLife.class.getSimpleName();
  private final AIGridCanvas<TileEntity> canvas;
  private Robot robot;

  public AiLife(){
    super(null);
    Board<TileEntity> board = createAiLifeBoard(0101);
    canvas = new AIGridCanvas<>();
    canvas.setAdapter(board);
    canvas.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
          case KeyEvent.VK_A:
            robot.move(0);
            break;
          case KeyEvent.VK_W:
            robot.move(1);
            break;
          case KeyEvent.VK_D:
            robot.move(2);
            break;
        }
      }
    });
    robot = new Robot(0, 0, board);
    board.set(robot);
    displayGui(canvas);
    canvas.requestFocus();
  }

  public AiLife(GeneticPreferences prefs) {
    super(prefs);
    canvas = new AIGridCanvas<>();
  }

  @Override
  protected double getCrossoverCut() {
    return ArtificialNeuralNetwork.random().nextDouble();
  }

  @Override
  public void initPopulation() {
    IntStream.range(0, getPopulationSize()).forEach(i -> {
      AiLifeGenotype genotype = new AiLifeGenotype(getPreferences().getAnnPreferences());
      getPopulation().add(genotype);
    });
  }

  @Override
  public boolean solution() {
//    return getPopulation().getBestGenotype().fitness() == 1;
    return getPopulation().getCurrentGeneration() == getPreferences().getMaxGenerations();
  }

  @Override
  public GeneticProblem newInstance(GeneticPreferences prefs) {
    return new AiLife(prefs);
  }

  @Override
  public GeneticProblem newInstance() {
    return new AiLife(getPreferences());
  }

  @Override
  public void increment(int increment) {

  }

  @Override
  public void onSolved() {
    AiLifeGenotype best = (AiLifeGenotype) getPopulation().getBestGenotype();
    Log.v(TAG, best.fitness());
    AiLifePhenotype pheno = (AiLifePhenotype) best.getPhenotype();
    ArtificialNeuralNetwork ann = pheno.getArtificialNeuralNetwork();
    Log.v(TAG, pheno.fitness());
    Board<TileEntity> board = createAiLifeBoard(0101);
    canvas.setAdapter(board);
    displayGui(canvas);
    robot = new Robot(0, 0, board);
    board.set(robot);
    for (int i = 0; i < 60; i++) {
      ann.updateInput(robot.getSensoryInput());
      List<Double> outputs = ann.getOutputs();
      int indexOfBest = outputs.indexOf(outputs.stream().max(Double::compare).get());
      robot.move(indexOfBest);
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
      }
    }

    Log.v(TAG, this);
  }

  public static void displayGui(AIGridCanvas<TileEntity> canvas) {
    new AIGui() {
      @Override
      protected int getDefaultCloseOperation() {
        return WindowConstants.DISPOSE_ON_CLOSE;
      }

      @Override
      protected Dimension getPreferredSize() {
        return new Dimension(500, 500);
      }

      @Override
      protected void init() {
        buildFrame(canvas, null, null);
        canvas.requestFocus();
      }

      @Override
      public JPanel getMainPanel() {
        return null;
      }

      @Override
      public AICanvas getDrawingCanvas() {
        return null;
      }

      @Override
      public AITextArea getInputField() {
        return null;
      }
    }.init();
  }

  public static Board<TileEntity> createAiLifeBoard(int seed) {
    Board<TileEntity> board = new Board<>(10, 10);
    Random random = new Random(seed);
    IntStream.range(0, 10).forEach(x -> IntStream.range(0, 10).forEach(y -> board.set(getRandomTile(board, x, y, random.nextDouble()))));
    return board;
  }

  private static TileEntity getRandomTile(Board<TileEntity> board, int x, int y, double random) {
    if (random < 1 / 3.) {
      return new Empty(x, y, board);
    } else if (random < 2 / 3.) {
      return new Food(x, y, board);
    } else {
      return new Poison(x, y, board);
    }
  }
}

