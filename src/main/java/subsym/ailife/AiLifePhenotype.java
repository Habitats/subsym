package subsym.ailife;

import java.util.List;
import java.util.stream.Collectors;

import subsym.ann.AnnNodes;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.Sigmoid;
import subsym.genetics.Phenotype;

/**
 * Created by anon on 21.03.2015.
 */
public class AiLifePhenotype implements Phenotype {

  private final AiLifeGenotype aiLifeGenotype;
  private AiLifeRobot robot;
  private final ArtificialNeuralNetwork ann;

  public AiLifePhenotype(AiLifeGenotype aiLifeGenotype, AiLifeRobot robot) {
    this.aiLifeGenotype = aiLifeGenotype;
    this.robot = robot;

    AnnNodes inputs = AnnNodes.createInput(robot.getSensoryInput());
    AnnNodes outputs = AnnNodes.createOutput(3);
    ann = new ArtificialNeuralNetwork(1, 4, inputs, outputs, new Sigmoid());

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

    double fitness = 0;
    for (int i = 0; i < 10; i++) {
      List<Double> sensoryInput = robot.getRandomSensoryInput();
      ann.updateInput(sensoryInput);
      List<Double> outputs = ann.getOutputs();
      int indexOfBest = outputs.indexOf(outputs.stream().max(Double::compare).get());

      int poisonScore = Math.abs(robot.getPoisonSensorInput().get(indexOfBest) - 1);
      int foodScore = robot.getFoodSensorInput().get(indexOfBest);
      fitness += foodScore + poisonScore;

      ann.updateInput(robot.getRandomSensoryInput());
//      robot.move(indexOfBest);
    }

    double v = fitness / 20.;
    return v;
  }

  public ArtificialNeuralNetwork getArtificialNeuralNetwork() {
    return ann;
  }
}
