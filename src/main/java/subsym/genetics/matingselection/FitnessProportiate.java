package subsym.genetics.matingselection;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import subsym.genetics.Genotype;

/**
 * Created by Patrick on 02.03.2015.
 */
public class FitnessProportiate implements MatingSelection {

  @Override
  public Genotype selectNext(List<Genotype> populationList) {
    double sum = populationList.stream().mapToDouble(Genotype::fitness).sum();
    List<Double> weights = populationList.stream() //
        .map(v -> Math.random() * v.fitness() / sum) //
        .collect(Collectors.toList());
    int index = weights.indexOf(Collections.max(weights));
    return populationList.remove(index);
  }
}
