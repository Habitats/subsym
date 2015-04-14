package subsym.ailife;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.Log;
import subsym.ailife.entity.Food;
import subsym.ailife.entity.Poison;
import subsym.ailife.entity.Robot;
import subsym.ann.AnnPreferences;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.WeightBound;
import subsym.ann.nodes.AnnNodes;
import subsym.genetics.Phenotype;
import subsym.models.Board;
import subsym.models.entity.TileEntity;

/**
 * Created by anon on 21.03.2015.
 */
public class AiLifePhenotype implements Phenotype {

  private static final String TAG = AiLifePhenotype.class.getSimpleName();
  private final AnnPreferences prefs;
  private final ArtificialNeuralNetwork ann;
  private final AiLifeGenotype aiLifeGenotype;
  private Double score;

  public AiLifePhenotype(AiLifeGenotype aiLifeGenotype, AnnPreferences prefs) {
    this.aiLifeGenotype = aiLifeGenotype;
    this.prefs = prefs;

    AnnNodes inputs = AnnNodes.createInput(new WeightBound(0, 1), 0., 0., 0., 0., 0., 0.);
    AnnNodes outputs = AnnNodes.createOutput(new WeightBound(0, 1), 3);
    ann = new ArtificialNeuralNetwork(prefs, inputs, outputs);
  }

  public void setValues(AiLifeGenotype aiLifeGenotype) {
    ann.setWeights(getNormalizedValues(aiLifeGenotype.toList()));
  }

  @Override
  public double fitness() {
    if (score == null) {
      score = boardFitness();
    }
    return score;
  }

  private double boardFitness() {
    setValues(aiLifeGenotype);

    AtomicDouble fitness = new AtomicDouble();
    int rounds = prefs.isSingle() ? 1 : 5;
    IntStream.range(0, rounds).forEach(run -> {
      int seed = prefs.isDynamic() ? aiLifeGenotype.getCurrentGeneration() + run : run;
      Board<TileEntity> board = AiLife.createAiLifeBoard(seed);
      Log.v(TAG, ann.getNumWeights());
      long numPoison = board.getItems().stream().filter(i -> i instanceof Poison).count();
      long numFood = board.getItems().stream().filter(i -> i instanceof Food).count();
      Robot robot = new Robot(0, 0, board);
      board.set(robot);
      for (int i = 0; i < 1; i++) {
        Log.v(TAG, board.getFormattedBoard());
        ann.updateInput(robot.getSensoryInput());
        List<Double> outputs = ann.getOutputs();
        int indexOfBest = outputs.indexOf(outputs.stream().max(Double::compare).get());
        Log.v(TAG, robot.getSensoryInput() + " " + outputs);
        Log.v(TAG, ann.getWeights().stream().map(String::valueOf).collect(Collectors.joining(" ")));
        robot.move(indexOfBest);
      }

      long deltaPoison = numPoison - board.getItems().stream().filter(i -> i instanceof Poison).count();
      long deltaFood = numFood - board.getItems().stream().filter(i -> i instanceof Food).count();
      Log.v(TAG, "" + deltaFood + deltaPoison);
      fitness.getAndAdd(deltaFood * 2 + deltaPoison * -2);
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
    return getNormalizedValues(aiLifeGenotype.toList()).stream().map(i -> String.format("%.3f", i))
        .collect(Collectors.joining(", ", " > Pheno > ", ""));
  }

  public int getNumWeights() {
    return ann.getNumWeights();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AiLifePhenotype) {
      AiLifePhenotype other = (AiLifePhenotype) obj;
      return other.score.equals(score) && other.ann.equals(ann);
    }
    return false;
  }
}
