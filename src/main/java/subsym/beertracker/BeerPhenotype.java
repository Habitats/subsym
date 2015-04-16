package subsym.beertracker;

import java.util.List;
import java.util.stream.Collectors;

import subsym.ann.AnnPreferences;
import subsym.ann.ArtificialNeuralNetwork;
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

    switch (prefs.getBeerScenario()) {
      case WRAP:
        ann = ArtificialNeuralNetwork.buildWrappingCtrnn(prefs);
        break;
      case NO_WRAP:
        ann = ArtificialNeuralNetwork.buildNoWrapCtrnn(prefs);
        break;
      case PULL:
        ann = ArtificialNeuralNetwork.buildPullingCtrnn(prefs);
        break;
      default:
        throw new IllegalStateException("No scenario!");
    }
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
      BeerGame game = new BeerGame(prefs.getBeerScenario());
      setValues(beerGenotype.toList(), ann);
      score = game.simulate(ann, 0, false);
    }
    return score;
  }

  public static void setValues(List<Integer> values, ArtificialNeuralNetwork ann) {
    int numNodes = (int) ann.getOutputNodeStream().count();
    int numWeights = ann.getNumWeights();
    List<Double> normalizedValues = getNormalizedValues(values);

    List<Double> weights = normalizedValues.subList(0, numWeights);
    List<Double> timeConstants = getTimeConstants(normalizedValues.subList(numWeights, numWeights + numNodes));
    List<Double> gains = getGains(normalizedValues.subList(numWeights + numNodes, numWeights + numNodes * 2));
    ann.setWeights(weights);
    ann.setTimeConstants(timeConstants);
    ann.setGains(gains);
  }

  private static List<Double> getTimeConstants(List<Double> values) {
    return values.stream().map(g -> g + 1).collect(Collectors.toList());
  }

  private static List<Double> getGains(List<Double> values) {
    return values.stream().map(g -> (g * 4) + 1).collect(Collectors.toList());
  }

  private static List<Double> getNormalizedValues(List<Integer> values) {
    return values.stream().mapToDouble(BeerPhenotype::normalize).boxed().collect(Collectors.toList());
  }

  @Override
  public void resetFitness() {
    score = null;
  }

  public static double normalize(int v) {
    return ((v * 4) % 1000) / 1000.;
  }

  public ArtificialNeuralNetwork getArtificialNeuralNetwork() {
    return ann;
  }

  @Override
  public String toString() {
    return beerGenotype.toList().stream().map(i -> String.format("%3d", i).replaceAll(" ", "0"))
        .collect(Collectors.joining("  ", "Pheno > ", ""));
  }

}
