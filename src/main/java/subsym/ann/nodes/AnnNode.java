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

  private final String id;
  protected AnnNodes inputs;
  protected ActivationFunction activationFunction;
  protected Map<AnnNode, Double> inputWeights;
  protected double currentValue;
  private Random random = new Random();
  private InputNode selfNode;
  private boolean hasState = false;

  protected AnnNode(Random random) {
    this.random = random;
    inputs = AnnNodes.createInput();
    inputWeights = new HashMap<>();
    id = ArtificialNeuralNetwork.nextId();
  }

  //##################################################################
  //################# STATIC FACTORY #################################
  //##################################################################

  public static OutputNode createOutput() {
    return new OutputNode(ArtificialNeuralNetwork.random());
  }

  public static InputNode createInput(Double value) {
    return new InputNode(value, ArtificialNeuralNetwork.random());
  }

  public static AnnNode createBias() {
    return new InputNode(1., ArtificialNeuralNetwork.random());
  }

  //##################################################################
  //################# MANAGEMENT #####################################
  //##################################################################

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

  public void addSelfNode(InputNode bias) {
    bias.connect(this);
    selfNode = bias;
  }

  public void incrementTime() {
    if (selfNode != null) {
      selfNode.setValue(currentValue);
    }
  }

  //##################################################################
  //################# PROPERTIES #####################################
  //##################################################################

  public void setRandomWeights() {
    inputs.stream().forEach(n -> inputWeights.put(n, random.nextDouble()));
    inputs.setRandomWeights();
  }

  public void setWeight(AnnNode outputNode, Double weight) {
    inputWeights.put(outputNode, weight);
  }

  public void setActivationFunction(ActivationFunction activationFunction) {
    if (this.activationFunction != null) {
      return;
    }
    this.activationFunction = activationFunction;
    inputs.stream().forEach(n -> n.setActivationFunction(activationFunction));
  }

  public abstract double getValue();

  public Set<AnnNode> getInputs() {
    return inputWeights.keySet();
  }

  public String getId() {
    return id;
  }

  public void setStateful() {
    hasState = true;
  }

  public boolean hasState() {
    return hasState;
  }

  @Override
  public String toString() {
    return id + " ";
  }
}
