package subsym.beertracker;

import java.util.List;
import java.util.stream.Collectors;

import subsym.ann.AnnPreferences;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.ann.ContinuousTimeRecurrentNeuralNetwork;
import subsym.ann.nodes.AnnNodes;
import subsym.genetics.Phenotype;

/**
 * Created by Patrick on 01.04.2015.
 */
public class BeerPhenotype implements Phenotype {

  private final BeerGenotype beerGenotype;
  private final AnnPreferences prefs;
  private final ArtificialNeuralNetwork ann;
  private ArtificialNeuralNetwork artificialNeuralNetwork;

  public BeerPhenotype(BeerGenotype beerGenotype, AnnPreferences prefs) {
    this.beerGenotype = beerGenotype;
    this.prefs = prefs;

    AnnNodes inputs = AnnNodes.createInput(0., 0., 0., 0., 0.);
    AnnNodes outputs = AnnNodes.createOutput(2);
    ann = new ContinuousTimeRecurrentNeuralNetwork(prefs, inputs, outputs);
    this.beerGenotype.setRandom(ann.getNumWeights() * beerGenotype.getBitGroupSize());
    ann.setWeights(getNormalizedValues(beerGenotype.toList()));
  }

  private List<Double> getNormalizedValues(List<Integer> values) {
    return values.stream().mapToDouble(this::normalize).boxed().collect(Collectors.toList());
  }

  private double normalize(int v) {
    return (v % 1000) / 1000.;
  }

  @Override
  public double fitness() {
    BeerGame game = new BeerGame();
    return game.simulate(ann);
  }

  public ArtificialNeuralNetwork getArtificialNeuralNetwork() {
    return ann;
  }
}
