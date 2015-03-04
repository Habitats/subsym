package subsym.genetics.matingselection;

import java.util.Arrays;
import java.util.List;

import subsym.genetics.Genotype;

/**
 * Created by Patrick on 02.03.2015.
 */
public interface MatingSelection {

  Genotype selectNext(List<Genotype> populationList);

  static List<String> values() {
    return Arrays
        .asList(FitnessProportiate.class.getSimpleName(), SigmaScaled.class.getSimpleName(), Tournament.class.getSimpleName(), Boltzman.class.getSimpleName(), Rank.class.getSimpleName());
  }
}
