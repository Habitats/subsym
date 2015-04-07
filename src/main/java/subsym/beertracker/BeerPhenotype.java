package subsym.beertracker;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.ann.AnnPreferences;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.Sigmoid;
import subsym.ann.nodes.AnnNodes;
import subsym.genetics.Phenotype;

/**
 * Created by Patrick on 01.04.2015.
 */
public class BeerPhenotype implements Phenotype {

  private final BeerGenotype beerGenotype;
  private final AnnPreferences prefs;
  private final ArtificialNeuralNetwork ann;

  public BeerPhenotype(BeerGenotype beerGenotype, AnnPreferences prefs) {
    this.beerGenotype = beerGenotype;
    this.prefs = prefs;

    ann = buildContinuousTimeRecurrentNeuralNetwork(beerGenotype, prefs);
  }

  @Override
  public double fitness() {
    BeerGame game = new BeerGame();
    return game.simulate(ann);
  }

  private ArtificialNeuralNetwork buildContinuousTimeRecurrentNeuralNetwork(BeerGenotype beerGenotype, AnnPreferences prefs) {
    AnnNodes inputs = AnnNodes.createInput(0., 0., 0., 0., 0.);
    AnnNodes outputs = AnnNodes.createOutput(2);
    ArtificialNeuralNetwork ann = new ArtificialNeuralNetwork(prefs, inputs, outputs, new Sigmoid());
    ann.setStateful();

    List<AnnNodes> layers = ann.getLayers();
    AnnNodes nodes = new AnnNodes(IntStream.range(1, layers.size())//
                                      .mapToObj(layers::get).flatMap(AnnNodes::stream)//
                                      .collect(Collectors.toList()));
    ann.addBiasNode(nodes);
    ann.addSelfNode(nodes);
    ann.getLayers().stream().forEach(layer -> {
      layer.stream().forEach(n -> {
        layer.stream().filter(other -> !n.equals(other)).forEach(n::connect);
      });
    });

    this.beerGenotype.setRandom(ann.getNumWeights() * beerGenotype.getBitGroupSize() * 3);

    List<Double> normalizedValues = getNormalizedValues(beerGenotype.toList());

    List<Double> weights = normalizedValues.subList(0, ann.getNumWeights());
    List<Double> timeConstants = getTimeConstants(normalizedValues.subList(ann.getNumWeights(), ann.getNumWeights() * 2));
    List<Double> gains = getGains(normalizedValues.subList(ann.getNumWeights() * 2, ann.getNumWeights() * 3));
    ann.setWeights(weights);
    ann.setTimeConstants(timeConstants);
    ann.setGains(gains);

    ann.getLayers().stream().flatMap(AnnNodes::stream).forEach(n -> n.setActivationFunction(new Sigmoid()));
    return ann;
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
    return (v % 1000) / 1000.;
  }

  public ArtificialNeuralNetwork getArtificialNeuralNetwork() {
    return ann;
  }
}
