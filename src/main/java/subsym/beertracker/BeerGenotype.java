package subsym.beertracker;

import subsym.ann.AnnPreferences;
import subsym.genetics.Genotype;
import subsym.genetics.Phenotype;

/**
 * Created by Patrick on 01.04.2015.
 */
public class BeerGenotype extends Genotype {

  private static final String TAG = BeerGenotype.class.getSimpleName();
  private BeerPhenotype phenotype;
  private AnnPreferences prefs;

  public BeerGenotype(AnnPreferences prefs) {
    super(prefs.shouldGrayCode());
    this.prefs = prefs;
    phenotype = new BeerPhenotype(this, prefs);
  }

  public void randomize() {
    int numNodes = phenotype.getNodeCount();
    int numWeights = phenotype.getNumWeights();
    setRandom((numWeights + numNodes * 2) * getBitGroupSize());
  }

  @Override
  protected Genotype newInstance() {
    return new BeerGenotype(prefs);
  }

  @Override
  public void copy(Genotype copy) {
    BeerGenotype beerCopy = (BeerGenotype) copy;
    beerCopy.phenotype = new BeerPhenotype(beerCopy, prefs);
  }

  @Override
  public Phenotype getPhenotype() {
    return phenotype;
  }

  @Override
  public int getBitGroupSize() {
    return 8;
  }

  @Override
  public String toString() {
    return getPhenotype() + " > " + String.format("%.3f", fitness());
  }
}
