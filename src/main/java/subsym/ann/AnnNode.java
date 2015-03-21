package subsym.ann;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by anon on 20.03.2015.
 */
public class AnnNode {

  private AnnNodes outputs;
  private Double value;
  private AnnNodes inputs;
  private ActivationFunction activationFunction;
  private Random random = new Random();
  private Map<AnnNode, Double> outputWeights;

  private AnnNode(Double value, Random random) {
    this.value = value;
    this.random = random;
    inputs = AnnNodes.createEmpty();
    outputs = AnnNodes.createEmpty();
    outputWeights = new HashMap<>();
  }

  public static AnnNode create(Double value) {
    return new AnnNode(value, ArtificialNeuralNetwork.random());
  }

  public static AnnNode create() {
    return new AnnNode(-1., ArtificialNeuralNetwork.random());
  }

  public void connect(AnnNodes outputs) {
    outputs.stream().forEach(this::connect);
    outputs.stream().forEach(n -> outputWeights.put(n, 1.));
  }

  private void connect(AnnNode annNode) {
    outputs.add(annNode);
    annNode.addInput(this);
  }

  private void addInput(AnnNode annNode) {
    inputs.add(annNode);
  }

  public void setActivationFunction(ActivationFunction activationFunction) {
    this.activationFunction = activationFunction;
    outputs.stream().forEach(n -> n.setActivationFunction(activationFunction));
  }

  private double getInputSum() {
    if (inputs.size() > 0) {
      return inputs.stream().mapToDouble(n -> n.getValue() * n.outputWeights.get(this)).reduce((n1, n2) -> n1 + n2).getAsDouble();
    } else {
      return value;
    }
  }

  public double getValue() {
    if (inputs.size() > 0) {
      return activationFunction.evaluate(getInputSum());
    } else {
      return value;
    }
  }

  public void setRandomWeights() {
    outputs.stream().forEach(n -> outputWeights.put(n, random.nextDouble()));
    outputs.setRandomWeights();
  }

  public void setValue(double value) {
    this.value = value;
  }

  @Override
  public String toString() {
    String weights = "";
    if (outputs.size() > 0) {
      weights = outputs.stream().map(n -> String.format("%.3f", outputWeights.get(n))).collect(Collectors.joining(", ", "- W: ", ""));
    }
    return String.format("V: %.3f %s - S: %.3f", getValue(), weights, getInputSum());
  }
}
