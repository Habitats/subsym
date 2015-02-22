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

  public OneMax(int populationSize, int bitVectorSize) {
    this.populationSize = populationSize;
    this.bitVectorSize = bitVectorSize;
  }

  @Override
  public boolean solution() {
    return population.getBestGenotype().numOnes() == bitVectorSize;
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
    population.selectAdults(AdultSelection.FULL_TURNOVER);
  }

  @Override
  public void crossOver() {
    population.crossOver(0.5);
  }

  @Override
  public void mutate() {
    population.mutate(0.9, 0.7);
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
