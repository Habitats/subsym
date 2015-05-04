package subsym.ann;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import subsym.Log;
import subsym.Main;
import subsym.ann.activation.ActivationFunction;
import subsym.ann.activation.Sigmoid;
import subsym.ann.nodes.AnnNode;
import subsym.ann.nodes.AnnNodes;
import subsym.ann.nodes.InputNode;
import subsym.ann.nodes.OutputNode;

/**
 * Created by anon on 20.03.2015.
 */
public class ArtificialNeuralNetwork {

  private static final String TAG = ArtificialNeuralNetwork.class.getSimpleName();
  private final int hiddenLayerCount;
  private final int hiddenNeuronCount;
  private final AnnNodes inputs;
  private final AnnNodes outputs;
  private final ActivationFunction activationFunction;
  private final List<AnnNodes> layers;
  private AnnNodes biasNodes;
  private List<Double> weights;

  private final static AtomicInteger globalIdCounter = new AtomicInteger();
  private final AtomicInteger idCounter;

  public ArtificialNeuralNetwork(AnnPreferences prefs, AnnNodes inputs, AnnNodes outputs) {
    layers = new ArrayList<>();
    idCounter = new AtomicInteger();
    Main.random = new Random(prefs.getRandomSeed());
    this.hiddenLayerCount = prefs.getHiddenLayerCount();
    this.hiddenNeuronCount = prefs.getHiddenNeuronCount();
    this.inputs = inputs;
    this.outputs = outputs;
    this.activationFunction = prefs.getActivationFunction();
    createNetwork();
    setActivationFunction();
  }

  public static ArtificialNeuralNetwork buildWrappingCtrnn(AnnPreferences prefs) {
    AnnNodes inputs = AnnNodes.createInput(new WeightBound(-5., 5.), 0., 0., 0., 0., 0.);
    AnnNodes outputs = AnnNodes.createOutput(new WeightBound(-5, 5), 2);
    ArtificialNeuralNetwork ann = createBeerAnn(prefs, inputs, outputs, true);
    return ann;
  }

  public static ArtificialNeuralNetwork buildNoWrapCtrnn(AnnPreferences prefs) {
//    AnnNodes inputs = AnnNodes.createInput(new WeightBound(-5., 5.), 0., 0., 0., 0., 0.);
//    AnnNode noWrap1 = AnnNode.createInput(new WeightBound(-10., 10.), 0.);
//    AnnNode noWrap2 = AnnNode.createInput(new WeightBound(-10., 10.), 0.);
//    inputs.add(noWrap1);
//    inputs.add(noWrap2);
    AnnNodes inputs = AnnNodes.createInput(new WeightBound(-5., 5.), 0., 0., 0., 0., 0., 0., 0.);
    AnnNodes outputs = AnnNodes.createOutput(new WeightBound(-5, 5), 2);
    ArtificialNeuralNetwork ann = createBeerAnn(prefs, inputs, outputs, false);
    return ann;
  }

  public static ArtificialNeuralNetwork buildPullingCtrnn(AnnPreferences prefs) {
    AnnNodes inputs = AnnNodes.createInput(new WeightBound(-5., 5.), 0., 0., 0., 0., 0.);
    AnnNodes outputs = AnnNodes.createOutput(new WeightBound(-5, 5), 3);
    ArtificialNeuralNetwork ann = createBeerAnn(prefs, inputs, outputs, false);
//    ArtificialNeuralNetwork ann = new ArtificialNeuralNetwork(prefs, inputs, outputs);
//    ann.setStateful();
//
//    List<AnnNodes> layers = ann.getLayers();
//    AnnNodes nodes = new AnnNodes(IntStream.range(1, layers.size())//
//                                      .mapToObj(layers::get).flatMap(AnnNodes::stream) //
//                                      .sorted((o1, o2) -> o1.getGlobalId().compareTo(o2.getGlobalId()))//
//                                      .collect(Collectors.toList()));
//    ann.addBiasNode(new WeightBound(-10, 0), nodes);
//    ann.getLayers().subList(1, layers.size() - 1).stream().forEach(layer -> {
//      layer.stream().forEach(n -> {
//        layer.stream().filter(other -> !n.equals(other)).forEach(n::crossConnect);
//      });
//    });
//    ann.addSelfNode(new WeightBound(-5, 5), nodes);
//    OutputNode pull = OutputNode.createOutput(new WeightBound(-5, 5));
//    outputs.add(pull);
//    inputs.stream().forEach(n -> n.connect(pull));
//    ann.getLayers().stream().flatMap(AnnNodes::stream).forEach(n -> n.setActivationFunction(new Sigmoid()));
//
//    ann.setIds();
    return ann;
  }

  private static ArtificialNeuralNetwork createBeerAnn(AnnPreferences prefs, AnnNodes inputs, AnnNodes outputs, boolean includeCrossnodes) {
    ArtificialNeuralNetwork ann = new ArtificialNeuralNetwork(prefs, inputs, outputs);
    ann.setStateful();

    List<AnnNodes> layers = ann.getLayers();
    AnnNodes nodes = new AnnNodes(IntStream.range(1, layers.size())//
                                      .mapToObj(layers::get).flatMap(AnnNodes::stream) //
                                      .sorted((o1, o2) -> o1.getGlobalId().compareTo(o2.getGlobalId()))//
                                      .collect(Collectors.toList()));
    ann.addBiasNode(new WeightBound(-10, 0), nodes);
    if (includeCrossnodes) {
      ann.addCrossNodes(ann, layers);
    }
    ann.addSelfNode(new WeightBound(-5, 5), nodes);
    ann.getLayers().stream().flatMap(AnnNodes::stream).forEach(n -> n.setActivationFunction(new Sigmoid()));

    ann.setIds();
    return ann;
  }

