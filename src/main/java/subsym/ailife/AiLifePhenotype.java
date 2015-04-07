package subsym.ailife;

import com.google.common.util.concurrent.AtomicDouble;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.ailife.entity.Food;
import subsym.ailife.entity.Poison;
import subsym.ailife.entity.Robot;
import subsym.ann.AnnPreferences;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.activation.Sigmoid;
import subsym.ann.nodes.AnnNodes;
import subsym.genetics.Phenotype;
import subsym.models.Board;
import subsym.models.entity.TileEntity;

/**
 * Created by anon on 21.03.2015.
 */
public class AiLifePhenotype implements Phenotype {

  private final AnnPreferences prefs;
  private final ArtificialNeuralNetwork ann;
  private final AiLifeGenotype aiLifeGenotype;

  public AiLifePhenotype(AiLifeGenotype aiLifeGenotype, AnnPreferences prefs) {
    this.aiLifeGenotype = aiLifeGenotype;
    this.prefs = prefs;

    AnnNodes inputs = AnnNodes.createInput(0., 0., 0., 0., 0., 0.);
    AnnNodes outputs = AnnNodes.createOutput(3);
    ann = new ArtificialNeuralNetwork(prefs, inputs, outputs, new Sigmoid());
    this.aiLifeGenotype.setRandom(ann.getNumWeights() * aiLifeGenotype.getBitGroupSize());
    ann.setWeights(getNormalizedValues(aiLifeGenotype.toList()));
  }

  @Override
  public double fitness() {
//    return robotFitness();
    return boardFitness();
  }

  private double boardFitness() {
    ann.setWeights(getNormalizedValues(aiLifeGenotype.toList()));

    AtomicDouble fitness = new AtomicDouble();
    IntStream.range(0, prefs.isSingle() ? 1 : 5).forEach(run -> {
      int seed = prefs.isDynamic() ? aiLifeGenotype.getCurrentGeneration() + run : run;
      Board<TileEntity> board = AiLife.createAiLifeBoard(seed);
      long numPoison = board.getItems().stream().filter(i -> i instanceof Poison).count();
      long numFood = board.getItems().stream().filter(i -> i instanceof Food).count();
      Robot robot = new Robot(0, 0, board);
      board.set(robot);
      for (int i = 0; i < 60; i++) {
        ann.updateInput(robot.getSensoryInput());
        List<Double> outputs = ann.getOutputs();
        int indexOfBest = outputs.indexOf(outputs.stream().max(Double::compare).get());
        robot.move(indexOfBest);
      }

      long deltaPoison = numPoison - board.getItems().stream().filter(i -> i instanceof Poison).count();
      long deltaFood = numFood - board.getItems().stream().filter(i -> i instanceof Food).count();
      fitness.getAndAdd(deltaFood * 2 + deltaPoison * -2);
    });
    return fitness.get();
  }

  private double robotFitness() {
    ann.setWeights(getNormalizedValues(aiLifeGenotype.toList()));

    Board<TileEntity> board = AiLife.createAiLifeBoard(0101);
    Robot robot = new Robot(0, 0, board);
    board.set(robot);
    for (int i = 0; i < 60; i++) {
      ann.updateInput(robot.getSensoryInput());
      List<Double> outputs = ann.getOutputs();
      int indexOfBest = outputs.indexOf(outputs.stream().max(Double::compare).get());
      robot.move(indexOfBest);
    }

    return robot.fitness();
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
}
