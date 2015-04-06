package subsym.ann.nodes;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import subsym.ann.ActivationFunction;
import subsym.ann.ArtificialNeuralNetwork;

/**
 * Created by anon on 20.03.2015.
 */
public abstract class AnnNode {

  protected AnnNodes inputs;
  protected ActivationFunction activationFunction;
  private Random random = new Random();
  private final String id;
  protected Map<AnnNode, Double> inputWeights;
  protected double currentValue;
  private InputNode selfNode;
  private boolean hasState = false;

  protected AnnNode(Random random) {
    this.random = random;
    inputs = AnnNodes.createInput();
    inputWeights = new HashMap<>();
    id = ArtificialNeuralNetwork.nextId();
  }

  public static OutputNode createOutput() {
    return new OutputNode(ArtificialNeuralNetwork.random());
  }

  public static InputNode createInput(Double value) {
    return new InputNode(value, ArtificialNeuralNetwork.random());
  }

  public void connect(AnnNodes outputs) {
    outputs.stream().forEach(this::connect);
  }

  public void connect(AnnNode annNode) {
    annNode.inputWeights.put(this, 1.);
    annNode.addInput(this);
  }

  private void addInput(AnnNode annNode) {
    inputs.add(annNode);
  }

  public void setActivationFunction(ActivationFunction activationFunction) {
    if (this.activationFunction != null) {
      return;
    }
    this.activationFunction = activationFunction;
    inputs.stream().forEach(n -> n.setActivationFunction(activationFunction));
  }

  public void setRandomWeights() {
    inputs.stream().forEach(n -> inputWeights.put(n, random.nextDouble()));
    inputs.setRandomWeights();
  }

  public Set<AnnNode> getInputs() {
    return inputWeights.keySet();
  }

  public void setWeight(AnnNode outputNode, Double weight) {
    inputWeights.put(outputNode, weight);
  }

  public abstract double getValue();

  public static AnnNode createBias() {
    return new InputNode(1., ArtificialNeuralNetwork.random());
  }

  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return id + " ";
  }

  public void incrementTime() {
    if (selfNode != null) {
      selfNode.setValue(currentValue);
    }
  }

  public void addSelfNode(InputNode bias) {
    bias.connect(this);
    selfNode = bias;
  }

  public void setStateful() {
    hasState = true;
  }

  public boolean hasState() {
    return hasState;
  }
}
