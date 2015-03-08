package subsym.genetics.matingselection;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import subsym.genetics.Genotype;

/**
 * Created by anon on 04.03.2015.
 */
public class Rank implements MatingSelection {


  @Override
  public Genotype selectNext(List<Genotype> populationList) {
    double min = populationList.stream().map(Genotype::fitness).min(Comparator.<Double>naturalOrder()).get();
    double max = populationList.stream().map(Genotype::fitness).max(Comparator.<Double>naturalOrder()).get();
    Function<Genotype, Double> toRank = v -> min + (max - min) * (v.fitness() - 1) / (populationList.size() - 1);

    Genotype parent = spinAndRemove(populationList, toRank);
    return parent;
  }
}
