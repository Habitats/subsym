package subsym.onemax;

import subsym.Log;
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

  private static double crossOverRate = .8;
  private static double genotypeMutationRate = .02;
  private static double genomeMutationRate = .02;
  private static AdultSelection adultSelectionMode = AdultSelection.FULL_TURNOVER;

  public OneMax(int populationSize, int bitVectorSize) {
    this.populationSize = populationSize;
    this.bitVectorSize = bitVectorSize;
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
    Log.v(TAG, population.getBestGenotype());
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
}
