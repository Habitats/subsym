package subsym.beertracker;

import subsym.ann.AnnPreferences;
import subsym.genetics.Phenotype;

/**
 * Created by Patrick on 01.04.2015.
 */
public class BeerPhenotype implements Phenotype{

  public BeerPhenotype(BeerGenotype beerGenotype, AnnPreferences prefs) {
  }

  @Override
  public double fitness() {
    return 0;
  }
}
