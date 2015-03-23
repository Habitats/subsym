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
  private final ArtificialNeuralNetwork ann;

  public AiLifePhenotype(AiLifeGenotype aiLifeGenotype) {
    this.aiLifeGenotype = aiLifeGenotype;
    List<Integer> values = aiLifeGenotype.toList();
    List<Double> normalizedValues = getNormalizedValues(values);
    AnnNodes inputs = AnnNodes.createInput(normalizedValues);
    AnnNodes outputs = AnnNodes.createOutput(3);
    ann = new ArtificialNeuralNetwork(1, 4, inputs, outputs, new Sigmoid());
  }

  private List<Double> getNormalizedValues(List<Integer> values) {
    return values.stream().mapToDouble(v -> v / values.size()).boxed().collect(Collectors.toList());
  }

  private void updateArtificialNeuralNetwork(AiLifeGenotype aiLifeGenotype) {
    ann.updateInput(getNormalizedValues(aiLifeGenotype.toList()));
  }

  @Override
  public double fitness() {
    updateArtificialNeuralNetwork(aiLifeGenotype);
    List<Double> outputs = ann.getOutputs();
    List<Double> weights = aiLifeGenotype.toList().stream().mapToDouble(v -> v / 1000.).boxed().collect(Collectors.toList());
    ann.setWeights(weights);
    return 0;
  }
}
