package subsym.ga;

import com.google.common.collect.MinMaxPriorityQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by anon on 21.02.2015.
 */
public class Population {

  private final int maxPopulationSize;
  private Queue<Genotype> population;
  private double mixingRate = 0.5;
  private GeneticProblem.AdultSelection selectionMode;
  private int currentGeneration = 0;

  public Population(int populationSize, int bitVectorSize) {
    this.maxPopulationSize = populationSize;
    population = MinMaxPriorityQueue.<Genotype>orderedBy((o1, o2) -> o2.numOnes() - o1.numOnes()) //
        .maximumSize(populationSize).create();
    IntStream.range(0, populationSize).forEach(i -> population.add(Genotype.getRandom(bitVectorSize)));
  }

  public Genotype getBestGenotype() {
    return population.peek();
  }

  public void selectAdults(GeneticProblem.AdultSelection selectionMode) {
    this.selectionMode = selectionMode;
    switch (selectionMode) {
      case FULL_TURNOVER:
        population.forEach(v -> v.tagForRemoval());
        break;
      case OVER_PRODUCTION:
        population.forEach(v -> v.tagForRemoval());
        break;
      case MIXING:
        population.forEach(v -> v.tagForRemoval());
        population.stream().sorted().limit(getAdultLimit()).forEach(v -> v.tagForRevival());
        break;
    }
  }

  public int getAdultLimit() {
    return (int) (mixingRate * population.size());
  }

  public void crossOver(double crossOverRate) {
    switch (selectionMode) {
      case FULL_TURNOVER:
        IntStream.range(0, maxPopulationSize).forEach(i -> crossOverSingle(crossOverRate, Math.random()));
        break;
      case OVER_PRODUCTION:
        IntStream.range(0, maxPopulationSize * 2).forEach(i -> crossOverSingle(crossOverRate, Math.random()));
        break;
      case MIXING:
        population.stream().sorted().limit(getAdultLimit()).forEach(v -> v.tagForRemoval());
        break;
    }
  }

  public void crossOverSingle(double crossOverRate, double cut) {
//    if (crossOverRate < Math.random()) {
//      return;
//    }
    List<Genotype> populationList = new ArrayList<>(population.stream() //
                                                        .filter(v -> v.getGeneration() <= currentGeneration)//
                                                        .collect(Collectors.toList()));
    Genotype p1 = getCrossOverCandidate(populationList);
    Genotype p2 = getCrossOverCandidate(populationList);

    Genotype child = Genotype.crossOver(p1, p2, cut);
    child.setGeneration(currentGeneration + 1);
    population.add(child);
  }

  public Genotype getCrossOverCandidate(List<Genotype> populationList) {
    int sum = populationList.stream() //
        .mapToInt(v -> v.numOnes()).sum();
    List<Double> weights = populationList.stream() //
        .map(v -> Math.random() * v.numOnes() / sum) //
        .collect(Collectors.toList());
    int index = weights.indexOf(Collections.max(weights));
    return populationList.remove(index);
  }

  public void mutate(double genomeMutationRate, double genotypeMutationRate) {
    List<Genotype> children = new ArrayList<>(population);
    Collections.shuffle(children);
    int numBitsToMutate = (int) (genomeMutationRate * population.peek().size());
    int genotypesToMutate = (int) (genotypeMutationRate * population.size());
    children.stream() //
        .limit(genotypesToMutate) //
        .forEach(v -> v.mutate(numBitsToMutate));
  }

  public void cleanUp() {
    population.removeIf(v -> v.shouldDie());
    currentGeneration++;
  }
}
