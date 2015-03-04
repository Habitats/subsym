package subsym.genetics.matingselection;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import subsym.genetics.Genotype;

/**
 * Created by anon on 04.03.2015.
 */
public class Boltzman implements MatingSelection {

  private double temperature = 100;

  @Override
  public Genotype selectNext(List<Genotype> populationList) {
    double averageFitness = populationList.stream().mapToDouble(Genotype::fitness).average().getAsDouble();
    Function<Genotype, Double> toBoltzmanScale = v -> Math.exp(v.fitness() / temperature) / Math.exp(averageFitness);
    List<Double> weights = populationList.stream()//
        .map(toBoltzmanScale).map(v -> Math.random() * v).collect(Collectors.toList());

    Genotype genotype = populationList.remove(weights.indexOf(Collections.max(weights)));

    temperature *= 0.9;
    return genotype;
  }
}
