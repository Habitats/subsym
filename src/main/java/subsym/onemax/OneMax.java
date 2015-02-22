package subsym.onemax;

import subsym.ga.GeneticProblem;
import subsym.ga.Genotype;
import subsym.ga.Phenotype;
import subsym.ga.Population;

/**
 * Created by anon on 21.02.2015.
 */
public class OneMax extends GeneticProblem {

  private static final String TAG = GeneticProblem.class.getSimpleName();
  private final int populationSize;
  private int bitVectorSize;
  private Population population;

  private double crossOverRate = .8;
  private double genotypeMutationRate = .02;
  private double genomeMutationRate = .02;
  private AdultSelection adultSelectionMode = AdultSelection.OVER_PRODUCTION;

  public OneMax(int populationSize, int bitVectorSize, double crossOverRate, double genomeMutationRate,
                double genotypeMutationRate, AdultSelection adultSelectionMode) {
    this.populationSize = populationSize;
    this.bitVectorSize = bitVectorSize;
    this.crossOverRate = crossOverRate;
    this.genomeMutationRate = genomeMutationRate;
    this.genotypeMutationRate = genotypeMutationRate;
    this.adultSelectionMode = adultSelectionMode;
  }

  @Override
  public boolean solution() {
    return population.getBestGenotype().fitness() == bitVectorSize;
  }

  @Override
  public void initPopulation() {
    population = new Population(populationSize, bitVectorSize);
  }

  @Override
  public void evaluate() {
//    Log.v(TAG, population.getBestGenotype());
  }

  @Override
  public void select() {
    population.selectAdults(adultSelectionMode);
  }

  @Override
  public void crossOver() {
    population.crossOver(crossOverRate);
  }

  @Override
  public void mutate() {
    population.mutate(genomeMutationRate, genotypeMutationRate);
  }

  @Override
  protected Population getPopulation() {
    return population;
  }

  @Override
  public Phenotype generatePhenotype(Genotype genotype) {
    return null;
  }

  @Override
  public String toString() {
    return "Best: " + population.getBestGenotype().toString();
  }

  @Override
  public int generations() {
    return population.getCurrentGeneration();
  }
}
