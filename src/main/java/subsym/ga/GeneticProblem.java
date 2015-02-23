package subsym.ga;

import subsym.Log;

/**
 * Created by anon on 21.02.2015.
 */
public abstract class GeneticProblem {

  private static final String TAG = GeneticProblem.class.getSimpleName();

  public enum AdultSelection {
    FULL_TURNOVER, OVER_PRODUCTION, MIXING
  }

  public enum MateSelection {
    FITNESS_PROPORTIONATE, SIGMA_SCALING, TOURNAMENT, UNKNOWN
  }

  protected final MateSelection matingMode;
  private final int populationSize;
  private Population population;
  protected double crossOverRate = .8;
  protected double genotypeMutationRate = .02;
  protected double genomeMutationRate = .02;
  protected AdultSelection adultSelectMode = AdultSelection.OVER_PRODUCTION;

  public GeneticProblem(int populationSize, double crossOverRate, double genomeMutationRate,
                        double genotypeMutationRate, AdultSelection adultSelectMode, MateSelection matingMode) {
    this.genotypeMutationRate = genotypeMutationRate;
    this.crossOverRate = crossOverRate;
    this.genomeMutationRate = genomeMutationRate;
    this.adultSelectMode = adultSelectMode;
    this.populationSize = populationSize;
    population = new Population(populationSize);
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
    population.crossOver(crossOverRate, getCrossoverCut(), matingMode);
  }

  protected abstract double getCrossoverCut();

  public void mutate() {
    population.mutate(genomeMutationRate, genotypeMutationRate);
  }

  public void log() {
    Log.v(TAG, population);
  }

  public abstract void initPopulation();

  public abstract boolean solution();

  @Override
  public String toString() {
//    int l1 = Arrays.stream(AdultSelection.values()).mapToInt(v -> v.name().length()).max().getAsInt();
//    int l2 = Arrays.stream(MateSelection.values()).mapToInt(v -> v.name().length()).max().getAsInt();
    String l1 = "";
    String l2 = "";
    return String.format("CR: %.2f - GMR: %.2f IMR: %.2f - AS: %" + l1 + "s - MS: %" + l2 + "s > Population > %s", //
                         crossOverRate, genomeMutationRate, genotypeMutationRate, adultSelectMode, matingMode,
                         population);
  }
}
