package subsym.beertracker;

import java.util.stream.Collectors;

import subsym.ann.AnnPreferences;
import subsym.genetics.Phenotype;

/**
 * Created by Patrick on 01.04.2015.
 */
public class BeerPhenotype implements Phenotype {

  private final BeerGenotype beerGenotype;
  private final AnnPreferences prefs;
  private final BeerGame game;
  private Double score = null;

  public BeerPhenotype(BeerGenotype beerGenotype, AnnPreferences prefs) {
    this.beerGenotype = beerGenotype;
    this.prefs = prefs;
    game = new BeerGame(prefs);
  }

  public int getNumWeights() {
    return game.getNumWeights();
  }

  public int getNodeCount() {
    return (int) game.getOutputNodeStream().count();
  }

  @Override
  public double fitness() {
    if (score == null) {
      game.setValues(beerGenotype.toList());
      score = game.simulate(prefs.getSimulationSeed() + beerGenotype.getCurrentGeneration(), false);
    }
    return score;
  }


  @Override
  public void resetFitness() {
    score = null;
  }

  public static double normalize(int v) {
    return ((v * 4) % 1000) / 1000.;
  }

  @Override
  public String toString() {
    return beerGenotype.toList().stream().map(i -> String.format("%3d", i).replaceAll(" ", "0")).collect(Collectors.joining(" "));
  }

}
