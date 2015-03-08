package subsym.genetics.matingselection;

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
    double average = populationList.stream().mapToDouble(Genotype::fitness).average().getAsDouble();
    double standardDeviation = //
        Population.standardDeviation(populationList.stream().map(i -> i.fitness()).collect(Collectors.toList()));

    Function<Genotype, Double> toSigmaScale = v -> (1 + (v.fitness() - average) / 2 * standardDeviation);
    Genotype parent = spinAndRemove(populationList, toSigmaScale);
    return parent;
  }
}
