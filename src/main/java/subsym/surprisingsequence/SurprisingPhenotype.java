package subsym.surprisingsequence;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import subsym.ga.Phenotype;

/**
 * Created by anon on 23.02.2015.
 */
public class SurprisingPhenotype implements Phenotype {

  private final SurprisingGenotype surprisingGenotype;

  public SurprisingPhenotype(SurprisingGenotype surprisingGenotype) {
    this.surprisingGenotype = surprisingGenotype;
  }

  @Override
  public double fitness() {
    List<Integer> ints = surprisingGenotype.toList();
    double fitness = ints.stream().mapToInt(v -> v).sum();
    double normalizingFactor = surprisingGenotype.toList().size() * Collections.max(surprisingGenotype.getAlphabet());
    fitness /= normalizingFactor;
    return fitness;
  }

  @Override
  public String toString() {
    return surprisingGenotype.toList().stream().map(v -> String.valueOf(v)).collect(Collectors.joining(", ", " > Phenotype > ",""));
  }
}
