package subsym.ailife;

import java.util.List;
import java.util.stream.Collectors;

import subsym.ann.AnnNodes;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.Sigmoid;
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
    ann = new ArtificialNeuralNetwork(2, 4, inputs, outputs, new Sigmoid());

    this.aiLifeGenotype.setRandom(ann.getNumWeights() * aiLifeGenotype.getBitGroupSize());

    List<Double> weights = aiLifeGenotype.toList().stream()//
        .mapToDouble(this::normalize).boxed().collect(Collectors.toList());
    ann.setWeights(weights);

  }

  private double normalize(int v) {
    return (v % 1000) / 1000.;
  }

  private List<Double> getNormalizedValues(List<Integer> values) {
    return values.stream().mapToDouble(this::normalize).boxed().collect(Collectors.toList());
  }

  private void updateArtificialNeuralNetwork(AiLifeGenotype aiLifeGenotype) {
    ann.setWeights(getNormalizedValues(aiLifeGenotype.toList()));
  }

  @Override
  public double fitness() {
    updateArtificialNeuralNetwork(aiLifeGenotype);

    Board<TileEntity> board = AiLife.createAiLifeBoard();
    AiLifeRobot robot = new AiLifeRobot(0, 0, board);
    board.set(robot);
    for (int i = 0; i < 60; i++) {
      ann.updateInput(robot.getSensoryInput());
      List<Double> outputs = ann.getOutputs();
      int indexOfBest = outputs.indexOf(outputs.stream().max(Double::compare).get());
      robot.move(indexOfBest);
    }

    return robot.fitness();
  }

  public ArtificialNeuralNetwork getArtificialNeuralNetwork() {
    return ann;
  }
}
