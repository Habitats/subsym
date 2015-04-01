package subsym.beertracker;

import java.util.stream.IntStream;

import subsym.genetics.GeneticPreferences;
import subsym.genetics.GeneticProblem;

/**
 * Created by Patrick on 30.03.2015.
 */
public class BeerTracker extends GeneticProblem {

  private static final String TAG = BeerTracker.class.getSimpleName();

  public BeerTracker(GeneticPreferences prefs) {
    super(prefs);
  }

  public static void demo() {
    BeerGui.demo();
  }

  @Override
  protected double getCrossoverCut() {
    return 0;
  }

  @Override
  public void initPopulation() {
    IntStream.range(0, getPopulationSize()).forEach(i -> {
      BeerGenotype genotype = new BeerGenotype(getPreferences().getAnnPreferences());
      getPopulation().add(genotype);
    });
  }

  @Override
  public boolean solution() {
    return getPopulation().getCurrentGeneration() == getPreferences().getMaxGenerations();
  }

  @Override
  public GeneticProblem newInstance(GeneticPreferences prefs) {
    return new BeerTracker(prefs);
  }

  @Override
  public GeneticProblem newInstance() {
    return new BeerTracker(getPreferences());
  }

  @Override
  public void increment(int increment) {
  }

  @Override
  public void onSolved() {
  }
}
