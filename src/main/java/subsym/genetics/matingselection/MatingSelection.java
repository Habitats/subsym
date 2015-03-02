package subsym.genetics.matingselection;

import java.util.List;

import subsym.genetics.Genotype;

/**
 * Created by Patrick on 02.03.2015.
 */
public interface MatingSelection {

  Genotype selectNext(List<Genotype> populationList);

}
