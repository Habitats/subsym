package subsym.ann;

/**
 * Created by anon on 20.03.2015.
 */
public class ArtificialNeuralNetwork {

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
    setStepFunction();
  }

  private void createNetwork() {
    inputs.stream().forEach(v -> v.connect(outputs));
  }

  private void setStepFunction() {
    inputs.stream().forEach(node -> node.setActivationFunction(activationFunction));
  }

  public void setRandomWeights() {
    inputs.setRandomWeights();
  }
}
