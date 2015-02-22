package subsym.ga;

/**
 * Created by anon on 21.02.2015.
 */
public abstract class GeneticProblem {

  protected final MateSelection fitnessProportionate;
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
    this.fitnessProportionate = matingMode;
  }

  public int generations() {
    return population.getCurrentGeneration();
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public enum AdultSelection {
    FULL_TURNOVER, OVER_PRODUCTION, MIXING
  }

  public enum MateSelection {
    FITNESS_PROPORTIONATE, SIGMA_SCALING, TOURNAMENT, UNKNOWN
  }

  public abstract boolean solution();

  public void cleanUp() {
    getPopulation().cleanUp();
  }

  protected Population getPopulation() {
    return population;
  }

  public abstract void initPopulation();

  public void select() {
    population.selectAdults(adultSelectMode);
  }

  public void crossOver() {
    population.crossOver(crossOverRate);
  }

  public void mutate() {
    population.mutate(genomeMutationRate, genotypeMutationRate);
  }

}
