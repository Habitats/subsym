package subsym.genetics.matingselection;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import subsym.genetics.Genotype;

/**
 * Created by anon on 04.03.2015.
 */
public class Rank implements MatingSelection {


  @Override
  public Genotype selectNext(List<Genotype> populationList) {
    double min = populationList.stream().map(Genotype::fitness).min(Comparator.<Double>naturalOrder()).get();
    double max = populationList.stream().map(Genotype::fitness).max(Comparator.<Double>naturalOrder()).get();
    Function<Integer, Double> mapper = v -> min + (max - min) * (v - 1) / (populationList.size() - 1);

    AtomicInteger i = new AtomicInteger(populationList.size());
    List<Double> weights = populationList.stream()//
        .mapToDouble(Genotype::fitness)//
        .sorted()//
        .mapToInt(v -> i.decrementAndGet()).boxed()//
        .map(mapper).collect(Collectors.toList());

    Genotype genotype = populationList.remove(weights.indexOf(Collections.max(weights)));
    return genotype;
  }
}
