package subsym.surprisingsequence;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import subsym.ga.GeneticProblem;

/**
 * Created by anon on 23.02.2015.
 */
public class SurprisingSequences extends GeneticProblem {

  private final List<Integer> alphabet;
  private final int length;

  public SurprisingSequences(int populationSize, int alphabetSize, int length, double crossOverRate,
                             double genomeMutationRate, double genotypeMutationRate, AdultSelection adultSelectMode,
                             MateSelection matingMode) {
    super(populationSize, crossOverRate, genomeMutationRate, genotypeMutationRate, adultSelectMode, matingMode);

    this.length = length;
    alphabet = IntStream.range(0, alphabetSize).boxed().collect(Collectors.toList());
  }

  private List<Integer> createPermutation(List<Integer> alphabet, int length) {
    Random random = new Random();
    return IntStream.range(0, length)//
        .map(v -> alphabet.get(random.nextInt(alphabet.size()))).boxed().collect(Collectors.toList());
  }

  @Override
  public void initPopulation() {
    IntStream.range(0, getPopulationSize()).forEach(i -> {
      List<Integer> permutation = createPermutation(alphabet, length);
      getPopulation().add(new SurprisingGenotype(permutation, alphabet));
    });
  }

  @Override
  public boolean solution() {
    return getPopulation().getBestGenotype().fitness() > 0.9;
  }
}
