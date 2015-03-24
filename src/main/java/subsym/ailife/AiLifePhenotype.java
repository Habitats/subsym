package subsym.ailife;

import java.util.List;
import java.util.stream.Collectors;

import subsym.ailife.entity.Robot;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.Sigmoid;
import subsym.ann.nodes.AnnNodes;
import subsym.genetics.Phenotype;
import subsym.models.Board;
import subsym.models.TileEntity;

/**
 * Created by anon on 21.03.2015.
 */
public class AiLifePhenotype implements Phenotype {

  private final AiLifeGenotype aiLifeGenotype;
  private final ArtificialNeuralNetwork ann;

  public AiLifePhenotype(AiLifeGenotype aiLifeGenotype) {
    this.aiLifeGenotype = aiLifeGenotype;

    AnnNodes inputs = AnnNodes.createInput(0., 0., 0., 0., 0., 0.);
    AnnNodes outputs = AnnNodes.createOutput(3);
    ann = new ArtificialNeuralNetwork(1, 3, inputs, outputs, new Sigmoid());
    this.aiLifeGenotype.setRandom(ann.getNumWeights() * aiLifeGenotype.getBitGroupSize());
    ann.setWeights(getNormalizedValues(aiLifeGenotype.toList()));
  }

  private double normalize(int v) {
    return (v % 1000) / 1000.;
  }

  private List<Double> getNormalizedValues(List<Integer> values) {
    return values.stream().mapToDouble(this::normalize).boxed().collect(Collectors.toList());
  }

  @Override
  public double fitness() {
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

  @Override
  public String toString() {
    return getNormalizedValues(aiLifeGenotype.toList()).stream().map(i -> String.format("%.3f", i))
        .collect(Collectors.joining(", ", " > Pheno > ", ""));
  }

  public ArtificialNeuralNetwork getArtificialNeuralNetwork() {
    return ann;
  }
}
