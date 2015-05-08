package subsym.flatland;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.flatland.entity.Robot;
import subsym.ann.AnnPreferences;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.WeightBound;
import subsym.ann.nodes.AnnNodes;
import subsym.genetics.Phenotype;
import subsym.gui.Direction;
import subsym.models.Board;
import subsym.models.entity.TileEntity;

/**
 * Created by anon on 21.03.2015.
 */
public class FlatlandPhenotype implements Phenotype {

  private static final String TAG = FlatlandPhenotype.class.getSimpleName();
  private final AnnPreferences prefs;
  private final ArtificialNeuralNetwork ann;
  private final FlatlandGenotype flatlandGenotype;
  private Double score;
  public static final List<Long> goodSeeds = Arrays.asList(new Long[]{-1517918040L, 1968097058L, 875635521L, 1489956094L, -1643623517L});

  public FlatlandPhenotype(FlatlandGenotype flatlandGenotype, AnnPreferences prefs) {
    this.flatlandGenotype = flatlandGenotype;
    this.prefs = prefs;

    AnnNodes inputs = AnnNodes.createInput(new WeightBound(0, 1), 0., 0., 0., 0., 0., 0.);
    AnnNodes outputs = AnnNodes.createOutput(new WeightBound(0, 1), 3);
    ann = new ArtificialNeuralNetwork(prefs, inputs, outputs);
    ann.setIds();
  }

  public void setValues(FlatlandGenotype flatlandGenotype) {
    ann.setWeights(getNormalizedValues(flatlandGenotype.toList()));
  }

  @Override
  public double fitness() {
    if (score == null) {
      score = boardFitness();
    }
    return score;
  }

  @Override
  public void resetFitness() {
    score = null;
  }

  private double boardFitness() {
    setValues(flatlandGenotype);

    AtomicDouble fitness = new AtomicDouble();
    int rounds = prefs.isSingle() ? 1 : 5;
    IntStream.range(0, rounds).forEach(run -> {
      long seed = prefs.isDynamic() ? flatlandGenotype.getCurrentGeneration() + run : goodSeeds.get(run);
      Board<TileEntity> board = FlatlandAnnSimulator.createAiLifeBoard(seed);
//      Log.v(TAG, ann.getNumWeights());

      Robot robot = new Robot(0, 0, board, true);
      board.set(robot);
      for (int i = 0; i < 60; i++) {
//        Log.v(TAG, board.getFormattedBoard());
        ann.updateInput(robot.getSensoryInput());
        List<Double> outputs = ann.getOutputs();
        int indexOfBest = outputs.indexOf(outputs.stream().max(Double::compare).get());
//        Log.v(TAG, robot.getSensoryInput() + " " + outputs);
//        Log.v(TAG, ann.getWeights().stream().map(String::valueOf).collect(Collectors.joining(" ")));
        if (indexOfBest == 0) {
          robot.move(Direction.LEFT);
        } else if (indexOfBest == 1) {
          robot.move(Direction.UP);
        } else if (indexOfBest == 2) {
          robot.move(Direction.RIGHT);
        }
      }

      fitness.getAndAdd(robot.getScore());
    });
    return fitness.get() / (double) rounds;
  }

  private double normalize(int v) {
    return (v % 1000) / 1000.;
  }

  private List<Double> getNormalizedValues(List<Integer> values) {
    return values.stream().mapToDouble(this::normalize).boxed().collect(Collectors.toList());
  }

  public ArtificialNeuralNetwork getArtificialNeuralNetwork() {
    return ann;
  }

  @Override
  public String toString() {
    return getNormalizedValues(flatlandGenotype.toList()).stream().map(i -> String.format("%.3f", i))
        .collect(Collectors.joining(", ", " > Pheno > ", ""));
  }

  public int getNumWeights() {
    return ann.getNumWeights();
  }

}
