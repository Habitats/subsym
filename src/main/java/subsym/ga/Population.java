package subsym.ga;

import com.google.common.collect.MinMaxPriorityQueue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by anon on 21.02.2015.
 */
public class Population {

  private final int maxPopulationSize;
  private final int bitVectorSize;
  private MinMaxPriorityQueue<Genotype> population;
  private GeneticProblem.AdultSelection selectionMode;
  private int currentGeneration = 0;

  public static double mixingRate = 0.4;
  public static double overProductionRate = 2;
  private int freeSpots;

  public Population(int populationSize, int bitVectorSize) {
    this.maxPopulationSize = populationSize;
    this.bitVectorSize = bitVectorSize;
    population = MinMaxPriorityQueue.create();
    IntStream.range(0, populationSize).forEach(i -> population.add(Genotype.getRandom(bitVectorSize)));
  }


  public void selectAdults(GeneticProblem.AdultSelection selectionMode) {
    this.selectionMode = selectionMode;
    switch (selectionMode) {
      case FULL_TURNOVER:
        population.stream().filter(v -> v.getGeneration() <= currentGeneration).forEach(v -> v.tagForRemoval());
        freeSpots = (int) population.stream().filter(v -> v.shouldDie()).count();
        break;
      case OVER_PRODUCTION:
        population.stream().filter(v -> v.getGeneration() <= currentGeneration).forEach(v -> v.tagForRemoval());
        freeSpots = (int) (population.stream().filter(v -> v.shouldDie()).count() * overProductionRate);
        break;
      case MIXING:
        population.stream().filter(v -> v.getGeneration() <= currentGeneration).forEach(v -> v.tagForRemoval());
        population.stream().sorted().limit(getAdultLimit()).forEach(v -> v.tagForRevival());
        freeSpots = (int) population.stream().filter(v -> v.shouldDie()).count();
        break;
    }
  }

  public int getAdultLimit() {
    return (int) (mixingRate * maxPopulationSize);
  }

  public int getChildLimit() {
    return (int) ((1 - mixingRate) * maxPopulationSize);
  }

  public void crossOver(double crossOverRate) {
    while (getFreeSpots() > 0) {
      globalCrossover(crossOverRate, Math.random());
    }
  }

  public int getFreeSpots() {
    return freeSpots;
  }

  public void globalCrossover(double crossOverRate, double cut) {
    if (crossOverRate < Math.random()) {
      return;
    }
    List<Genotype> populationList = new ArrayList<>(population.stream() //
                                                        .filter(v -> v.getGeneration() <= currentGeneration)//
                                                        .collect(Collectors.toList()));
    Genotype p1 = getCrossOverCandidate(populationList);
    Genotype p2 = getCrossOverCandidate(populationList);

    Genotype c1 = Genotype.crossOver(p1, p1, cut);
    Genotype c2 = Genotype.crossOver(p2, p2, cut);
    c1.setGeneration(currentGeneration + 1);
    c2.setGeneration(currentGeneration + 1);
    population.add(c1);
    population.add(c2);

    freeSpots -= 2;
  }

  public Genotype getCrossOverCandidate(List<Genotype> populationList) {
    int sum = populationList.stream() //
        .mapToInt(v -> v.fitness()).sum();
    List<Double> weights = populationList.stream() //
        .map(v -> Math.random() * v.fitness() / sum) //
        .collect(Collectors.toList());
    int index = weights.indexOf(Collections.max(weights));
    return populationList.remove(index);
  }

  public void mutate(double genomeMutationRate, double genotypeMutationRate) {
    List<Genotype> children = new ArrayList<>(population);
    Collections.shuffle(children);
    int numBitsToMutate = (int) Math.ceil(genomeMutationRate * population.peek().size());
    int genotypesToMutate = (int) Math.ceil(genotypeMutationRate * maxPopulationSize);
    children.stream() //
        .filter(v -> !v.shouldDie()) //
        .limit(genotypesToMutate) //
        .forEach(v -> v.mutate(numBitsToMutate));
  }

  public void cleanUp() {
    switch (selectionMode) {
      case FULL_TURNOVER:
        fullTurnoverSelection();
        break;
      case OVER_PRODUCTION:
        overProductionSelection();
        break;
      case MIXING:
        mixingSelection();
        break;
    }
    currentGeneration++;
  }

  public void fullTurnoverSelection() {
    population.removeIf(v -> v.getGeneration() <= currentGeneration && v.shouldDie());
  }

  public void overProductionSelection() {
    population.removeIf(v -> v.getGeneration() <= currentGeneration && v.shouldDie());
    while (population.size() > maxPopulationSize) {
      population.removeLast();
    }
  }

  public void mixingSelection() {
    population.stream().sorted().limit(getAdultLimit()).forEach(v -> v.tagForRevival());
    population.removeIf(v -> v.getGeneration() <= currentGeneration && v.shouldDie());
    while (population.size() > maxPopulationSize) {
      population.removeLast();
    }
  }

  public void add(Genotype genotype) {
    population.add(genotype);
  }

  public Genotype getWorstGenotype() {
    return population.peekLast();
  }

  public Genotype getBestGenotype() {
    return population.peekFirst();
  }

  public int size() {
    return population.size();
  }
}
