package subsym.genetics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import subsym.PopulationList;
import subsym.genetics.adultselection.AdultSelection;
import subsym.genetics.matingselection.MatingSelection;

/**
 * Created by anon on 21.02.2015.
 */
public class Population {

  private final int maxPopulationSize;
  private final PopulationList currentPopulation;
  private PopulationList nextGeneration;

  private AdultSelection selectionMode;

  private int freeSpots = 0;
  private int currentGeneration = 0;

  public Population(int maxPopulationSize) {
    this.maxPopulationSize = maxPopulationSize;
    currentPopulation = new PopulationList();
  }

  public void selectAdults(AdultSelection selectionMode) {
    nextGeneration = new PopulationList();
    this.selectionMode = selectionMode;
    selectionMode.selectAdults(this);
  }

  public void crossOver(double crossOverRate, double cut, MatingSelection matingMode) {
    while (getFreeSpots() > 0) {
      crossOverSingle(crossOverRate, cut, matingMode);
    }
  }

  public void crossOver(double crossOverRate, MatingSelection matingMode) {
    while (getFreeSpots() > 0) {
      crossOverSingle(crossOverRate, Math.random(), matingMode);
    }
  }

  public void crossOverSingle(double crossOverRate, double cut, MatingSelection matingMode) {
    List<Genotype> populationList = new ArrayList<>(currentPopulation.stream().collect(Collectors.toList()));
    Genotype p1 = matingMode.selectNext(populationList);
    Genotype p2 = matingMode.selectNext(populationList);

    if (Math.random() < crossOverRate) {
      Genotype c1 = Genotype.crossOver(p1, p2, cut);
      Genotype c2 = Genotype.crossOver(p2, p1, cut);
      c1.setGeneration(currentGeneration + 1);
      c2.setGeneration(currentGeneration + 1);

      addToNextGeneration(c1);
      addToNextGeneration(c2);
    } else {
      addToNextGeneration(p1.copy());
      addToNextGeneration(p2.copy());
    }
  }

  private void addToNextGeneration(Genotype child) {
    if (freeSpots > 0 && nextGeneration.add(child)) {
      freeSpots -= 1;
    }
  }


  public static double standardDeviation(Collection<Genotype> population) {
    double M = 0.0;
    double S = 0.0;
    int k = 1;
    for (Genotype genotype : population) {
      double tmpM = M;
      double fitness = genotype.fitness();
      M += (fitness - tmpM) / k;
      S += (fitness - tmpM) * (fitness - M);
      k++;
    }
    double sd = Math.sqrt(S / (k - 2));
    return sd;
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
    selectionMode.cleanUp(this);
    currentGeneration++;
  }

  public void add(Genotype genotype) {
    currentPopulation.add(genotype);
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
    return String.format("Gen: %5d - Fitness (max/avg): %6.2f / %6.2f - SD: %6.2f > Genotype > %s",//
                         currentGeneration, getCurrentMaxFitness(), getCurrentAverageFitness(),
                         getCurrentStandardDeviation(), currentPopulation.peekBest());
  }

  public double getCurrentStandardDeviation() {
    return standardDeviation(currentPopulation.get());
  }

  public double getCurrentAverageFitness() {
    return currentPopulation.stream().mapToDouble(Genotype::fitness).average().getAsDouble();
  }

  public double getCurrentMaxFitness() {
    return currentPopulation.stream().mapToDouble(Genotype::fitness).max().getAsDouble();
  }

  public PopulationList getCurrent() {
    return currentPopulation;
  }

  public int getMaxPopulationSize() {
    return maxPopulationSize;
  }

  public void setFreeSpots(int freeSpots) {
    this.freeSpots = freeSpots;
  }

  public PopulationList getNextGeneration() {
    return nextGeneration;
  }
}
