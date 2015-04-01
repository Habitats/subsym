package subsym.beertracker;

import subsym.ann.AnnPreferences;
import subsym.genetics.Genotype;
import subsym.genetics.Phenotype;

/**
 * Created by Patrick on 01.04.2015.
 */
public class BeerGenotype extends Genotype{

  private BeerPhenotype phenotype;
  private AnnPreferences prefs;

  public BeerGenotype(AnnPreferences prefs) {
    this.prefs = prefs;
    phenotype = new BeerPhenotype(this, prefs);
  }

  @Override
  protected Genotype newInstance() {
    return new BeerGenotype(prefs);
  }

  @Override
  public void copy(Genotype copy) {
    BeerGenotype beerCopy = (BeerGenotype) copy;
    beerCopy.phenotype = new BeerPhenotype(this, prefs);
  }

  @Override
  public Phenotype getPhenotype() {
    return phenotype;
  }

  @Override
  public int getBitGroupSize() {
    return 10;
  }
}
