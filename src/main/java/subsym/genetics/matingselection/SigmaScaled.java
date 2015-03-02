package subsym.genetics.matingselection;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import subsym.genetics.Genotype;
import subsym.genetics.Population;

/**
 * Created by Patrick on 02.03.2015.
 */
public class SigmaScaled implements MatingSelection {

  public Genotype selectNext(List<Genotype> populationList) {
    double averageFitness = populationList.stream().mapToDouble(Genotype::fitness).average().getAsDouble();
    double standardDeviation = Population.standardDeviation(populationList);
    Function<Genotype, Double> toSigmaScale = v -> //
        Math.random() * (1 + (v.fitness() - averageFitness) / 2 * standardDeviation);
    List<Double> weights = populationList.stream().map(toSigmaScale).collect(Collectors.toList());
    int index = weights.indexOf(Collections.max(weights));
    return populationList.remove(index);
  }
}
