package subsym.genetics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import subsym.ann.ArtificialNeuralNetwork;
import subsym.genetics.adultselection.AdultSelection;
import subsym.genetics.matingselection.MatingSelection;

/**
 * Created by anon on 21.02.2015.
 */
public class Population {

  private static final String TAG = Population.class.getSimpleName();
  private final PopulationList currentPopulation;
  private final GeneticPreferences prefs;
  private PopulationList nextGeneration;

  private int currentGeneration = 0;
  private Random r = ArtificialNeuralNetwork.random();


  public Population(GeneticPreferences prefs) {
    this.prefs = prefs;
    currentPopulation = new PopulationList();
    nextGeneration = new PopulationList();
  }

  public void selectAdults() {
    prefs.getAdultSelectionMode().selectAdults(this);
    currentGeneration++;
    currentPopulation.stream().forEach(v -> v.setCurrentGeneration(currentGeneration));
    nextGeneration = new PopulationList();
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

  private int getFreeSpots() {
    return prefs.getAdultSelectionMode().getFreeSpots(this);
  }

  public void crossOverSingle(double crossOverRate, double cut, MatingSelection matingMode) {
    List<Genotype> populationList = new ArrayList<>(currentPopulation.stream().collect(Collectors.toList()));
    Genotype p1 = matingMode.selectNext(populationList);
    Genotype p2 = matingMode.selectNext(populationList);

    if (Math.random() < crossOverRate) {
      Genotype c1 = Genotype.crossOver(p1, p2, cut);
      Genotype c2 = Genotype.crossOver(p2, p1, cut);
      c1.setGenerationOrigin(currentGeneration + 1);
      c2.setGenerationOrigin(currentGeneration + 1);

      addToNextGeneration(c1);
      addToNextGeneration(c2);
    } else {
      Genotype p1Copy = p1.copy();
      Genotype p2Copy = p2.copy();
      addToNextGeneration(p1Copy);
      addToNextGeneration(p2Copy);
    }
  }

  private void addToNextGeneration(Genotype child) {
    nextGeneration.add(child);
  }


  public static double standardDeviation(List<Double> numbers) {
    double mean = numbers.stream().mapToDouble(i -> i).average().getAsDouble();
    double averagePowerMeans = numbers.stream().mapToDouble(i -> Math.pow(i - mean, 2)).average().getAsDouble();
    double sd = Math.sqrt(averagePowerMeans);
    return sd;
  }

  public void mutate(double populationMutationRate, double genotypeMutationRate) {
    List<Genotype> mutationCandidates = new ArrayList<>(currentPopulation.get());

//    int numBitsToMutate = (int) Math.ceil(genotypeMutationRate * currentPopulation.peekBest().size());
    int numIndividualsToMutate = (int) Math.ceil(populationMutationRate * currentPopulation.size());
    for (int i = 0; i < (numIndividualsToMutate); i++) {
      Collections.swap(mutationCandidates, i, r.nextInt(mutationCandidates.size() - i));
    }
    mutationCandidates.stream().limit(numIndividualsToMutate).map(Genotype::copy).forEach(v -> {
//      Log.v(TAG, "MUTATION ...");
//      String before = v.getPaddedBitString();
//      Log.v(TAG, before);
      v.mutate(prefs.shouldGaussian() ? applyGaussian(genotypeMutationRate) : genotypeMutationRate);
//      String after = v.getPaddedBitString();
//      Log.v(TAG, after);
//      Log.v(TAG, IntStream.range(0, v.size()).mapToObj(i -> before.charAt(i) == after.charAt(i) ? " " : "x").collect(Collectors.joining()));
      nextGeneration.add(v);
    });
  }

  private double applyGaussian(double rate) {
    double gaussian = r.nextGaussian() / 5;
    gaussian = gaussian > 0 ? gaussian * (1 - rate) : gaussian * -1 * rate;
    if (gaussian > 0 && gaussian < 1) {
      rate += gaussian;
    }
//    Log.v(TAG, String.format("Rate: %.5f - Gaussian: %.5f", rate, gaussian));
    return rate;
  }

  public void add(Genotype genotype) {
    currentPopulation.add(genotype);
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
    return String.format("Gen: %5d - Fitness (max/avg): %6.2f / %6.2f - SD: %6.2f > Geno > %s",//
                         currentGeneration, getCurrentMaxFitness(), getCurrentAverageFitness(), getCurrentStandardDeviation(),
                         currentPopulation.peekBest());
  }

  public double getCurrentStandardDeviation() {
    return standardDeviation(currentPopulation.get().stream().map(i -> i.fitness()).collect(Collectors.toList()));
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
    return prefs.getPopulationSize();
  }

  public PopulationList getNextGeneration() {
    return nextGeneration;
  }

  public void selectAdults(AdultSelection mode) {
    prefs.setAdultSelectionMode(mode);
    selectAdults();
  }
}
