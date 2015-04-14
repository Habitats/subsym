package subsym.beertracker;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.ann.AnnPreferences;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.WeightBound;
import subsym.ann.activation.Sigmoid;
import subsym.ann.nodes.AnnNodes;
import subsym.genetics.Phenotype;

/**
 * Created by Patrick on 01.04.2015.
 */
public class BeerPhenotype implements Phenotype {

  private final BeerGenotype beerGenotype;
  private final AnnPreferences prefs;
  private final ArtificialNeuralNetwork ann;
  private Double score = null;

  public BeerPhenotype(BeerGenotype beerGenotype, AnnPreferences prefs) {
    this.beerGenotype = beerGenotype;
    this.prefs = prefs;

    ann = buildContinuousTimeRecurrentNeuralNetwork(prefs);
  }



  public int getNumWeights() {
    return ann.getNumWeights();
  }

  public int getNodeCount() {
    return (int) ann.getOutputNodeStream().count();
  }

  @Override
  public double fitness() {
    if (score == null) {
      BeerGame game = new BeerGame();
      setValues(beerGenotype,  ann);
      score = game.simulate(ann, 0, false);
    }
    return score;
  }

  private static ArtificialNeuralNetwork buildContinuousTimeRecurrentNeuralNetwork(AnnPreferences prefs) {
    AnnNodes inputs = AnnNodes.createInput(new WeightBound(-5., 5.), 0., 0., 0., 0., 0.);
    AnnNodes outputs = AnnNodes.createOutput(new WeightBound(-5, 5), 2);
    ArtificialNeuralNetwork ann = new ArtificialNeuralNetwork(prefs, inputs, outputs);
    ann.setStateful();

    List<AnnNodes> layers = ann.getLayers();
    AnnNodes nodes = new AnnNodes(IntStream.range(1, layers.size())//
                                      .mapToObj(layers::get).flatMap(AnnNodes::stream)//
                                      .collect(Collectors.toList()));
    ann.addBiasNode(new WeightBound(-10, 0), nodes);
    ann.getLayers().subList(1, layers.size() - 1).stream().forEach(layer -> {
      layer.stream().forEach(n -> {
        layer.stream().filter(other -> !n.equals(other)).forEach(n::crossConnect);
      });
    });
    ann.addSelfNode(new WeightBound(-5, 5), nodes);
    ann.getLayers().stream().flatMap(AnnNodes::stream).forEach(n -> n.setActivationFunction(new Sigmoid()));

    ann.setIds();
    return ann;
  }

  private void setValues(BeerGenotype beerGenotype, ArtificialNeuralNetwork ann) {
    int numNodes = (int) ann.getOutputNodeStream().count();
    int numWeights = ann.getNumWeights();

    List<Double> normalizedValues = getNormalizedValues(beerGenotype.toList());

    List<Double> weights = normalizedValues.subList(0, numWeights);
    List<Double> timeConstants = getTimeConstants(normalizedValues.subList(numWeights, numWeights + numNodes));
    List<Double> gains = getGains(normalizedValues.subList(numWeights + numNodes, numWeights + numNodes * 2));
    ann.setWeights(weights);
    ann.setTimeConstants(timeConstants);
    ann.setGains(gains);
  }

  private List<Double> getTimeConstants(List<Double> values) {
    return values.stream().map(g -> g + 1).collect(Collectors.toList());
  }

  private List<Double> getGains(List<Double> values) {
    return values.stream().map(g -> (g * 4) + 1).collect(Collectors.toList());
  }

  private List<Double> getNormalizedValues(List<Integer> values) {
    return values.stream().mapToDouble(this::normalize).boxed().collect(Collectors.toList());
  }

  private double normalize(int v) {
    return ((v * 4) % 1000) / 1000.;
  }

  public ArtificialNeuralNetwork getArtificialNeuralNetwork() {
    return ann;
  }

  @Override
  public String toString() {
    return getNormalizedValues(beerGenotype.toList()).stream().map(i -> String.format("%.2f", i))
        .collect(Collectors.joining(" ", " > Pheno > ", ""));
  }
}
