package subsym.ga;

import com.google.common.collect.MinMaxPriorityQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by anon on 21.02.2015.
 */
public class Population {

  private final int maxPopulationSize;

  private MinMaxPriorityQueue<Genotype> currentPopulation;
  private List<Genotype> nextGeneration;

  private GeneticProblem.AdultSelection selectionMode;

  public static double mixingRate = 0.4;
  public static double overProductionRate = 2;
  public static double tornamentLimit = .2;
  private int freeSpots = 0;
  private int currentGeneration = 0;

  public Population(int maxPopulationSize) {
    this.maxPopulationSize = maxPopulationSize;
    currentPopulation = MinMaxPriorityQueue.create();
  }

  public void selectAdults(GeneticProblem.AdultSelection selectionMode) {
    nextGeneration = new ArrayList<>();
    this.selectionMode = selectionMode;
    switch (selectionMode) {
      case FULL_TURNOVER:
        currentPopulation.stream().forEach(v -> v.tagForRemoval());
        freeSpots = maxPopulationSize;
        break;
      case OVER_PRODUCTION:
        currentPopulation.stream().forEach(v -> v.tagForRemoval());
        freeSpots = (int) (maxPopulationSize * overProductionRate);
        break;
      case MIXING:
        currentPopulation.stream().forEach(v -> v.tagForRemoval());
        currentPopulation.stream().sorted().limit(getAdultLimit()).forEach(v -> v.tagForRevival());
        freeSpots = (int) currentPopulation.stream().filter(v -> v.shouldDie()).count();
        break;
    }
  }

  public void crossOver(double crossOverRate, double cut, GeneticProblem.MateSelection matingMode) {
    while (getFreeSpots() > 0) {
      crossOverSingle(crossOverRate, cut, matingMode);
    }
  }

  public void crossOverSingle(double crossOverRate, double cut, GeneticProblem.MateSelection matingMode) {
    if (crossOverRate < Math.random()) {
      return;
    }
    List<Genotype> populationList = new ArrayList<>(currentPopulation.stream().collect(Collectors.toList()));
    Genotype p1 = null;
    Genotype p2 = null;
    switch (matingMode) {
      case FITNESS_PROPORTIONATE:
        p1 = getFitnessProportionateParent(populationList);
        p2 = getFitnessProportionateParent(populationList);
        break;
      case SIGMA_SCALING:
        p1 = getSigmaScaledParent(populationList);
        p2 = getSigmaScaledParent(populationList);
        break;
      case TOURNAMENT:
        p1 = getTournamentParent(populationList);
        p2 = getTournamentParent(populationList);
        break;
    }

    Genotype c1 = Genotype.crossOver(p1, p1, cut);
    Genotype c2 = Genotype.crossOver(p2, p2, cut);
    c1.setGeneration(currentGeneration + 1);
    c2.setGeneration(currentGeneration + 1);
    nextGeneration.add(c1);
    nextGeneration.add(c2);

    freeSpots -= 2;
  }

  private Genotype getTournamentParent(List<Genotype> populationList) {
    Random r = new Random();
    int limit = (int) (tornamentLimit * populationList.size());
    for (int i = 0; i < limit; i++) {
      Collections.swap(populationList, i, i + r.nextInt(populationList.size() - i));
    }
    return populationList.stream() //
        .limit(limit) //
        .max(Comparator.<Genotype>reverseOrder()).get();
  }

  private Genotype getSigmaScaledParent(List<Genotype> populationList) {
    double averageFitness = populationList.stream().mapToDouble(v -> v.fitness()).average().getAsDouble();
    double standardDeviation = standardDeviation(populationList);
    List<Double> weights = populationList.stream() //
        .map(v -> Math.random() * (1 + (v.fitness() - averageFitness) / 2 * standardDeviation)) //
        .collect(Collectors.toList());
    int index = weights.indexOf(Collections.max(weights));
    return populationList.remove(index);
  }

  public Genotype getFitnessProportionateParent(List<Genotype> populationList) {
    double sum = populationList.stream().mapToDouble(v -> v.fitness()).sum();
    List<Double> weights = populationList.stream() //
        .map(v -> Math.random() * v.fitness() / sum) //
        .collect(Collectors.toList());
    int index = weights.indexOf(Collections.max(weights));
    return populationList.remove(index);
  }

  private double standardDeviation(Collection<Genotype> population) {
    double M = 0.0;
    double S = 0.0;
    int k = 1;
    for (Genotype genotype : population) {
      double tmpM = M;
      M += (genotype.fitness() - tmpM) / k;
      S += (genotype.fitness() - tmpM) * (genotype.fitness() - M);
      k++;
    }
    return Math.sqrt(S / (k - 2));
  }

  public void mutate(double genomeMutationRate, double genotypeMutationRate) {
    List<Genotype> children = new ArrayList<>(currentPopulation);

    int numBitsToMutate = (int) Math.ceil(genomeMutationRate * currentPopulation.peek().size());
    int genotypesToMutate = (int) Math.ceil(genotypeMutationRate * maxPopulationSize);
    Random r = new Random();
    for (int i = 0; i < genotypesToMutate; i++) {
      Collections.swap(children, i, i + r.nextInt(children.size() - i));
    }
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
    currentPopulation.clear();
    currentPopulation.addAll(nextGeneration);
  }

  public void overProductionSelection() {
    currentPopulation.clear();
    currentPopulation.addAll(nextGeneration);
    while (currentPopulation.size() > maxPopulationSize) {
      currentPopulation.removeLast();
    }
  }

  public void mixingSelection() {
    currentPopulation.stream().sorted().limit(getAdultLimit()).forEach(v -> v.tagForRevival());
    currentPopulation.removeIf(v -> v.shouldDie());
    currentPopulation.addAll(nextGeneration);
    while (currentPopulation.size() > maxPopulationSize) {
      currentPopulation.removeLast();
    }
  }

  public void add(Genotype genotype) {
    currentPopulation.add(genotype);
  }

  public int getAdultLimit() {
    return (int) (mixingRate * maxPopulationSize);
  }

  public int getChildLimit() {
    return (int) ((1 - mixingRate) * maxPopulationSize);
  }

  public int getFreeSpots() {
    return freeSpots;
  }

  public Genotype getWorstGenotype() {
    return currentPopulation.peekLast();
  }

  public Genotype getBestGenotype() {
    return currentPopulation.peekFirst();
  }

  public int size() {
    return currentPopulation.size();
  }

  public int nextGenerationSize() {
    return nextGeneration.size();
  }

  public int getCurrentGeneration() {
    return currentGeneration;
  }

  @Override
  public String toString() {
    return String.format("Gen: %5d - Fitness (max/avg): %6.3f / %6.3f - SD: %6.3f > Genotype > %s",//
                         currentGeneration,
                         currentPopulation.stream().mapToDouble(v -> v.fitness()).max().getAsDouble(),
                         currentPopulation.stream().mapToDouble(v -> v.fitness()).average().getAsDouble(),
                         standardDeviation(currentPopulation), currentPopulation.peek());
  }
}
