package subsym.ailife;

import java.awt.*;
import java.util.stream.IntStream;

import javax.swing.*;

import subsym.Log;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.genetics.GeneticPreferences;
import subsym.genetics.GeneticProblem;
import subsym.genetics.gui.GeneticGui;
import subsym.gui.AICanvas;
import subsym.gui.AIGridCanvas;
import subsym.gui.AIGui;
import subsym.gui.AITextArea;
import subsym.gui.ColorUtils;
import subsym.models.Board;
import subsym.models.TileEntity;

/**
 * Created by anon on 20.03.2015.
 */
public class AiLife extends GeneticProblem {

  private static final String TAG = AiLife.class.getSimpleName();
  private final GeneticGui geneticGui;

  public AiLife(GeneticPreferences prefs, GeneticGui geneticGui) {
    super(prefs);
//    AnnNodes inputs = AnnNodes.createInput(0.3, 0.1, 0.7);
//    AnnNodes outputs = AnnNodes.createOutput(2);
//    ArtificialNeuralNetwork ann = new ArtificialNeuralNetwork(1, 4, inputs, outputs, new Sigmoid());
//
//    Log.v(TAG, ann);
////    ann.updateInput(0.8, 0.9, 0.2);
//    Log.v(TAG, ann.getNumNodes());
//    Log.v(TAG, ann.getNumWeights());
//    ann.setRandomWeights();
//    ann.setRandomWeights();
//
//    Log.v(TAG, "Setting random weights ...");
//    List<Double> weights = IntStream.range(0, ann.getNumWeights())//
//        .mapToDouble(i -> ArtificialNeuralNetwork.random().nextDouble()).boxed().collect(Collectors.toList());
//    ann.setWeights(weights);
//    Log.v(TAG, ann);
//
//    Log.v(TAG, "Setting weights to 1 ...");
//    weights = IntStream.range(0, ann.getNumWeights()).mapToDouble(i -> 1).boxed().collect(Collectors.toList());
//    ann.setWeights(weights);
//
//    Log.v(TAG, ann);
    this.geneticGui = geneticGui;
  }

  @Override
  protected double getCrossoverCut() {
    return ArtificialNeuralNetwork.random().nextDouble();
  }

  @Override
  public void initPopulation() {
    IntStream.range(0, getPopulationSize()).forEach(i -> {
      AiLifeGenotype genotype = new AiLifeGenotype();
      getPopulation().add(genotype);
    });
  }

  @Override
  public boolean solution() {
    return getPopulation().getBestGenotype().fitness() == 1;
  }

  @Override
  public GeneticProblem newInstance(GeneticPreferences prefs) {
    return new AiLife(prefs, geneticGui);
  }

  @Override
  public GeneticProblem newInstance() {
    return new AiLife(getPreferences(), geneticGui);
  }

  @Override
  public void increment(int increment) {

  }

  @Override
  public void onSolved() {
    Log.v(TAG, this);
    AIGridCanvas<TileEntity> canvas = new AIGridCanvas<>();
    Board<TileEntity> board = new Board<>(10, 10);
    IntStream.range(0, 10).forEach(x -> IntStream.range(0, 10).forEach(y -> board.set(new Empty(x, y, board))));
    canvas.setAdapter(board);
//
    new AIGui() {

      @Override
      protected int getDefaultCloseOperation() {
        return WindowConstants.EXIT_ON_CLOSE;
      }

      @Override
      protected Dimension getPreferredSize() {
        return new Dimension(500, 500);
      }

      @Override
      protected void init() {
        buildFrame(canvas, null, null);
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

  private class Empty extends TileEntity {

    private Empty(int x, int y, Board<TileEntity> board) {
      super(x, y, board);
    }

    @Override
    public Color getColor() {
      return ColorUtils.c(ArtificialNeuralNetwork.random().nextInt(ColorUtils.NUM_COLORS));
    }
  }
}
