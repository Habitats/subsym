package subsym.beertracker;

import java.util.stream.IntStream;

import subsym.Log;
import subsym.ann.AnnPreferences;
import subsym.ann.ArtificialNeuralNetwork;
import subsym.genetics.GeneticPreferences;
import subsym.genetics.GeneticProblem;

/**
 * Created by Patrick on 30.03.2015.
 */
public class BeerTracker extends GeneticProblem {

  private static final String TAG = BeerTracker.class.getSimpleName();
  private AnnPreferences annPrefs;
  private int count = 0;
  private long start;

  public BeerTracker(GeneticPreferences prefs, AnnPreferences annPrefs) {
    super(prefs);
    this.annPrefs = annPrefs;
  }


  @Override
  protected double getCrossoverCut() {
    return 0;
  }

  @Override
  public void initPopulation() {
    start = System.currentTimeMillis();
    IntStream.range(0, getPopulationSize()).forEach(i -> {
      BeerGenotype genotype = new BeerGenotype(annPrefs);
      genotype.randomize();
      getPopulation().add(genotype);
    });
  }

  @Override
  public boolean solution() {
    return getPopulation().getCurrentGeneration() == getPreferences().getMaxGenerations();
  }

  @Override
  public GeneticProblem newInstance(GeneticPreferences prefs) {
    return new BeerTracker(prefs, annPrefs);
  }

  @Override
  public GeneticProblem newInstance() {
    return new BeerTracker(getPreferences(), annPrefs);
  }

  @Override
  public void increment(int increment) {
  }

  @Override
  public void onSolved() {
    BeerGenotype best = (BeerGenotype) getPopulation().getBestGenotype();
    Log.v(TAG, best.fitness());
    BeerPhenotype pheno = (BeerPhenotype) best.getPhenotype();
    Log.v(TAG, pheno.fitness());
    int v = (int) ((System.currentTimeMillis() - start) / 1000.);
    Log.v(TAG, String.format("Search finished in %d seconds", v));

    ArtificialNeuralNetwork ann = pheno.getArtificialNeuralNetwork();
    BeerGame game = new BeerGame(annPrefs.getBeerScenario());
    game.initGui();
    game.simulate(ann, 0 + count, true);
    ann.statePrint();
    count++;

    Log.v(TAG, this);
  }
}
