package subsym.ann;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by anon on 20.03.2015.
 */
public class ArtificialNeuralNetwork {

  private static Random random = new Random(1);
  private final int hiddenLayerCount;
  private final int hiddenNeuronCount;
  private final AnnNodes inputs;
  private final AnnNodes outputs;
  private final ActivationFunction activationFunction;

  public ArtificialNeuralNetwork(int hiddenLayerCount, int hiddenNeuronCount, AnnNodes inputs, AnnNodes outputs,
                                 ActivationFunction activationFunction) {
    this.hiddenLayerCount = hiddenLayerCount;
    this.hiddenNeuronCount = hiddenNeuronCount;
    this.inputs = inputs;
    this.outputs = outputs;
    this.activationFunction = activationFunction;
    createNetwork();
    setActivationFunction();
  }

  private void createNetwork() {
    AnnNodes currentLayer = inputs;
    for (int i = 0; i < hiddenLayerCount; i++) {
      AnnNodes nextLayer = AnnNodes.createOutput(hiddenNeuronCount);
      currentLayer.stream().forEach(v -> v.connect(nextLayer));
      currentLayer = nextLayer;
    }
    currentLayer.stream().forEach(v -> v.connect(outputs));
  }

  private void setActivationFunction() {
    inputs.stream().forEach(node -> node.setActivationFunction(activationFunction));
  }

  public void setRandomWeights() {
    inputs.setRandomWeights();
  }

  public void updateInput(double... inputs) {
    if (inputs.length != this.inputs.size()) {
      throw new IllegalStateException("Inputs not equal size!");
    }
    AtomicInteger i = new AtomicInteger(0);
    this.inputs.stream().forEach(n -> n.setValue(inputs[i.getAndIncrement()]));
  }

  public List<Double> getOutputs() {
    return outputs.stream().mapToDouble(n -> n.getValue()).boxed().collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return "ANN > Inputs: " + inputs + " > Outputs: " + outputs;
  }

  public static Random random() {
    return random;
  }
}
