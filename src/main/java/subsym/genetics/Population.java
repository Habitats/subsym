package subsym.genetics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import subsym.UniquePriorityQueue;

/**
 * Created by anon on 21.02.2015.
 */
public class Population {

  private final int maxPopulationSize;
  private final boolean ensureUnique;
  private final UniquePriorityQueue currentPopulation;
  private UniquePriorityQueue nextGeneration;

  private GeneticProblem.AdultSelection selectionMode;

  public static double mixingRate = 0.2;
  public static double overProductionRate = 2;
  public static double tournamentLimit = .2;
  private int freeSpots = 0;
  private int currentGeneration = 0;

  public Population(int maxPopulationSize, boolean ensureUnique) {
    this.maxPopulationSize = maxPopulationSize;
    this.ensureUnique = ensureUnique;
    currentPopulation = new UniquePriorityQueue(ensureUnique);
  }

  public void selectAdults(GeneticProblem.AdultSelection selectionMode) {
    nextGeneration = new UniquePriorityQueue(ensureUnique);
    this.selectionMode = selectionMode;
    switch (selectionMode) {
      case FULL_TURNOVER:
        currentPopulation.stream().forEach(Genotype::tagForRemoval);
        freeSpots = maxPopulationSize;
        break;
      case OVER_PRODUCTION:
        currentPopulation.stream().forEach(Genotype::tagForRemoval);
        freeSpots = (int) (maxPopulationSize * overProductionRate);
        break;
      case MIXING:
        currentPopulation.stream().forEach(Genotype::tagForRemoval);
        currentPopulation.stream().sorted().limit(getAdultLimit()).forEach(Genotype::tagForRemoval);
        freeSpots = maxPopulationSize - (int) currentPopulation.stream().filter(v -> !v.shouldDie()).count();
        break;
    }
  }

  public void crossOver(double crossOverRate, double cut, GeneticProblem.MateSelection matingMode) {
    while (getFreeSpots() > 0) {
      crossOverSingle(crossOverRate, cut, matingMode);
    }
  }

  public void crossOver(double crossOverRate, GeneticProblem.MateSelection matingMode) {
    while (getFreeSpots() > 0) {
      crossOverSingle(crossOverRate, Math.random(), matingMode);
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

    Genotype c1 = Genotype.crossOver(p1, p2, cut);
    Genotype c2 = Genotype.crossOver(p2, p1, cut);
    c1.setGeneration(currentGeneration + 1);
    c2.setGeneration(currentGeneration + 1);

    addToNextGeneration(c1);
    addToNextGeneration(c2);
  }

  private void addToNextGeneration(Genotype child) {
    if (freeSpots > 0 && nextGeneration.add(child)) {
      freeSpots -= 1;
    }
  }

  private Genotype getTournamentParent(List<Genotype> populationList) {
    Random r = new Random();
    int limit = (int) (tournamentLimit * populationList.size());
    for (int i = 0; i < limit; i++) {
      Collections.swap(populationList, i, i + r.nextInt(populationList.size() - i));
    }
    return populationList.stream() //
        .limit(limit) //
        .max(Comparator.<Genotype>reverseOrder()).get();
  }

  private Genotype getSigmaScaledParent(List<Genotype> populationList) {
    double averageFitness = populationList.stream().mapToDouble(Genotype::fitness).average().getAsDouble();
    double standardDeviation = standardDeviation(populationList);
    Function<Genotype, Double> toSigmaScale = v -> //
        Math.random() * (1 + (v.fitness() - averageFitness) / 2 * standardDeviation);
    List<Double> weights = populationList.stream().map(toSigmaScale).collect(Collectors.toList());
    int index = weights.indexOf(Collections.max(weights));
    return populationList.remove(index);
  }

  public Genotype getFitnessProportionateParent(List<Genotype> populationList) {
    double sum = populationList.stream().mapToDouble(Genotype::fitness).sum();
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

  public void mutate(double populationMutationRate, double genotypeMutationRate) {
    List<Genotype> children = new ArrayList<>(currentPopulation.get());

    int numBitsToMutate = (int) Math.ceil(genotypeMutationRate * currentPopulation.peekBest().size());
    int numIndividualsToMutate = (int) Math.ceil(populationMutationRate * (children.size()));
    Random r = new Random();
    for (int i = 0; i < (numIndividualsToMutate); i++) {
      Collections.swap(children, i, r.nextInt(children.size() - i));
    }
    children.stream() //
        .filter(v -> !v.shouldDie()) //
        .limit(numIndividualsToMutate) //
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

  private void mixingSelection() {
    removeBadAdults();
    addNextGeneration();
  }

  private void addNextGeneration() {
    currentPopulation.addAll(nextGeneration);
    while (currentPopulation.size() > maxPopulationSize) {
      currentPopulation.removeLast();
    }
  }

  private void removeBadAdults() {
    currentPopulation.stream().sorted().limit(getAdultLimit()).forEach(Genotype::tagForRevival);
    currentPopulation.removeIf(Genotype::shouldDie);
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
    return currentPopulation.peekWorst();
  }

  public Genotype getBestGenotype() {
    return currentPopulation.peekBest();
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
                         currentPopulation.stream().mapToDouble(Genotype::fitness).max().getAsDouble(),
                         currentPopulation.stream().mapToDouble(Genotype::fitness).average().getAsDouble(),
                         standardDeviation(currentPopulation.get()), currentPopulation.peekBest());
  }
}
