package subsym.ann;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import subsym.ann.nodes.AnnNode;
import subsym.ann.nodes.AnnNodes;
import subsym.ann.nodes.InputNode;
import subsym.ann.nodes.OutputNode;

/**
 * Created by anon on 20.03.2015.
 */
public class ArtificialNeuralNetwork {

  private static Random random = new Random(1);
  private static int idCounter = 0;
  private final int hiddenLayerCount;
  private final int hiddenNeuronCount;
  private final AnnNodes inputs;
  private final AnnNodes outputs;
  private final ActivationFunction activationFunction;
  private final List<AnnNodes> layers;
  private AnnNodes biasNodes;

  public ArtificialNeuralNetwork(AnnPreferences prefs, AnnNodes inputs, AnnNodes outputs) {
    layers = new ArrayList<>();
    this.hiddenLayerCount = prefs.getHiddenLayerCount();
    this.hiddenNeuronCount = prefs.getHiddenNeuronCount();
//    this.inputs = prefs.getInputs();
//    this.outputs = prefs.getInputs();
    this.inputs = inputs;
    this.outputs = outputs;
    this.activationFunction = prefs.getActivationFunction();
    createNetwork();
    setActivationFunction();
    setWeights(Collections.nCopies(getNumWeights(), .1));
  }

  public void addBiasNode(AnnNodes nodes) {
    AnnNode bias = AnnNode.createBias();
    bias.connect(nodes);

    if (biasNodes == null) {
      biasNodes = new AnnNodes();
      layers.add(biasNodes);
    }
    biasNodes.add(bias);
  }

  public void addSelfNode(AnnNodes nodes) {
    nodes.stream().forEach(n -> {
      InputNode bias = InputNode.createInput(1.);
      n.addSelfNode(bias);
      if (biasNodes == null) {
        biasNodes = new AnnNodes();
        layers.add(biasNodes);
      }
      biasNodes.add(bias);
    });
  }

  public List<AnnNodes> getLayers() {
    return layers;
  }

  private void createNetwork() {
    AnnNodes currentLayer = inputs;
    layers.add(inputs);
    for (int i = 0; i < hiddenLayerCount; i++) {
      AnnNodes nextLayer = AnnNodes.createOutput(hiddenNeuronCount);
      layers.add(nextLayer);
      currentLayer.stream().forEach(v -> v.connect(nextLayer));
      currentLayer = nextLayer;
    }
    layers.add(outputs);
    currentLayer.stream().forEach(v -> v.connect(outputs));
  }

  private void setActivationFunction() {
    inputs.stream().forEach(node -> node.setActivationFunction(activationFunction));
  }

  public void setRandomWeights() {
    inputs.setRandomWeights();
  }

  public void updateInput(Double... inputs) {
    updateInput(Arrays.asList(inputs));
  }

  public void updateInput(List<Double> inputs) {
    if (inputs.size() != this.inputs.size()) {
      throw new IllegalStateException("Inputs not equal size!");
    }
    AtomicInteger i = new AtomicInteger(0);
    this.inputs.stream().forEach(n -> ((InputNode) n).setValue(inputs.get(i.getAndIncrement())));

    layers.stream().flatMap(AnnNodes::stream).forEach(AnnNode::incrementTime);
  }

  public List<Double> getOutputs() {
    return outputs.stream().mapToDouble(n -> n.getValue()).boxed().collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return "ANN > Inputs " + inputs + " > Outputs " + outputs;
  }

  public static Random random() {
    return random;
  }

  public void setWeights(List<Double> weights) {
    AtomicInteger i = new AtomicInteger();
    layers.stream()//
        .flatMap(layer -> layer.stream())//
        .forEach(node -> node.getInputs().stream()//
            .forEach(outputNode -> node.setWeight(outputNode, weights.get(i.getAndIncrement()))));
  }

  public int getNumWeights() {
    return layers.stream().flatMap(layer -> layer.stream()).mapToInt(node -> node.getInputs().size()).sum();
  }

  public int getNumNodes() {
    return layers.stream().mapToInt(layer -> layer.size()).sum();
  }

  public List<Double> getInputs() {
    return inputs.getValues();
  }

  public int getBestIndex(List<Double> sensoryInput) {
    updateInput(sensoryInput);
    List<Double> outputs = getOutputs();
    return outputs.indexOf(outputs.stream().max(Double::compare).get());
  }

  public static String nextId() {
    return "n" + (idCounter++);
  }

  public void setTimeConstants(List<Double> doubles) {
    AtomicInteger i = new AtomicInteger();
    getOutputStream().forEach(outputNode -> outputNode.setTimeConstant(doubles.get(i.getAndIncrement())));
  }

  public void setGains(List<Double> gains) {
    AtomicInteger i = new AtomicInteger();
    getOutputStream().forEach(outputNode -> outputNode.setGain(gains.get(i.getAndIncrement())));
  }

  private Stream<OutputNode> getOutputStream() {
    return layers.stream().flatMap(AnnNodes::stream).filter(n -> n instanceof OutputNode).map(n -> (OutputNode) n);
  }

  public void setStateful() {
    layers.stream().flatMap(AnnNodes::stream).forEach(AnnNode::setStateful);
  }
}
