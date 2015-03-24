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

  protected AnnNodes outputs;
  protected AnnNodes inputs;
  protected ActivationFunction activationFunction;
  private Random random = new Random();
  protected Map<AnnNode, Double> outputWeights;

  protected AnnNode(Random random) {
    this.random = random;
    inputs = AnnNodes.createInput();
    outputs = AnnNodes.createOutput(0);
    outputWeights = new HashMap<>();
  }

  public static AnnNode createOutput() {
    return new OutputNode(ArtificialNeuralNetwork.random());
  }

  public static AnnNode createInput(Double value) {
    return new InputNode(value, ArtificialNeuralNetwork.random());
  }

  public static AnnNode createHidden() {
    return new HiddenNode(ArtificialNeuralNetwork.random());
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

  public void setRandomWeights() {
    outputs.stream().forEach(n -> outputWeights.put(n, random.nextDouble()));
    outputs.setRandomWeights();
  }

  public Set<AnnNode> getOutputs() {
    return outputWeights.keySet();
  }

  public void setWeight(AnnNode outputNode, Double weight) {
    outputWeights.put(outputNode, weight);
  }

  public abstract double getValue();
}
