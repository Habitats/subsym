package subsym.surprisingsequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.genetics.Phenotype;

/**
 * Created by anon on 23.02.2015.
 */
public class SurprisingPhenotype implements Phenotype {

  private final SurprisingGenotype surprisingGenotype;
  private final Integer maxWidth;
  private final Integer maxValue;

  public SurprisingPhenotype(SurprisingGenotype surprisingGenotype) {
    this.surprisingGenotype = surprisingGenotype;
    maxValue = Collections.max(surprisingGenotype.getAlphabet());
    maxWidth = Integer.toString(maxValue).length() + 1;
  }

  @Override
  public double fitness() {
//    return getLocalSurprisingSequenceFitness(surprisingGenotype.toList());
    return isValidGenotype() ? getGlobalSurprisingSequenceFitness(surprisingGenotype.toList()) : 0;
  }

  public boolean isValidGenotype() {
    return !surprisingGenotype.toList().stream() //
        .filter(v -> !surprisingGenotype.getAlphabet().contains(v)).findFirst().isPresent();
  }

  public static double getLocalSurprisingSequenceFitness(List<Integer> ints) {
    return getAverageDistinct(ints, 0);
  }

  public static double getGlobalSurprisingSequenceFitness(List<Integer> ints) {
    return getAverageDistinct(ints, ints.size() - 2);
  }

  private static double getAverageDistinct(List<Integer> ints, int x) {
    List<Integer> shifted = new ArrayList<>(ints);
    IntUnaryOperator toDistinctCount = i -> {
      shifted.add(shifted.remove(0));
      int distinctCount = getDistinctCount(ints, shifted, i);
      return distinctCount + i; //
    };
    double unique = IntStream.range(0, x + 1).map(toDistinctCount).average().getAsDouble();
    return unique / (double) (ints.size() - 1);
  }

  private static int getDistinctCount(List<Integer> ints, List<Integer> shifted, int startIndex) {
    return (int) IntStream.range(0, ints.size() - 1 - startIndex) //
        .mapToObj(i -> Integer.toString(ints.get(i)) + ":" + Integer.toString(shifted.get(i))) //
        .distinct().count();
  }

  private double getMaxSumFitness() {
    List<Integer> ints = surprisingGenotype.toList();
    double fitness = ints.stream().mapToInt(v -> v).sum();
    double normalizingFactor = surprisingGenotype.toList().size() * maxValue;
    fitness /= normalizingFactor;
    return fitness;
  }

  @Override
  public String toString() {
    return surprisingGenotype.toList().stream() //
        .map(v -> String.format("%" + maxWidth + "d", v)) //
        .collect(Collectors.joining(", ", String.format(" > Phenotype > S: %d - L: %d - Seq: ", //
                                                        surprisingGenotype.getAlphabet().size(),
                                                        surprisingGenotype.toList().size()), ""));
  }
}