  private void addCrossNodes(ArtificialNeuralNetwork ann, List<AnnNodes> layers) {
    ann.getLayers().subList(1, layers.size() - 1).stream().forEach(layer -> {
      layer.stream().forEach(n -> {
        layer.stream().filter(other -> !(Long.compare(n.hashCode(), other.hashCode()) == 0)).forEach((annNode) -> {
          InputNode inputNode = n.crossConnect(annNode);
          ann.biasNodes.add(inputNode);
        });
      });
    });
  }

  public void addBiasNode(WeightBound bound, AnnNodes nodes) {
    AnnNode bias = AnnNode.createBias(bound);
    bias.connect(nodes);

    if (biasNodes == null) {
      biasNodes = new AnnNodes();
      layers.add(biasNodes);
    }
    biasNodes.add(bias);
  }

  public void addSelfNode(WeightBound bound, AnnNodes nodes) {
    nodes.stream().forEach(n -> {
      InputNode bias = InputNode.createInput(bound, 1.);
      n.addSelfNode(bias);
      if (biasNodes == null) {
        biasNodes = new AnnNodes();
        layers.add(biasNodes);
      }
      biasNodes.add(bias);
    });
  }

  public void resetInternalState() {
    getOutputNodeStream().forEach(n -> n.resetInternalState());
  }

  public List<AnnNodes> getLayers() {
    return layers;
  }

  private void createNetwork() {
    AnnNodes currentLayer = inputs;
    layers.add(inputs);
    for (int i = 0; i < hiddenLayerCount; i++) {
      AnnNodes nextLayer = AnnNodes.createOutput(getInternalWeightBound(), hiddenNeuronCount);
      layers.add(nextLayer);
      currentLayer.stream().forEach(v -> v.connect(nextLayer));
      currentLayer = nextLayer;
    }
    layers.add(outputs);
    currentLayer.stream().forEach(v -> v.connect(outputs));
  }

  private WeightBound getInternalWeightBound() {
    return getLayers().get(0).stream().findFirst().get().getBound();
  }

  public void updateInput(Double... inputs) {
    updateInput(Arrays.asList(inputs));
  }

  public void updateInput(List<Double> inputs) {
    if (inputs.size() != this.inputs.size()) {
      throw new IllegalStateException("Inputs not equal size!");
    }
    AtomicInteger i = new AtomicInteger(0);
    this.inputs.stream().sorted().forEach(n -> ((InputNode) n).setValue(inputs.get(i.getAndIncrement())));

    layers.stream().flatMap(AnnNodes::stream).forEach(AnnNode::incrementTime);
  }

  //##################################################################
  //################# PROPERTIES #####################################
  //##################################################################

  public List<Double> getOutputs() {
    return outputs.stream().sorted().mapToDouble(n -> n.getValue()).boxed().collect(Collectors.toList());
  }

  public void setWeights(List<Double> weights) {
    this.weights = weights;
    AtomicInteger i = new AtomicInteger();
    layers.stream()//
        .flatMap(layer -> layer.stream()).sorted()//
        .forEach(node -> node.getInputs().stream().sorted()//
            .forEach(outputNode -> {
              Double weight = weights.get(i.getAndIncrement());
              node.setWeight(outputNode, weight);
//              Log.v(TAG, node.getId() + " -> " + outputNode.getId() + " = " + weight);
            }));
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

  public void setTimeConstants(List<Double> doubles) {
    AtomicInteger i = new AtomicInteger();
    getOutputNodeStream().sorted().forEach(outputNode -> outputNode.setTimeConstant(doubles.get(i.getAndIncrement())));
  }

  public void setGains(List<Double> gains) {
    AtomicInteger i = new AtomicInteger();
    getOutputNodeStream().sorted().forEach(outputNode -> outputNode.setGain(gains.get(i.getAndIncrement())));
  }


  public void setActivationFunction() {
    outputs.stream().forEach(node -> node.setActivationFunction(activationFunction));
  }

  public void setStateful() {
    layers.stream().flatMap(AnnNodes::stream).forEach(AnnNode::setStateful);
  }

  public Stream<OutputNode> getOutputNodeStream() {
    return layers.stream().flatMap(AnnNodes::stream).filter(n -> n instanceof OutputNode).map(n -> (OutputNode) n);
  }

  public List<Double> getWeights() {
    return weights;
  }

  @Override
  public String toString() {
    return "ANN > Inputs " + inputs + " > Outputs " + outputs;
  }

  private List<AnnNode> getSortedNodes() {
    return layers.stream().flatMap(AnnNodes::stream).sorted().collect(Collectors.toList());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ArtificialNeuralNetwork) {
      ArtificialNeuralNetwork other = (ArtificialNeuralNetwork) obj;
      List<AnnNode> otherNodes = other.getSortedNodes();
      List<AnnNode> nodes = getSortedNodes();
      return IntStream.range(0, getNumNodes()).allMatch(i -> otherNodes.get(i).equals(nodes.get(i)));
    }
    return false;
  }

  public void statePrint() {
    Log.v(TAG, layers.stream().map(String::valueOf).collect(Collectors.joining("\n", "\n", "")));
  }

  public void setIds() {
    List<AnnNodes> sorted = layers.stream().sorted().collect(Collectors.toList());
    for (AnnNodes layer : sorted) {
      for (AnnNode node : layer) {
        node.setId(idCounter.getAndIncrement());
      }
    }
  }


  public static int nextGlobalId() {
    return globalIdCounter.getAndIncrement();
  }

}
