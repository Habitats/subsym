package subsym.genetics.matingselection;

import java.util.List;

import subsym.genetics.Genotype;

/**
 * Created by Patrick on 02.03.2015.
 */
public class FitnessProportiate implements MatingSelection {

  @Override
  public Genotype selectNext(List<Genotype> populationList) {
    Genotype parent = spinAndRemove(populationList, v -> v.fitness());
    return parent;
  }
}
