package subsym.genetics;

import subsym.Log;
import subsym.genetics.adultselection.AdultSelection;
import subsym.genetics.matingselection.MatingSelection;

/**
 * Created by anon on 21.02.2015.
 */
public abstract class GeneticProblem {

  private static final String TAG = GeneticProblem.class.getSimpleName();

  protected final MatingSelection matingMode;
  private final int populationSize;
  private final AdultSelection adultSelectMode;
  private Population population;
  protected double crossOverRate = .8;
  protected double genotypeMutationRate = .02;
  protected double populationMutationRate = .02;

  public GeneticProblem(int populationSize, double crossOverRate, double genotypeMutationRate,
                        double populationMutationRate, AdultSelection adultSelectMode, MatingSelection matingMode,
                        boolean ensureUnique) {
    this.genotypeMutationRate = genotypeMutationRate;
    this.crossOverRate = crossOverRate;
    this.populationMutationRate = populationMutationRate;
    this.adultSelectMode = adultSelectMode;
    this.populationSize = populationSize;
    population = new Population(populationSize, ensureUnique);
    this.matingMode = matingMode;
  }

  public int generations() {
    return population.getCurrentGeneration();
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public void cleanUp() {
    getPopulation().cleanUp();
  }

  protected Population getPopulation() {
    return population;
  }

  public void select() {
    population.selectAdults(adultSelectMode);
  }

  public void crossOver() {
    population.crossOver(crossOverRate, matingMode);
  }

  protected abstract double getCrossoverCut();

  public void mutate() {
    population.mutate(populationMutationRate, genotypeMutationRate);
  }

  public void log() {
    Log.v(TAG, population);
  }

  public abstract void initPopulation();

  public abstract boolean solution();

  public String getId() {
    return String.format("CR: %.2f - GMR: %.2f IMR: %.2f - AS: %s - MS: %s", //
                         crossOverRate, populationMutationRate, genotypeMutationRate, adultSelectMode, matingMode);
  }

  @Override
  public String toString() {
//    int l1 = Arrays.stream(AdultSelection.values()).mapToInt(v -> v.name().length()).max().getAsInt();
//    int l2 = Arrays.stream(MateSelection.values()).mapToInt(v -> v.name().length()).max().getAsInt();
    String l1 = "";
    String l2 = "";
    return String.format("CR: %.2f - GMR: %.2f IMR: %.2f - AS: %" + l1 + "s - MS: %" + l2 + "s > Population > %s", //
                         crossOverRate, populationMutationRate, genotypeMutationRate, adultSelectMode, matingMode,
                         population);
  }
}
