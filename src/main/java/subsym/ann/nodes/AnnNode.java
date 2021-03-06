package subsym.ann.nodes;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import subsym.Main;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.WeightBound;
import subsym.ann.activation.ActivationFunction;

/**
 * Created by anon on 20.03.2015.
 */
public abstract class AnnNode implements Comparable<AnnNode> {

  private String id;
  protected AnnNodes inputs;
  protected ActivationFunction activationFunction;
  protected Map<AnnNode, Double> inputWeights;
  protected double currentValue;
  private Random random = new Random();
  private InputNode selfNode;
  private InputNode crossNode;
  private boolean hasState = false;
  private WeightBound bound;
  private String globalId;

  protected AnnNode(Random random, WeightBound bound) {
    this.random = random;
    this.bound = bound;
    inputs = AnnNodes.createInput(new WeightBound(-5., 5.));
    inputWeights = new HashMap<>();
    globalId = String.valueOf(ArtificialNeuralNetwork.nextGlobalId());
  }

  //##################################################################
  //################# STATIC FACTORY #################################
  //##################################################################

  public static OutputNode createOutput(WeightBound bound) {
    return new OutputNode(Main.random(), bound);
  }

  public static InputNode createInput(WeightBound bound, Double value) {
    return new InputNode(value, Main.random(), bound);
  }

  public static AnnNode createBias(WeightBound bound) {
    return new InputNode(1., Main.random(), bound);
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

  public InputNode crossConnect(AnnNode annNode) {
    if (crossNode == null) {
      crossNode = createInput(bound, 1.);
    }
    crossNode.inputWeights = inputWeights;
    annNode.inputWeights.put(crossNode, 1.);
    annNode.addInput(crossNode);
    return crossNode;
  }

  private void addInput(AnnNode annNode) {
    inputs.add(annNode);
  }

  public void addSelfNode(InputNode bias) {
    bias.connect(this);
    selfNode = bias;
  }

  public void incrementTime() {
    if (crossNode != null) {
      crossNode.setValue(currentValue);
    }
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
    inputWeights.put(outputNode, outputNode.getBound().fromNormal(weight));
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

  protected String getFormattedWeights() {
    String weights = "";
    if (inputs.size() > 0) {
      weights = inputs.stream()//
          .map(n -> String.format("(%3s | %7.3f)", n.getId(), inputWeights.get(n))).collect(Collectors.joining(", ", " > W = [", "]"));
    }
    return weights;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AnnNode) {
      AnnNode other = (AnnNode) obj;
      return getFormattedWeights().equals(other.getFormattedWeights());
    }

    return false;
  }

  public WeightBound getBound() {
    return bound;
  }

  @Override
  public String toString() {
    return String.format("%3s %3s", id, globalId);
  }

  public void setId(int id) {
    this.id = "n" + String.valueOf(id);
  }

  @Override
  public int compareTo(AnnNode o) {
    return o.getId().compareTo(getId());
  }

  public String getGlobalId() {
    return String.valueOf(globalId);
  }
}
