package subsym.ann;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import subsym.ann.nodes.AnnNodes;
import subsym.ann.nodes.InputNode;

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
  private final List<AnnNodes> layers;

  public ArtificialNeuralNetwork(AnnPreferences prefs) {
    layers = new ArrayList<>();
    this.hiddenLayerCount = prefs.getHiddenLayerCount();
    this.hiddenNeuronCount = prefs.getHiddenNeuronCount();
//    this.inputs = prefs.getInputs();
//    this.outputs = prefs.getOutputs();
    this.inputs = AnnNodes.createInput(0., 0., 0., 0., 0., 0.);
    this.outputs = AnnNodes.createOutput(3);
    this.activationFunction = prefs.getActivationFunction();
    createNetwork();
    setActivationFunction();
    setWeights(Collections.nCopies(getNumWeights(), .1));
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

  public void setWeights(List<Double> weights) {
    AtomicInteger i = new AtomicInteger();
    layers.stream()//
        .flatMap(layer -> layer.stream())//
        .forEach(node -> node.getOutputs().stream()//
            .forEach(outputNode -> node.setWeight(outputNode, weights.get(i.getAndIncrement()))));
  }

  public int getNumWeights() {
    int sum = layers.stream().flatMap(layer -> layer.stream()).mapToInt(node -> node.getOutputs().size()).sum();
    return sum;
  }

  public int getNumNodes() {
    return layers.stream().mapToInt(layer -> layer.size()).sum();
  }

  public List<Double> getInputs() {
    return inputs.getValues();
  }
}
