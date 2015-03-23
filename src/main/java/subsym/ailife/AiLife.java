package subsym.ailife;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.Log;
import subsym.ann.AnnNodes;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.Sigmoid;
import subsym.genetics.GeneticPreferences;
import subsym.genetics.GeneticProblem;

/**
 * Created by anon on 20.03.2015.
 */
public class AiLife extends GeneticProblem {

  private static final String TAG = AiLife.class.getSimpleName();

  public AiLife(GeneticPreferences prefs) {
    super(prefs);
    AnnNodes inputs = AnnNodes.createInput(0.3, 0.1, 0.7);
    AnnNodes outputs = AnnNodes.createOutput(2);
    ArtificialNeuralNetwork ann = new ArtificialNeuralNetwork(1, 4, inputs, outputs, new Sigmoid());

    Log.v(TAG, ann);
//    ann.updateInput(0.8, 0.9, 0.2);
    Log.v(TAG, ann.getNumNodes());
    Log.v(TAG, ann.getNumWeights());
    ann.setRandomWeights();
    ann.setRandomWeights();

    Log.v(TAG, "Setting random weights ...");
    List<Double> weights = IntStream.range(0, ann.getNumWeights())//
        .mapToDouble(i -> ArtificialNeuralNetwork.random().nextDouble()).boxed().collect(Collectors.toList());
    ann.setWeights(weights);
    Log.v(TAG, ann);

    Log.v(TAG, "Setting weights to 1 ...");
    weights = IntStream.range(0, ann.getNumWeights()).mapToDouble(i -> 1).boxed().collect(Collectors.toList());
    ann.setWeights(weights);

    Log.v(TAG, ann);
  }

  @Override
  protected double getCrossoverCut() {
    return 0;
  }

  @Override
  public void initPopulation() {

  }

  @Override
  public boolean solution() {
    return false;
  }

  @Override
  public GeneticProblem newInstance(GeneticPreferences prefs) {
    return null;
  }

  @Override
  public GeneticProblem newInstance() {
    return null;
  }

  @Override
  public void increment(int increment) {

  }
}
